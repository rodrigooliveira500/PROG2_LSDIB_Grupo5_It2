package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *Representa uma enfermaria geral, com suporte a acompanhantes e lista de recursos disponíveis.
 *Estende {@link Enfermaria} com atributos específicos de uma unidade geral.
 */
public class EnfermariaGeral extends Enfermaria {

  /** Número máximo de acompanhantes permitidos por paciente. */
  private int limiteAcompanhantes;
  
  /** Horário de visitas da enfermaria. */
  private String horarioVisitas;

  /** Lista de recursos disponiveis na enfermaria. */
  private List<String> recursosDisponiveis;

  /**
   * Cria uma nova enfermaria geral.
   *
   * @param id                  identificador único de enfermaria
   * @param camas               número toal de camas
   * @param limiteAcompanhantes número máximo de acompanhantes por paciente
   * @param horarioVisitas      horário de visitas permitido
   */
  public EnfermariaGeral(String id, int camas, int limiteAcompanhantes, String horarioVisitas) {
    super (id, camas);
    this.limiteAcompanhantes = limiteAcompanhantes;
    this.horarioVisitas = horarioVisitas;
    this.recursosDisponiveis = new ArrayList<>();
  }

  /**
   * Adiciona um recurso à lista de recursos disponíveis na enfermaria.
   * Ignora valores nulos ou em branco.
   *
   * @param recurso nome do recurso a adicionar
   */
  public void adicionarRecurso(String recurso){
    if (recurso != null && !recurso.isBlank()) {
      recursosDisponiveis.add(recurso);
    }
  }
  
  /**
   * Remove um recurso da linha de recursos disponíveis.
   * 
   * @param recurso nome do recurso a remover
   * @return {@code true} se removido com sucesso, {@code false} se não existia
   */
  public boolean removerRecurso(String recurso){
    return recursosDisponiveis.remove(recurso);
  }

  /**
   * Retorna o número máximo de acompanhantes por paciente.
   * 
   * @return limiteAcompanhantes
   */
  public int getLimiteAcompanhantes(){
    return limiteAcompanhantes;
  }

  /**
   * Retorna o horário de visitas da enfermaria.
   * 
   * @return horário de visitas
   */
  public String getHorariosVisitas(){
    return horarioVisitas;
  }

  /**
   * Retorna uma cópia da lista de recursos disponíveis.
   *
   * @return lista de recursos (cópia defensiva)
   */
  public List<String> getRecursosDisponiveis(){
    return new ArrayList<>(recursosDisponiveis);
  }

    /**
     * Retorna o tipo de enfermaria.
     *
     * @return string "Geral" indicando o tipo desta enfermaria
     */
    @Override
    public String getTipoEnfermaria() {
        return "Geral";
    }

    /**
   * Retorna uma representação textual da enfermaria geral.
   *
   * @return string com identificador, camas, acaompanhantes e horário de visitas
   */
  @Override
  public String toString(){
    return String.format("%s | Tipo: Geral | Acompanhantes: %d | Visitas: %s | Recursos: %s",
                         super.toString(), limiteAcompanhantes, horarioVisitas, recursosDisponiveis);
  }
}