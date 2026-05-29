package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Representa uma enfermaria do hospital.
 */
public abstract class Enfermaria implements Analisavel {

    /** Identificador da enfermaria. */
    private String identificador;

    /** Número total de camas. */
    private int numeroCamas;

    /** Episódios associados à enfermaria. */
    private List<Episodio> episodios;

    /**
     * Cria uma enfermaria.
     *
     * @param identificador identificador da enfermaria
     * @param numeroCamas número total de camas
     */
    public Enfermaria(String identificador, int numeroCamas) {
        this.identificador = identificador;
        this.numeroCamas = numeroCamas;
        this.episodios = new ArrayList<>();
    }

    /**
     * Devolve o identificador da enfermaria.
     *
     * @return identificador
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Devolve o número de camas.
     *
     * @return número de camas
     */
    public int getNumeroCamas() {
        return numeroCamas;
    }

    /**
     * Devolve os episódios da enfermaria.
     *
     * @return cópia da lista de episódios
     */
    public List<Episodio> getEpisodios() {
        return new ArrayList<>(episodios);
    }

    /**
     * Adiciona um episódio à enfermaria.
     *
     * @param episodio episódio a adicionar
     * @return {@code true} se foi adicionado
     */
    public boolean adicionarEpisodio(Episodio episodio) {
        if (episodio == null || episodio.getCamaId() == null || episodio.getCamaId().isBlank()) {
            return false;
        }
        if (existeConflitoDeCama(episodio)) {
            return false;
        }
        episodio.associarEnfermaria(identificador);
        episodios.add(episodio);
        return true;
    }

    /**
     * Devolve os episódios ativos numa data.
     *
     * @param data data a analisar
     * @return lista de episódios ativos
     */
    public List<Episodio> getEpisodiosAtivos(LocalDate data) {
        List<Episodio> ativos = new ArrayList<>();
        for (Episodio episodio : episodios) {
            if (episodio.estaAtivoEm(data)) {
                ativos.add(episodio);
            }
        }
        return ativos;
    }

    /**
     * Devolve os episódios com alta.
     *
     * @return episódios com alta
     */
    public List<Episodio> getEpisodiosComAlta() {
        List<Episodio> concluidos = new ArrayList<>();
        for (Episodio episodio : episodios) {
            if (episodio.temAlta()) {
                concluidos.add(episodio);
            }
        }
        return concluidos;
    }

