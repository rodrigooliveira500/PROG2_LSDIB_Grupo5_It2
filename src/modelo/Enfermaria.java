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
     * Devolve o tipo da enfermaria.
     *
     * @return tipo da enfermaria
     */
    public abstract String getTipoEnfermaria();

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
        ordenados.sort(Comparator.comparing(Episodio::getDataAdmissao)
                .thenComparing(Episodio::getEpisodioId));
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
        return String.format("%s | ID: %s | Camas: %d | Episodios: %d",
                getTipoEnfermaria(),
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
}
