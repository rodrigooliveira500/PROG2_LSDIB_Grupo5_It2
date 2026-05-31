package utils;

import modelo.Enfermaria;
import modelo.Episodio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe responsável por fornecer cálculos estatísticos e indicadores hospitalares.
 * Segue estritamente o Princípio da Responsabilidade Única (SRP): não efetua
 * impressões na consola, tratando apenas do processamento de dados matemáticos.
 *
 * @author Grupo 5
 * @version 2.0
 */
public class AnalisadorEstatistico {

    // Constantes de Ocupação
    /** Limiar percentual a partir do qual uma enfermaria é considerada em pressão. */
    public static final double LIMIAR_PRESSAO = 85.0;
    private static final double LIMIAR_OCUPACAO_NIVEL_2 = 90.0;
    private static final double LIMIAR_OCUPACAO_NIVEL_3 = 95.0;
    private static final double LIMIAR_OCUPACAO_NIVEL_4 = 100.0;

    private static final double LIMIAR_TURNOVER_NIVEL_1 = 10.0;
    private static final double LIMIAR_TURNOVER_NIVEL_2 = 20.0;
    private static final double LIMIAR_TURNOVER_NIVEL_3 = 30.0;
    private static final double LIMIAR_TURNOVER_NIVEL_4 = 40.0;

    private static final double LIMIAR_INDICE_BAIXA = 2.0;
    private static final double LIMIAR_INDICE_MODERADA = 3.5;

    /**
     * Classe interna utilitária para encapsular o resumo estatístico do Length of Stay (LoS).
     */
    public static class SumarioLoS {
        private int totalEpisodios;
        private double media;
        private double desvioPadrao;
        private long minimo;
        private long maximo;

        /**
         * Cria um resumo estatístico estruturado do LoS.
         *
         * @param totalEpisodios número de episódios
         * @param media média dos tempos de internamento
         * @param desvioPadrao desvio padrão calculado
         * @param minimo valor mínimo de internamento
         * @param maximo valor máximo de internamento
         */
        public SumarioLoS(int totalEpisodios, double media, double desvioPadrao, long minimo, long maximo) {
            this.totalEpisodios = totalEpisodios;
            this.media = media;
            this.desvioPadrao = desvioPadrao;
            this.minimo = minimo;
            this.maximo = maximo;
        }

        /** @return número de episódios avaliados */
        public int getTotalEpisodios() { return totalEpisodios; }
        /** @return média aritmética do LoS */
        public double getMedia() { return media; }
        /** @return desvio padrão do LoS */
        public double getDesvioPadrao() { return desvioPadrao; }
        /** @return valor mínimo registado */
        public long getMinimo() { return minimo; }
        /** @return valor máximo registado */
        public long getMaximo() { return maximo; }

        /**
         * Retorna uma representação textual estruturada do resumo estatístico do LoS.
         *
         * @return string formatada com os indicadores descritivos
         */
        @Override
        public String toString() {
            if (totalEpisodios == 0) {
                return "Sem episódios com alta";
            }
            return String.format("Pacientes com alta=%d | media=%.2f | desvio padrao=%.2f | Minimo=%d | Máximo=%d",
                    totalEpisodios, media, desvioPadrao, minimo, maximo);
        }
    }

    /**
     * Calcula as estatísticas descritivas do Length of Stay (LoS) de uma enfermaria.
     *
     * @param enfermaria a enfermaria a analisar
     * @return objeto SumarioLoS preenchido com as métricas calculadas
     */
    public static SumarioLoS calcularEstatisticaLoS(Enfermaria enfermaria) {
        List<Long> valores = enfermaria.getValoresLoS();
        if (valores.isEmpty()) {
            return new SumarioLoS(0, 0.0, 0.0, 0, 0);
        }

        long minimo = valores.get(0);
        long maximo = valores.get(0);
        double soma = 0.0;
        for (Long valor : valores) {
            soma += valor;
            if (valor < minimo) minimo = valor;
            if (valor > maximo) maximo = valor;
        }

        double media = soma / valores.size();
        double somaQuadrados = 0.0;
        for (Long valor : valores) {
            double diferenca = valor - media;
            somaQuadrados += diferenca * diferenca;
        }
        double desvioPadrao = Math.sqrt(somaQuadrados / valores.size());

        return new SumarioLoS(valores.size(), media, desvioPadrao, minimo, maximo);
    }

    /**
     * Ordena uma cópia da lista de enfermarias por taxa de ocupação decrescente (e por ID em caso de empate).
     *
     * @param enfermarias lista de enfermarias a ordenar
     * @param data        data de referência para a taxa de ocupação
     * @return nova lista de enfermarias ordenada
     */
    public static List<Enfermaria> ordenarPorTaxaOcupacao(List<Enfermaria> enfermarias, LocalDate data) {
        List<Enfermaria> ordenadas = new ArrayList<>(enfermarias);

        ordenadas.sort(new Comparator<Enfermaria>() {
            @Override
            public int compare(Enfermaria primeira, Enfermaria segunda) {
                int comparacao = Double.compare(segunda.getTaxaOcupacao(data), primeira.getTaxaOcupacao(data));

                if (comparacao != 0) {
                    return comparacao;
                }

                String id1 = primeira.getIdentificador().toUpperCase();
                String id2 = segunda.getIdentificador().toUpperCase();
                return id1.compareTo(id2);
            }
        });

        return ordenadas;
    }

