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

    /**
     * Repete um caracter n vezes.
     *
     * @param c caracter a repetir
     * @param n número de repetições
     * @return string resultante
     */
    private static String repetir(char c, int n) {
        if (n <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Trunca uma string ao comprimento máximo indicado.
     *
     * @param texto    string a truncar
     * @param maxChars comprimento máximo
     * @return string truncada
     */
    private static String truncar(String texto, int maxChars) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= maxChars ? texto : texto.substring(0, maxChars);
    }

    /**
     * Devolve o valor máximo de uma lista de doubles.
     *
     * @param valores lista de valores
     * @return valor máximo; 0.0 se a lista for vazia
     */
    private static double obterMaximo(List<Double> valores) {
        if (valores == null || valores.isEmpty()) {
            return 0.0;
        }
        double max = valores.get(0);
        for (double v : valores) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }
}
