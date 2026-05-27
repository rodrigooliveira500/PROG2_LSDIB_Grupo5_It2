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
        ordenadas.sort(Comparator.comparingDouble((Enfermaria e) -> e.getTaxaOcupacao(data)).reversed()
                .thenComparing(Enfermaria::getIdentificador));
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
