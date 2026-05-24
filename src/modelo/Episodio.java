package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Representa um episódio de internamento.
 */
public class Episodio {

    /** Contador para gerar identificadores simples. */
    private static int proximoNumero = 1;

    /** Identificador do episódio. */
    private String episodioId;

    /** Identificador da cama. */
    private String camaId;

    /** Data de admissão. */
    private LocalDate dataAdmissao;

    /** Data de alta. */
    private LocalDate dataAlta;

    /** Estado atual do episódio. */
    private String estado;

    /** Identificador da enfermaria associada. */
    private String enfermariaId;

    /**
     * Cria um episódio ativo.
     *
     * @param camaId identificador da cama
     * @param dataAdmissao data de admissão
     */
    public Episodio(String camaId, LocalDate dataAdmissao) {
        this("EP" + proximoNumero++, camaId, dataAdmissao, null, "ATIVO");
    }

    /**
     * Cria um episódio com os dados principais.
     *
     * @param episodioId identificador do episódio
     * @param camaId identificador da cama
     * @param dataAdmissao data de admissão
     * @param dataAlta data de alta
     * @param estado estado do episódio
     */
    public Episodio(String episodioId, String camaId, LocalDate dataAdmissao, LocalDate dataAlta, String estado) {
        this.episodioId = episodioId;
        this.camaId = camaId;
        this.dataAdmissao = dataAdmissao;
        this.dataAlta = dataAlta;
        this.estado = estado == null || estado.isBlank() ? "ATIVO" : estado.trim().toUpperCase();
        if (temAlta()) {
            this.estado = "ALTA";
        }
    }

    /**
     * Devolve o identificador do episódio.
     *
     * @return identificador do episódio
     */
    public String getEpisodioId() {
        return episodioId;
    }

    /**
     * Devolve o identificador da cama.
     *
     * @return identificador da cama
     */
    public String getCamaId() {
        return camaId;
    }

    /**
     * Devolve a data de admissão.
     *
     * @return data de admissão
     */
    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    /**
     * Devolve a data de alta.
     *
     * @return data de alta
     */
    public LocalDate getDataAlta() {
        return dataAlta;
    }

    /**
     * Devolve o estado do episódio.
     *
     * @return estado atual
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Devolve o identificador da enfermaria.
     *
     * @return identificador da enfermaria
     */
    public String getEnfermariaId() {
        return enfermariaId;
    }

    /**
     * Associa o episódio a uma enfermaria.
     *
     * @param enfermariaId identificador da enfermaria
     */
    public void associarEnfermaria(String enfermariaId) {
        this.enfermariaId = enfermariaId;
    }

    /**
     * Indica se o episódio tem alta.
     *
     * @return {@code true} se existir data de alta
     */
    public boolean temAlta() {
        return dataAlta != null;
    }

    /**
     * Indica se o episódio está ativo numa data.
     *
     * @param data data a verificar
     * @return {@code true} se estiver ativo
     */
    public boolean estaAtivoEm(LocalDate data) {
        if (data == null || dataAdmissao == null) {
            return false;
        }
        boolean depoisDaAdmissao = !data.isBefore(dataAdmissao);
        boolean antesOuNaAlta = dataAlta == null || !data.isAfter(dataAlta);
        return depoisDaAdmissao && antesOuNaAlta;
    }

    /**
     * Regista a alta do episódio.
     *
     * @param novaDataAlta data de alta
     */
    public void darAlta(LocalDate novaDataAlta) {
        if (novaDataAlta != null && !novaDataAlta.isBefore(dataAdmissao)) {
            this.dataAlta = novaDataAlta;
            this.estado = "ALTA";
        }
    }

    /**
     * Calcula o Length of Stay em dias.
     *
     * @return número de dias, ou {@code -1} se não tiver alta
     */
    public long calcularLoS() {
        if (!temAlta()) {
            return -1;
        }
        return ChronoUnit.DAYS.between(dataAdmissao, dataAlta);
    }

    /**
     * Devolve uma representação textual do episódio.
     *
     * @return texto com os dados principais
     */
    @Override
    public String toString() {
        String alta = temAlta() ? dataAlta.toString() : "sem alta";
        return String.format("%s | Cama: %s | Enfermaria: %s | Admissao: %s | Alta: %s | Estado: %s",
                episodioId,
                camaId,
                enfermariaId == null ? "-" : enfermariaId,
                dataAdmissao,
                alta,
                estado);
    }
}
