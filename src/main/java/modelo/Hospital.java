package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Representa um hospital com várias enfermarias.
 */
public class Hospital {

    private static final int ESCALA_BARRA = 50;

    /** Nome do hospital. */
    private String nome;

    /** Enfermarias do hospital. */
    private List<Enfermaria> enfermarias;

    /**
     * Cria um hospital.
     *
     * @param nome nome do hospital
     */
    public Hospital(String nome) {
        this.nome = nome;
        this.enfermarias = new ArrayList<>();
    }

    /**
     * Devolve o nome do hospital.
     *
     * @return nome do hospital
     */
    public String getNome() {
        return nome;
    }

    /**
     * Adiciona uma enfermaria ao hospital.
     *
     * @param enfermaria enfermaria a adicionar
     * @return {@code true} se foi adicionada
     */
    public boolean adicionarEnfermaria(Enfermaria enfermaria) {
        if (enfermaria == null || obterEnfermaria(enfermaria.getIdentificador()) != null) {
            return false;
        }
        enfermarias.add(enfermaria);
        return true;
    }

    /**
     * Procura uma enfermaria pelo identificador.
     *
     * @param identificador identificador da enfermaria
     * @return enfermaria encontrada, ou {@code null}
     */
    public Enfermaria obterEnfermaria(String identificador) {
        for (Enfermaria enfermaria : enfermarias) {
            if (enfermaria.getIdentificador().equalsIgnoreCase(identificador)) {
                return enfermaria;
            }
        }
        return null;
    }

    /**
     * Devolve as enfermarias do hospital.
     *
     * @return cópia da lista de enfermarias
     */
    public List<Enfermaria> getEnfermarias() {
        return new ArrayList<>(enfermarias);
    }

    /**
     * Lista as enfermarias ordenadas por taxa de ocupação.
     *
     * @param data data de referência
     * @return lista ordenada por taxa decrescente
     */
    public List<Enfermaria> listarEnfermariasOrdenadasPorTaxaOcupacao(LocalDate data) {
        List<Enfermaria> ordenadas = getEnfermarias();
        int n = ordenadas.size();

        /** Algoritmo Bubble Sort para ordenar as enfermarias */
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                Enfermaria enf1 = ordenadas.get(j);
                Enfermaria enf2 = ordenadas.get(j + 1);

                /** Ir buscar as taxas de ocupação para a data fornecida */
                double taxa1 = enf1.getTaxaOcupacao(data);
                double taxa2 = enf2.getTaxaOcupacao(data);

                /** Ir buscar os identificadores (para o desempate) */
                String id1 = enf1.getIdentificador();
                String id2 = enf2.getIdentificador();

                boolean trocar = false;

                /** Se a taxa do primeiro for MENOR que a do segundo, trocamos de sítio */
                if (taxa1 < taxa2) {
                    trocar = true;
                }
                /** Se as taxas forem iguais, desempatamos por ordem crescente do identificador */
                else if (taxa1 == taxa2) {
                    if (id1.compareTo(id2) > 0) {
                        trocar = true;
                    }
                }

                /** Fazer a troca na lista */
                if (trocar) {
                    ordenadas.set(j, enf2);
                    ordenadas.set(j + 1, enf1);
                }
            }
        }

        return ordenadas;
    }




    // RF3 — tabela alinhada com barras ASCII horizontais para uma enfermaria num intervalo
    public void exibirTabelaHorizontal(String idEnfermaria, LocalDate inicio, LocalDate fim, char simbolo) {
        Enfermaria enf = obterEnfermaria(idEnfermaria);
        if (enf == null) {
            System.out.println("[ERRO] Enfermaria nao encontrada.");
            return;
        }

        System.out.println();
        System.out.printf("%-14s %-12s %-8s %-10s %-9s %-10s  Grafico (50=%s)%n",
                "Enfermaria", "Data", "Ocupadas", "CamasTotais", "Ocup%", "Turnover%", "100%");
        System.out.println("-".repeat(110));

        LocalDate data = inicio;
        while (!data.isAfter(fim)) {
            int    ocupadas    = enf.getOcupacaoAbsoluta(data);
            int    camasTotais = enf.getNumeroCamas();
            double percOcup    = enf.getTaxaOcupacao(data);
            double percTurnover = enf.getPercTurnover(data);
            String barra       = gerarBarra(percOcup, simbolo);

            System.out.printf("%-14s %-12s %-8d %-11d %-9.1f %-10.1f  [%s]%n",
                    enf.getIdentificador(), data, ocupadas, camasTotais, percOcup, percTurnover, barra);

            data = data.plusDays(1);
        }
        System.out.println();
    }

    // RF8 — barras horizontais para todas as enfermarias numa data
    public void exibirBarrasHorizontais(LocalDate data, char simbolo) {
        System.out.println();
        System.out.printf("%-14s %-12s  Ocupacao (50=%s)%n", "Enfermaria", "Data", "100%");
        System.out.println("-".repeat(80));

        for (Enfermaria enf : enfermarias) {
            double percOcup = enf.getTaxaOcupacao(data);
            System.out.printf("%-14s %-12s [%s] %.1f%%%n",
                    enf.getIdentificador(), data, gerarBarra(percOcup, simbolo), percOcup);
        }
        System.out.println();
    }

    // RF8 — barras verticais para todas as enfermarias numa data
    public void exibirBarrasVerticais(LocalDate data, char simbolo) {
        if (enfermarias.isEmpty()) {
            System.out.println("  Sem enfermarias para exibir.");
            return;
        }

        int alturaMaxima = 20;
        System.out.println("\n  Grafico Vertical — Ocupacao em " + data + "\n");

        for (int linha = alturaMaxima; linha >= 1; linha--) {
            double limiar = (linha * 100.0) / alturaMaxima;
            if (linha % 4 == 0 || linha == alturaMaxima)
                System.out.printf("%5.0f%% |", limiar);
            else
                System.out.print("       |");

            for (Enfermaria enf : enfermarias) {
                if (enf.getTaxaOcupacao(data) >= limiar)
                    System.out.printf("  %c  ", simbolo);
                else
                    System.out.print("     ");
            }
            System.out.println();
        }

        System.out.print("    0% +");
        System.out.print("-----".repeat(enfermarias.size()));
        System.out.println();

        System.out.print("        ");
        for (Enfermaria enf : enfermarias) {
            String id = enf.getIdentificador();
            System.out.printf("%-5s", id.length() > 5 ? id.substring(0, 5) : id);
        }
        System.out.println("\n");
    }

    // gera uma barra ASCII com ESCALA_BARRA caracteres proporcional à percentagem
    private String gerarBarra(double percentagem, char simbolo) {
        int n = (int) Math.round((percentagem / 100.0) * ESCALA_BARRA);
        if (n > ESCALA_BARRA) n = ESCALA_BARRA;
        if (n < 0) n = 0;

        String barra = "";
        for (int i = 0; i < n; i++)
            barra += simbolo;
        for (int i = n; i < ESCALA_BARRA; i++)
            barra += " ";
        return barra;
    }

    public void substituirDados(Hospital outro) {
        this.nome = outro.nome;
        this.enfermarias = outro.enfermarias;
    }


    /**
     * Devolve uma representação textual do hospital.
     *
     * @return texto com o nome e o número de enfermarias
     */

    @Override
    public String toString() {
        return String.format("%s | Enfermarias: %d", nome, enfermarias.size());
    }
}
}
