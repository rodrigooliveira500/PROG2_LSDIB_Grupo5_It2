package modelo;

import java.time.LocalDate;

/**
 * Define indicadores de ocupação por data.
 */
public interface Analisavel {

    /**
     * Devolve o número de camas ocupadas numa data.
     *
     * @param data data a analisar
     * @return número de camas ocupadas
     */
    int getOcupacaoAbsoluta(LocalDate data);

    /**
     * Calcula a taxa de ocupação numa data.
     *
     * @param data data a analisar
     * @return taxa de ocupação em percentagem
     */
    double getTaxaOcupacao(LocalDate data);

    /**
     * Indica se a enfermaria está em pressão numa data.
     *
     * @param data data a analisar
     * @return {@code true} se a taxa for superior a 85%
     */
    boolean emPressao(LocalDate data);
}
