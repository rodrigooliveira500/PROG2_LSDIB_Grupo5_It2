package utils;

import modelo.Enfermaria;
import modelo.Episodio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Fornece cálculos estatísticos simples.
 */
public class AnalisadorEstatistico {

    /**
     * Representa um resumo estatístico do LoS.
     */
    public static class SumarioLoS {

        /** Número de episódios considerados. */
        private int totalEpisodios;

        /** Média do LoS. */
        private double media;

        /** Desvio Padrão do LoS. */
        private double desvioPadrao;

        /** Valor mínimo do LoS. */
        private long minimo;

        /** Valor máximo do LoS. */
        private long maximo;

        /**
         * Cria um resumo de LoS.
         *
         * @param totalEpisodios número de episódios
         * @param media média
         * @param desvioPadrao desvio padrão
         * @param minimo mínimo
         * @param maximo máximo
         */
        public SumarioLoS(int totalEpisodios, double media, double desvioPadrao, long minimo, long maximo) {
            this.totalEpisodios = totalEpisodios;
            this.media = media;
            this.desvioPadrao = desvioPadrao;
            this.minimo = minimo;
            this.maximo = maximo;
        }

        /**
         * Devolve o número de episódios.
         *
         * @return número de episódios
         */
        public int getTotalEpisodios() {
            return totalEpisodios;
        }

        /**
         * Devolve a média.
         *
         * @return média
         */
        public double getMedia() {
            return media;
        }

        /**
         * Devolve o desvio padrão.
         *
         * @return desvio padrão
         */
        public double getDesvioPadrao() {
            return desvioPadrao;
        }

        /**
         * Devolve o mínimo.
         *
         * @return mínimo
         */
        public long getMinimo() {
            return minimo;
        }

        /**
         * Devolve o máximo.
         *
         * @return máximo
         */
        public long getMaximo() {
            return maximo;
        }


        /**
         * Retorna uma representação textual do resumo estatístico do LoS.
         *
         * @return string com total de episódios, média, desvio padrão, mínimo e máximo,
         * ou "Sem episódios com alta" se {@code totalEpisodios} for zero
         */
        @Override
        public String toString(){
            if (totalEpisodios == 0){
                return "Sem episódios com alta";
            }
            return String.format("Pacientes com alta=%d | media=%.2f | desvio padrao=%.2f | Minimo=%d | Máximo=%d",
                    totalEpisodios, media, desvioPadrao, minimo, maximo);
        }
    }

    /**
     * Calcula estatística de LoS de uma enfermaria.
     *
     * @param enfermaria enfermaria a analisar
     * @return resumo estatístico
     */
    public static SumarioLoS calculasEstatisticaLoS(Enfermaria enfermaria) {
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
     * Mostra a análise diária de pressão num intervalo.
     *
     * @param enfermaria enfermaria a analisar
     * @param dataInicio data inicial
     * @param dataFim data final
     */
    public static void analisarPressaoPorIntervalo(Enfermaria enfermaria, LocalDate dataInicio, LocalDate dataFim){
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)){
            System.out.println(" Intervalo inválido.");
            return;
        }

        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)){
            double taxa = enfermaria.getTaxaOcupacao(dataAtual);
            String estado = enfermaria.emPressao(dataAtual) ? "Em pressão" : "Estado normal";
            System.out.printf(" %s -> %s (%.1f%%)%n", dataAtual, estado, taxa);
            dataAtual = dataAtual.plusDays(1);
        }

        System.out.printf(" Dias em pressão: %.1f%%%n",
                enfermaria.getPercentagemDiasEmPressao(dataInicio, dataFim));

    }

    /**
     * Ordena uma lista de enfermarias por taxa de ocupação decrescente,
     * utilizando uma classe anónima como {@link Comparator}.
     *
     * @param enfermarias lista de enfermarias
     * @param data data de referência
     */
    public static void ordenarPorTaxaOcupacao(List<Enfermaria> enfermarias, LocalDate data) {
        enfermarias.sort(new Comparator<Enfermaria>() {
            /**
             * Compara duas enfermarias por taxa de ocupação decrescente.
             *
             * @param primeira primeira enfermaria
             * @param segunda segunda enfermaria
             * @return valor de comparação
             */
            @Override
            public int compare(Enfermaria primeira, Enfermaria segunda) {
                int comparacao = Double.compare(segunda.getTaxaOcupacao(data), primeira.getTaxaOcupacao(data));
                if (comparacao != 0) {
                    return comparacao;
                }
                return primeira.getIdentificador().compareToIgnoreCase(segunda.getIdentificador());
            }
        });
    }

    /**
     * Calcula o turnover de uma enfermaria numa data específica.
     * Fórmula: (admissões + altas) / camasTotais × 100
     *
     * @param enfermaria enfermaria a analisar
     * @param data       data de referência
     * @return percentagem de turnover; 0.0 se não houver camas
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
        return camas == 0 ? 0.0 : ((admissoes + altas) * 100.0) / camas;
    }

    /**
     * Calcula a percentagem de enfermarias com taxa de ocupação superior
     * a 85% numa data de referência (RF5).
     *
     * @param enfermarias lista de enfermarias a analisar
     * @param data        data de referência
     * @return percentagem (0–100) de enfermarias em pressão;
     *         0.0 se a lista for nula ou vazia
     */
    public static double percentagemEmPressao(List<Enfermaria> enfermarias, LocalDate data) {
        if (enfermarias == null || enfermarias.isEmpty()) {
            return 0.0;
        }
        int emPressao = 0;
        for (Enfermaria e : enfermarias) {
            if (e.emPressao(data)) {
                emPressao++;
            }
        }
        return (emPressao * 100.0) / enfermarias.size();
    }

    /**
     * Conta o número absoluto de enfermarias em pressão numa data.
     *
     * @param enfermarias lista de enfermarias
     * @param data        data de referência
     * @return número de enfermarias com ocupação superior a 85%
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

    /**
     * Calcula o score de ocupação (1-5) com base na taxa de ocupação.
     *
     * @param percOcup taxa de ocupação em percentagem
     * @return score de ocupação entre 1 e 5
     */
    private static int calcularScoreOcupacao(double percOcup) {
        if (percOcup <= 85) return 1;
        else if (percOcup <= 90) return 2;
        else if (percOcup <= 95) return 3;
        else if (percOcup <= 100) return 4;
        else return 5;
    }

    /**
     * Calcula o score de turnover (1-5) com base na percentagem de turnover.
     *
     * @param percTurnover percentagem de turnover
     * @return score de turnover entre 1 e 5
     */
    private static int calcularScoreTurnover(double percTurnover) {
        if (percTurnover <= 10) return 1;
        else if (percTurnover <= 20) return 2;
        else if (percTurnover <= 30) return 3;
        else if (percTurnover <= 40) return 4;
        else return 5;
    }

    /**
     * Interpreta o índice de pressão e devolve a classificação.
     *
     * @param indice índice de pressão calculado
     * @return classificação textual do índice
     */
    private static String interpretarIndice(double indice) {
        if (indice <= 2) return "Pressao Baixa";
        else if (indice <= 3.5) return "Pressao Moderada";
        else return "Pressao Alta";
    }
}

