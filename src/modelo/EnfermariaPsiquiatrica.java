package modelo;

/**
 * Representa uma enfermaria psiquiátrica, com controlo de horário de visitas e nível de segurança.
 * Estende {@link Enfermaria} com atributos específicos de uma unidade psiquiátrica.
 */
public class EnfermariaPsiquiatrica extends Enfermaria {

    /** Horário de visitas permitido na enfermaria. */
    private String horarioVisitas;

    /** Nível de segurança da enfermaria (ex: "baixo", "médio", "alto"). */
    private String nivelSeguranca;

    /**
     *
     * @param id               identificador único da enfermaria
     * @param camas            número total de camas
     * @param horarioVisitas   horário de visitas permitido
     * @param nivelSeguranca   nível de segurança da unidade
     */
    public EnfermariaPsiquiatrica(String id, int camas, String horarioVisitas, String nivelSeguranca){
        super(id, camas);
        this.horarioVisitas = horarioVisitas;
        this.nivelSeguranca = nivelSeguranca;
    }

    /**
     * Retorna o horário de visitas da enfermaria
     *
     * @return horário de visitas
     */
    public String getHorarioVisitas(){
        return horarioVisitas;
    }

    /**
     * Retorna o nível de segurança da enfermaria.
     *
     * @return nível de segurança
     */
    public String getNivelSeguranca(){
        return nivelSeguranca;
    }


    @Override
    public String getTipoEnfermaria() {
        return "Psiquiatrica";
    }

    /**
     * Retorna uma representação textual da enfermaria psiquiátrica
     *
     * @return string com identificador, camas, horário de visitas e nível de segurança
     */
    @Override
    public String toString(){
        return String.format("%s | Tipo: Psiquiátrica | Visitas: %s | Segurança: %s",
                super.toString(), horarioVisitas, nivelSeguranca);
    }
}
