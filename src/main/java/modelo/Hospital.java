package modelo;

import utils.AnalisadorEstatistico;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Hospital implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private String nome;
    private List<Enfermaria> enfermarias;

    public Hospital(String nome) {
        this.nome = nome;
        this.enfermarias = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public boolean adicionarEnfermaria(Enfermaria enfermaria) {
        if (enfermaria == null || obterEnfermaria(enfermaria.getIdentificador()) != null) {
            return false;
        }
        enfermarias.add(enfermaria);
        return true;
    }

    public Enfermaria obterEnfermaria(String identificador) {
        for (Enfermaria enfermaria : enfermarias) {
            if (enfermaria.getIdentificador().equalsIgnoreCase(identificador)) {
                return enfermaria;
            }
        }
        return null;
    }

    public List<Enfermaria> getEnfermarias() {
        return new ArrayList<>(enfermarias);
    }

    public List<Enfermaria> listarEnfermariasOrdenadasPorTaxaOcupacao(LocalDate data) {
        return AnalisadorEstatistico.ordenarPorTaxaOcupacao(this.enfermarias, data);
    }

    @Override
    public String toString() {
        return String.format("%s | Enfermarias: %d", nome, enfermarias.size());
    }
}