package modelo;

/**
 * Representa uma enfermaria de cuidados intensivos.
 */
public class EnfermariaCuidadosIntensivos extends Enfermaria {

    /** Horário de visitas. */
    private String horarioVisitas;

    /** Pressão atmosférica observada. */
    private double pressaoAtmosferica;

    /** Pressão atmosférica de referência. */
    private double pressaoAtmosfericaReferencia;

    /**
     * Cria uma enfermaria de cuidados intensivos.
     *
     * @param id identificador da enfermaria
     * @param camas número total de camas
     * @param horarioVisitas horário de visitas
     * @param pressaoAtmosferica pressão atmosférica observada
     * @param pressaoAtmosfericaReferencia pressão atmosférica de referência.
    */
    public EnfermariaCuidadosIntensivos(String id, int camas, String horarioVisitas,
                                        double pressaoAtmosferica, double pressaoAtmosfericaReferencia) {
        super(id, camas);
        this.horarioVisitas = horarioVisitas;
        this.pressaoAtmosferica = pressaoAtmosferica;
        this.pressaoAtmosfericaReferencia = pressaoAtmosfericaReferencia;
    }

    /**
     * Devolve o horário das visitas.
     *
     * @return horario de visitas
     */
    public String getHorarioVisitas() {
            return horarioVisitas;
    }

    /**
     * Devolve a pressão atmosférica.
     *
     * @return pressão atmosférica
     */
    public double getPressaoAtmosferica(){
        return pressaoAtmosferica;
    }

    /**
     * Devolve a pressão atmosférica de referência
     *
     * @return pressão atmosférica de referência
     */
    public double getPressaoAtmosfericaReferencia() {
        return pressaoAtmosfericaReferencia;
    }

    /**
     * Devolve o desvio face à referência.
     *
     * @return diferença entre as duas pressões
     */
    public double getDesvioPressaoAtmosferica(){
        return pressaoAtmosferica - pressaoAtmosfericaReferencia;
    }

    /**
     * Devolve o tipo de enfermaria.
     *
     * @return tipo de enfermaria
     */
    @Override
    public String getTipoEnfermaria(){
        return "Cuidados Intensivos";
    }

    /**
     * Devolve uma representação textual da enfermaria.
     *
     * @return texto com os dados principais
     */
    @Override
    public String toString(){
        return String.format("%s | Visitas: %s | Pressão: %.2f | Referencia: %.2f",
                super.toString(), horarioVisitas, pressaoAtmosferica, pressaoAtmosfericaReferencia);
    }
}
