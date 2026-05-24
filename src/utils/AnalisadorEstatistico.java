package utils;

import modelo.Enfermaria;

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
}

