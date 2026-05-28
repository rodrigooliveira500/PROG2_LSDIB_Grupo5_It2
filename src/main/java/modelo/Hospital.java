package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Representa um hospital com várias enfermarias.
 */
public class Hospital {

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