    /**
     * Devolve os episódios ordenados por admissão.
     *
     * @return lista ordenada
     */
    public List<Episodio> getEpisodiosOrdenadosPorAdmissao() {
        List<Episodio> ordenados = getEpisodios();
        int n = ordenados.size();

        /** Algoritmo Bubble Sort */
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                Episodio ep1 = ordenados.get(j);
                Episodio ep2 = ordenados.get(j + 1);

                // 1. Ir buscar as datas e os IDs
                java.time.LocalDate data1 = ep1.getDataAdmissao();
                java.time.LocalDate data2 = ep2.getDataAdmissao();

                String id1 = ep1.getEpisodioId();
                String id2 = ep2.getEpisodioId();

                /** Variável para decidir se devemos trocar os dois de sítio */
                boolean trocar = false;

                /** Se a data do primeiro for depois da data do segundo, troca */
                if (data1.isAfter(data2)) {
                    trocar = true;
                }
                /** Se as datas forem exatamente iguais, desempata pelo ID do episódio */
                else if (data1.equals(data2)) {
                    if (id1.compareTo(id2) > 0) {
                        trocar = true;
                    }
                }

                /** Código tradicional para trocar dois elementos numa lista */
                if (trocar) {
                    ordenados.set(j, ep2);
                    ordenados.set(j + 1, ep1);
                }
            }
        }

        return ordenados;
    }

    /**
     * Devolve os valores de LoS dos episódios com alta.
     *
     * @return lista de valores de LoS
     */
    public List<Long> getValoresLoS() {
        List<Long> valores = new ArrayList<>();
        for (Episodio episodio : getEpisodiosComAlta()) {
            valores.add(episodio.calcularLoS());
        }
        return valores;
    }

    /**
     * Calcula o número de camas ocupadas numa data.
     *
     * @param data data a analisar
     * @return camas ocupadas
     */
    @Override
    public int getOcupacaoAbsoluta(LocalDate data) {
        return getEpisodiosAtivos(data).size();
    }

    /**
     * Calcula a taxa de ocupação numa data.
     *
     * @param data data a analisar
     * @return taxa de ocupação
     */
    @Override
    public double getTaxaOcupacao(LocalDate data) {
        if (numeroCamas <= 0) {
            return 0.0;
        }
        return (getOcupacaoAbsoluta(data) * 100.0) / numeroCamas;
    }

    /**
     * Indica se a enfermaria está em pressão numa data.
     *
     * @param data data a analisar
     * @return {@code true} se a taxa for superior a 85%
     */
    @Override
    public boolean emPressao(LocalDate data) {
        return getTaxaOcupacao(data) > 85.0;
    }

    /**
     * Calcula a percentagem de dias em pressão num intervalo.
     *
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return percentagem de dias em pressão
     */
    public double getPercentagemDiasEmPressao(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            return 0.0;
        }
        int totalDias = 0;
        int diasEmPressao = 0;
        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            totalDias++;
            if (emPressao(dataAtual)) {
                diasEmPressao++;
            }
            dataAtual = dataAtual.plusDays(1);
        }
        return totalDias == 0 ? 0.0 : (diasEmPressao * 100.0) / totalDias;
    }

    /**
     * Verifica se existe conflito de ocupação da mesma cama.
     *
     * @param novoEpisodio episódio a validar
     * @return {@code true} se existir conflito
     */
    public boolean existeConflitoDeCama(Episodio novoEpisodio) {
        for (Episodio episodioExistente : episodios) {
            if (episodioExistente.getCamaId().equalsIgnoreCase(novoEpisodio.getCamaId())
                    && episodiosSobrepostos(episodioExistente, novoEpisodio)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devolve uma representação textual da enfermaria.
     *
     * @return texto com os dados principais
     */
    @Override
    public String toString() {
        return String.format("ID: %s | Camas: %d | Episodios: %d",
                identificador,
                numeroCamas,
                episodios.size());
    }

    /**
     * Verifica se dois episódios se sobrepõem.
     *
     * @param primeiro primeiro episódio
     * @param segundo segundo episódio
     * @return {@code true} se houver sobreposição
     */
    private boolean episodiosSobrepostos(Episodio primeiro, Episodio segundo) {
        LocalDate inicioPrimeiro = primeiro.getDataAdmissao();
        LocalDate fimPrimeiro = primeiro.temAlta() ? primeiro.getDataAlta() : LocalDate.MAX;
        LocalDate inicioSegundo = segundo.getDataAdmissao();
        LocalDate fimSegundo = segundo.temAlta() ? segundo.getDataAlta() : LocalDate.MAX;

        return !inicioPrimeiro.isAfter(fimSegundo) && !inicioSegundo.isAfter(fimPrimeiro);
    }

    /**
     * Conta o número de admissões numa data específica.
     *
     * @param data data de referência
     * @return número de episódios admitidos nessa data
     */
    public int getNumeroAdmissoes(LocalDate data) {
        int count = 0;
        for (Episodio ep : episodios) {
            if (ep.getDataAdmissao().equals(data)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Conta o número de altas numa data específica.
     *
     * @param data data de referência
     * @return número de episódios com alta nessa data
     */
    public int getNumeroAltas(LocalDate data) {
        int count = 0;
        for (Episodio ep : episodios) {
            if (ep.temAlta() && ep.getDataAlta().equals(data)) {
                count++;
            }
        }
        return count;
    }




// METODOS 2 ITERAÇÃO

/**
 * Altera a capacidade de todas as enfermarias do hospital com base numa percentagem.
 *
 * @param enfermarias lista de enfermarias a processar
 * @param percentagem valor percentual de alteracao (ex: 10 para +10%, -20 para -20%)
 */
public static void alterarCapacidade(List<Enfermaria> enfermarias, double percentagem) {
    if (enfermarias == null || enfermarias.isEmpty()) {
        return;
    }

    for (int i = 0; i < enfermarias.size(); i++) {
        Enfermaria enf = enfermarias.get(i);
        int capacidadeAtual = enf.getNumeroCamas();

        // Calcula a diferenca e arredonda para não termos "meias camas"
        int variacao = (int) Math.round(capacidadeAtual * (percentagem / 100.0));
        int novaCapacidade = capacidadeAtual + variacao;

        // Blindagem: Uma enfermaria tem de ter, no mínimo, 1 cama.
        if (novaCapacidade < 1) {
            novaCapacidade = 1;
        }

        // Como o metodo esta dentro da propria classe, pode aceder ao atributo privado diretamente
        enf.numeroCamas = novaCapacidade;
    }
}

}