package main;

import modelo.Enfermaria;
import utils.AnalisadorEstatistico;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe responsável pela apresentação visual de dados no ecrã.
 *
 * <p>Implementa as funcionalidades RF3, RF5 e RF8 do enunciado.</p>
 *
 * @author Grupo 5
 * @version 1.0
 */
public class Visualizador {

    /** Altura máxima em linhas do gráfico vertical. */
    private static final int ALTURA_GRAFICO_VERTICAL = 20;

    /** * Largura fixa (número de caracteres) utilizada para representar 100%
     * nas barras ASCII.
     */
    private static final int TAMANHO_BARRA_ASCII = 50;

    /** Construtor privado — classe utilitária, não deve ser instanciada. */
    private Visualizador() {}

    /**
     * Repete um caracter n vezes.
     *
     * @param c caracter a repetir
     * @param n número de repetições
     * @return string resultante
     */
    private static String repetir(char c, int n) {
        if (n <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Trunca uma string ao comprimento máximo indicado.
     *
     * @param texto    string a truncar
     * @param maxChars comprimento máximo
     * @return string truncada
     */
    private static String truncar(String texto, int maxChars) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= maxChars ? texto : texto.substring(0, maxChars);
    }

    /**
     * Devolve o valor máximo de uma lista de doubles.
     *
     * @param valores lista de valores
     * @return valor máximo; 0.0 se a lista for vazia
     */
    private static double obterMaximo(List<Double> valores) {
        if (valores == null || valores.isEmpty()) {
            return 0.0;
        }
        double max = valores.get(0);
        for (double v : valores) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    /**
     * Constrói uma barra ASCII proporcional a uma percentagem.
     * 50 caracteres = 100%.
     *
     * @param percentagem valor em percentagem (0–100)
     * @param simbolo     caracter de preenchimento
     * @return string com a barra formatada
     */
    private static String construirBarra(double percentagem, char simbolo) {
        double percLimitada;
        if (percentagem > 100.0) {
            percLimitada = 100.0;
        } else {
            percLimitada = percentagem;
        }
        
        int comprimento = (int) (percLimitada / 100.0 * TAMANHO_BARRA_ASCII);
        
        String preenchimento = repetir(simbolo, comprimento);
        String espacos = repetir(' ', TAMANHO_BARRA_ASCII - comprimento);
        
        return preenchimento + espacos;
    }

    /**
     * Apresenta tabela alinhada com indicadores diários de ocupação
     * de uma enfermaria num intervalo de datas (RF3).
     * Inclui barras ASCII horizontais com escala de 50 caracteres = 100%.
     *
     * @param enfermaria enfermaria a analisar
     * @param dataInicio data de início do intervalo (inclusive)
     * @param dataFim    data de fim do intervalo (inclusive)
     * @param simbolo    símbolo a utilizar no desenho da barra
     */
    public static void mostrarTabelaOcupacao(Enfermaria enfermaria,
                                             LocalDate dataInicio,
                                             LocalDate dataFim,
                                             char simbolo) {
        if (enfermaria == null || dataInicio == null || dataFim == null) {
            System.out.println("[ERRO] Parâmetros inválidos para a tabela de ocupação.");
            return;
        }
        if (dataInicio.isAfter(dataFim)) {
            System.out.println("[ERRO] A data de início não pode ser posterior à data de fim.");
            return;
        }

        System.out.println();
        System.out.println("Tabela de Ocupação — " + enfermaria.getIdentificador()
                + " (" + dataInicio + " a " + dataFim + ")");
        System.out.println("-".repeat(110));
        System.out.printf("%-12s %-10s %-8s %-11s %-8s %-10s  Barra (escala: 50 = 100%%)%n",
                "Enfermaria", "Data", "Ocup.", "Camas Tot.", "%Ocup", "Turnover%");
        System.out.println("-".repeat(110));

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            int    ocupadas = enfermaria.getOcupacaoAbsoluta(dataAtual);
            int    camas    = enfermaria.getNumeroCamas();
            double percOcup = enfermaria.getTaxaOcupacao(dataAtual);
            double turnover = AnalisadorEstatistico.calcularTurnover(enfermaria, dataAtual);
            String barra    = construirBarra(percOcup, simbolo);

            System.out.printf("%-12s %-10s %-8d %-11d %-8.1f %-10.1f  [%s]%n",
                    enfermaria.getIdentificador(),
                    dataAtual,
                    ocupadas,
                    camas,
                    percOcup,
                    turnover,
                    barra);

            dataAtual = dataAtual.plusDays(1);
        }
        System.out.println("-".repeat(110));
    }

    /**
     * Apresenta tabela de ocupação para várias enfermarias numa única data.
     *
     * @param enfermarias lista de enfermarias a apresentar
     * @param data        data de referência
     * @param simbolo     símbolo a utilizar no desenho da barra
     */
    public static void mostrarTabelaOcupacaoMultipla(List<Enfermaria> enfermarias,
                                                     LocalDate data,
                                                     char simbolo) {
        if (enfermarias == null || enfermarias.isEmpty()) {
            System.out.println("Nenhuma enfermaria disponível.");
            return;
        }

        System.out.println();
        System.out.println("Tabela de Ocupação — " + data);
        
        System.out.println(repetir('=', 110));
        System.out.printf("%-12s %-10s %-8s %-11s %-8s %-10s  Barra (escala: 50 = 100%%)%n",
                "Enfermaria", "Data", "Ocup.", "Camas Tot.", "%Ocup", "Turnover%");
        System.out.println(repetir('=', 110));

        for (Enfermaria enfermaria : enfermarias) {
            int    ocupadas = enfermaria.getOcupacaoAbsoluta(data);
            int    camas    = enfermaria.getNumeroCamas();
            double percOcup = enfermaria.getTaxaOcupacao(data);
            double turnover = AnalisadorEstatistico.calcularTurnover(enfermaria, data);
            String barra    = construirBarra(percOcup, simbolo);

            System.out.printf("%-12s %-10s %-8d %-11d %-8.1f %-10.1f  [%s]%n",
                    enfermaria.getIdentificador(),
                    data,
                    ocupadas,
                    camas,
                    percOcup,
                    turnover,
                    barra);
        }
        System.out.println(repetir('=', 110));
    }

    // ===================================================================
    // RF5 — Percentagem de enfermarias em pressão
    // ===================================================================

    /**
     * Apresenta a percentagem de enfermarias em pressão
     * (ocupação > 85%) numa data de referência, com detalhe por enfermaria (RF5).
     *
     * @param enfermarias lista de enfermarias
     * @param data        data de referência
     */
    public static void mostrarPercentagemEmPressao(List<Enfermaria> enfermarias,
                                                   LocalDate data) {
        if (enfermarias == null || enfermarias.isEmpty()) {
            System.out.println("Nenhuma enfermaria registada.");
            return;
        }

        double percentagem = AnalisadorEstatistico.percentagemEmPressao(enfermarias, data);
        int    emPressao   = AnalisadorEstatistico.contarEmPressao(enfermarias, data);
        int    total       = enfermarias.size();

        System.out.println();
        System.out.println(repetir('=', 60));
        System.out.println("  Enfermarias em Pressão — " + data);
        System.out.println(repetir('=', 60));
        System.out.printf("  Em pressão (ocup. > %.0f%%): %d de %d  ->  %.1f%%%n",
                AnalisadorEstatistico.LIMIAR_PRESSAO, emPressao, total, percentagem);
        System.out.println();
        System.out.println("  Detalhe:");
        for (Enfermaria e : enfermarias) {
            double taxa = e.getTaxaOcupacao(data);
            
            String estado;
            if (e.emPressao(data)) {
                estado = "EM PRESSAO  !";
            } else {
                estado = "Normal";
            }
            
            System.out.printf("    %-12s  %5.1f%%  %s%n",
                    e.getIdentificador(), taxa, estado);
        }
        System.out.println(repetir('=', 60));
    }

    /**
     * Apresenta um gráfico de barras horizontal (RF8).
     * A barra mais longa corresponde ao valor máximo e ocupa 50 caracteres.
     *
     * @param rotulos lista de rótulos, um por barra
     * @param valores lista de valores correspondentes
     * @param simbolo símbolo a usar no preenchimento
     */
    public static void mostrarGraficoHorizontal(List<String> rotulos,
                                                List<Double> valores,
                                                char simbolo) {
        if (rotulos == null || valores == null
                || rotulos.size() != valores.size() || rotulos.isEmpty()) {
            System.out.println("[ERRO] Dados inválidos para o gráfico horizontal.");
            return;
        }

        System.out.println();
        System.out.println("--- Gráfico de Barras Horizontal ---");
        System.out.println();

        double max = obterMaximo(valores);
        if (max <= 0) {
            max = 1.0;
        }

        for (int i = 0; i < rotulos.size(); i++) {
            double valor       = valores.get(i);
            int    comprimento = (int) ((valor / max) * TAMANHO_BARRA_ASCII);
            if (valor > 0 && comprimento == 0) {
                comprimento = 1;
            }
            String barra   = repetir(simbolo, comprimento);
            String espacos = repetir(' ', TAMANHO_BARRA_ASCII - comprimento);

            System.out.printf("%-15s [%s%s]  %.2f%n",
                    truncar(rotulos.get(i), 15), barra, espacos, valor);
        }
        System.out.println();
    }

    /**
     * Apresenta um gráfico de barras vertical (RF8).
     * Altura máxima de 20 linhas; cada coluna ocupa 5 caracteres de largura.
     *
     * @param rotulos lista de rótulos, um por barra
     * @param valores lista de valores correspondentes
     * @param simbolo símbolo a usar no preenchimento
     */
    public static void mostrarGraficoVertical(List<String> rotulos,
                                              List<Double> valores,
                                              char simbolo) {
        if (rotulos == null || valores == null
                || rotulos.size() != valores.size() || rotulos.isEmpty()) {
            System.out.println("[ERRO] Dados inválidos para o gráfico vertical.");
            return;
        }

        System.out.println();
        System.out.println("--- Gráfico de Barras Vertical ---");
        System.out.println();

        double max = obterMaximo(valores);
        if (max <= 0) {
            max = 1.0;
        }

        int[] alturas = new int[valores.size()];
        for (int i = 0; i < valores.size(); i++) {
            alturas[i] = (int) ((valores.get(i) / max) * ALTURA_GRAFICO_VERTICAL);
            if (valores.get(i) > 0 && alturas[i] == 0) {
                alturas[i] = 1;
            }
        }

        for (int linha = ALTURA_GRAFICO_VERTICAL; linha >= 1; linha--) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < alturas.length; i++) {
                if (alturas[i] >= linha) {
                    sb.append("  ").append(simbolo).append("  ");
                } else {
                    sb.append("     ");
                }
            }
            System.out.println(sb.toString());
        }

