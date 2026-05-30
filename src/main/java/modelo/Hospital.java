package modelo;

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
        List<Enfermaria> ordenadas = getEnfermarias();
        int n = ordenadas.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Enfermaria enf1 = ordenadas.get(j);
                Enfermaria enf2 = ordenadas.get(j + 1);

                double taxa1 = enf1.getTaxaOcupacao(data);
                double taxa2 = enf2.getTaxaOcupacao(data);

                boolean trocar = taxa1 < taxa2
                        || (taxa1 == taxa2 && enf1.getIdentificador().compareTo(enf2.getIdentificador()) > 0);

                if (trocar) {
                    ordenadas.set(j, enf2);
                    ordenadas.set(j + 1, enf1);
                }
            }
        }
        return ordenadas;
    }



    public void substituirDados(Hospital outro) {
        this.nome = outro.nome;
        this.enfermarias = outro.enfermarias;
    }

    @Override
    public String toString() {
        return String.format("%s | Enfermarias: %d", nome, enfermarias.size());
    }
}