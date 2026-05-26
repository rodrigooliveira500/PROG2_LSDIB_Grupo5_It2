package main;

import modelo.Enfermaria;
import utils.AnalisadorEstatistico;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe responsável pela apresentação visual de dados no ecrã.
 *
 * <p>Implementa as funcionalidades RF3, RF5 e RF8 do enunciado.</p>
 *
 * @author Grupo 5
 * @version 1.0
 */
public class Visualizador {

    /** Número de caracteres que representa 100% na barra ASCII. */
    private static final int LARGURA_BARRA = 50;

    /** Altura máxima em linhas do gráfico vertical. */
    private static final int ALTURA_GRAFICO_VERTICAL = 20;

    /** Símbolo utilizado por omissão. */
    public static final char SIMBOLO_PADRAO = '#';

    /** Construtor privado — classe utilitária, não deve ser instanciada. */
    private Visualizador() {}
}