        System.out.println(repetir('-', rotulos.size() * 5));

        StringBuilder rotulosLinha = new StringBuilder();
        for (String r : rotulos) {
            rotulosLinha.append(String.format("%-5s", truncar(r, 4)));
        }
        System.out.println(rotulosLinha.toString());

        StringBuilder valoresLinha = new StringBuilder();
        for (double v : valores) {
            valoresLinha.append(String.format("%-5s", truncar(String.format("%.0f", v), 4)));
        }
        System.out.println(valoresLinha.toString());
        System.out.println();
    }

    // ===================================================================
    // MÉTODOS ADICIONAIS PARA INTEGRAR A LÓGICA DE APRESENTAÇÃO
    // ===================================================================

    /**
     * Imprime a monitorização diária do estado de pressão de uma unidade.
     * (Movido do AnalisadorEstatistico para o Visualizador para cumprir SRP).
     */
    public static void mostrarAnalisePressaoIntervalo(Enfermaria enfermaria, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            System.out.println(" Intervalo inválido.");
            return;
        }

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            double taxa = enfermaria.getTaxaOcupacao(dataAtual);
            String estado = enfermaria.emPressao(dataAtual) ? "Em pressão" : "Estado normal";

            System.out.printf(" %s -> %s (%.1f%%)%n", dataAtual, estado, taxa);
            dataAtual = dataAtual.plusDays(1);
        }

        System.out.printf(" Dias em pressão: %.1f%%%n", enfermaria.getPercentagemDiasEmPressao(dataInicio, dataFim));
    }

    /**
     * Desenha a tabela com a classificação e ranking do Índice de Pressão (RF6).
     * (Movido do AnalisadorEstatistico para o Visualizador para cumprir SRP).
     */
    public static void mostrarRankingIndicePressao(List<Enfermaria> enfermarias, LocalDate data) {
        List<Enfermaria> ordenadas = AnalisadorEstatistico.ordenarPorIndiceDePressao(enfermarias, data);

        System.out.println("\n=== Ranking Indice de Pressao em " + data + " ===");
        System.out.printf("%-5s %-10s %-8s %-8s %-8s %-8s %-20s%n",
                "Pos", "Enfermaria", "Ocup%", "Turnover%", "ScOcup", "ScTurn", "Indice | Classificacao");
        System.out.println(repetir('-', 75));

        for (int i = 0; i < ordenadas.size(); i++) {
            Enfermaria enf = ordenadas.get(i);

            double percOcup = enf.getTaxaOcupacao(data);
            double percTurnover = AnalisadorEstatistico.calcularTurnover(enf, data);

            int scoreOcup = AnalisadorEstatistico.calcularScoreOcupacao(percOcup);
            int scoreTurnover = AnalisadorEstatistico.calcularScoreTurnover(percTurnover);
            double indice = AnalisadorEstatistico.calcularIndiceDePressao(enf, data);

            System.out.printf("%-5d %-10s %-8.1f %-8.1f %-8d %-8d %.1f | %s%n",
                    i + 1, enf.getIdentificador(), percOcup, percTurnover, scoreOcup, scoreTurnover,
                    indice, AnalisadorEstatistico.interpretarIndice(indice));
        }
    }
}
