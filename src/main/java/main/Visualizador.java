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

    /** Número de caracteres que representa 100% na barra ASCII. */
    private static final int LARGURA_BARRA = 50;

    /** Altura máxima em linhas do gráfico vertical. */
    private static final int ALTURA_GRAFICO_VERTICAL = 20;

    /** Símbolo utilizado por omissão. */
    public static final char SIMBOLO_PADRAO = '#';

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
        
        int comprimento = (int) (percLimitada / 100.0 * LARGURA_BARRA);
        
        String preenchimento = repetir(simbolo, comprimento);
        String espacos = repetir(' ', LARGURA_BARRA - comprimento);
        
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
     */
    public static void mostrarTabelaOcupacao(Enfermaria enfermaria,
                                             LocalDate dataInicio,
                                             LocalDate dataFim) {
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
            String barra    = construirBarra(percOcup, SIMBOLO_PADRAO);

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
     */
    public static void mostrarTabelaOcupacaoMultipla(List<Enfermaria> enfermarias,
                                                     LocalDate data) {
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
            String barra    = construirBarra(percOcup, SIMBOLO_PADRAO);

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
}