    /**
     * Calcula o turnover de uma enfermaria numa determinada data.
     * Fórmula: ((admissões + altas) * 100.0) / camasTotais
     *
     * @param enfermaria a enfermaria sob análise
     * @param data       a data de referência
     * @return a percentagem de turnover obtida (0.0 se não houver camas)
     */
    public static double calcularTurnover(Enfermaria enfermaria, LocalDate data) {
        if (enfermaria == null || data == null) {
            return 0.0;
        }
        int admissoes = 0;
        int altas     = 0;
        for (Episodio ep : enfermaria.getEpisodios()) {
            if (ep.getDataAdmissao().equals(data)) {
                admissoes++;
            }
            if (ep.temAlta() && ep.getDataAlta().equals(data)) {
                altas++;
            }
        }
        int camas = enfermaria.getNumeroCamas();
        if (camas == 0) {
            return 0.0;
        }
        return ((admissoes + altas) * 100.0) / camas;
    }

    /**
     * Determina a percentagem global de enfermarias que se encontram em situação de pressão.
     *
     * @param enfermarias listagem total do hospital
     * @param data        data sob avaliação
     * @return percentagem de unidades em pressão (0.0 a 100.0)
     */
    public static double percentagemEmPressao(List<Enfermaria> enfermarias, LocalDate data) {
        if (enfermarias == null || enfermarias.isEmpty()) {
            return 0.0;
        }
        int emPressao = contarEmPressao(enfermarias, data);
        return (emPressao * 100.0) / enfermarias.size();
    }

    /**
     * Conta o número absoluto de enfermarias em situação de pressão numa data.
     *
     * @param enfermarias listagem de enfermarias
     * @param data        data de referência
     * @return número total de enfermarias com ocupação superior ao limiar
     */
    public static int contarEmPressao(List<Enfermaria> enfermarias, LocalDate data) {
        if (enfermarias == null) {
            return 0;
        }
        int count = 0;
        for (Enfermaria e : enfermarias) {
            if (e.emPressao(data)) {
                count++;
            }
        }
        return count;
    }

    // MOTORES DE CÁLCULO E ESCALAS DO ÍNDICE DE PRESSÃO - RF6

    /**
     * Calcula o score de ocupação com base na taxa de ocupação.
     *
     * @param percOcup taxa de ocupação em percentagem
     * @return score de ocupação
     */
    public static int calcularScoreOcupacao(double percOcup) {
        if (percOcup <= LIMIAR_PRESSAO) return 1;
        else if (percOcup <= LIMIAR_OCUPACAO_NIVEL_2) return 2;
        else if (percOcup <= LIMIAR_OCUPACAO_NIVEL_3) return 3;
        else if (percOcup <= LIMIAR_OCUPACAO_NIVEL_4) return 4;
        else return 5;
    }

    /**
     * Calcula o score de turnover com base na percentagem de turnover.
     *
     * @param percTurnover percentagem de turnover
     * @return score de turnover
     */
    public static int calcularScoreTurnover(double percTurnover) {
        if (percTurnover <= LIMIAR_TURNOVER_NIVEL_1) return 1;
        else if (percTurnover <= LIMIAR_TURNOVER_NIVEL_2) return 2;
        else if (percTurnover <= LIMIAR_TURNOVER_NIVEL_3) return 3;
        else if (percTurnover <= LIMIAR_TURNOVER_NIVEL_4) return 4;
        else return 5;
    }

    /**
     * Interpreta o índice de pressão e devolve a respetiva classificação textual.
     *
     * @param indice índice de pressão calculado
     * @return classificação textual do estado de pressão
     */
    public static String interpretarIndice(double indice) {
        if (indice <= LIMIAR_INDICE_BAIXA) return "Pressao Baixa";
        else if (indice <= LIMIAR_INDICE_MODERADA) return "Pressao Moderada";
        else return "Pressao Alta";
    }

    /**
     * Calcula o valor individual ponderado do Índice de Pressão para uma enfermaria.
     *
     * @param enf  enfermaria sob avaliação
     * @param data data de referência para os cálculos
     * @return valor decimal do índice arredondado a uma casa decimal
     */
    public static double calcularIndiceDePressao(Enfermaria enf, LocalDate data) {
        double percOcup = enf.getTaxaOcupacao(data);
        double percTurnover = calcularTurnover(enf, data);

        int scoreOcup = calcularScoreOcupacao(percOcup);
        int scoreTurnover = calcularScoreTurnover(percTurnover);

        return Math.round((0.7 * scoreOcup + 0.3 * scoreTurnover) * 10.0) / 10.0;
    }

    /**
     * Gera uma cópia da lista ordenada de forma decrescente pelo Índice de Pressão.
     *
     * @param enfermarias listagem original
     * @param data        data de referência
     * @return nova lista reordenada pelo índice de pressão
     */
    public static List<Enfermaria> ordenarPorIndiceDePressao(List<Enfermaria> enfermarias, LocalDate data) {
        List<Enfermaria> ordenadas = new ArrayList<>(enfermarias);

        ordenadas.sort(new Comparator<Enfermaria>() {
            @Override
            public int compare(Enfermaria e1, Enfermaria e2) {
                double ind1 = calcularIndiceDePressao(e1, data);
                double ind2 = calcularIndiceDePressao(e2, data);
                return Double.compare(ind2, ind1);
            }
        });

        return ordenadas;
    }
}