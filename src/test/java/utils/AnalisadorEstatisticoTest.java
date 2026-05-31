package utils;

import modelo.Enfermaria;
import modelo.EnfermariaGeral;
import modelo.Episodio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe AnalisadorEstatistico.
 */
public class AnalisadorEstatisticoTest {

    private EnfermariaGeral enfermaria;

    @BeforeAll
    static void setup() {
        System.out.println("Iniciando testes de AnalisadorEstatistico...");
    }

    @AfterAll
    static void teardown() {
        System.out.println("Finalizando testes de AnalisadorEstatistico...");
    }

    @BeforeEach
    void init() {
        enfermaria = new EnfermariaGeral("G1", 4, 2, "09h-20h");

        Episodio ep1 = new Episodio("C1", LocalDate.of(2024, 3, 1));
        ep1.darAlta(LocalDate.of(2024, 3, 10)); // LoS = 9

        Episodio ep2 = new Episodio("C2", LocalDate.of(2024, 3, 1));
        ep2.darAlta(LocalDate.of(2024, 3, 6));  // LoS = 5

        Episodio ep3 = new Episodio("C3", LocalDate.of(2024, 3, 1));
        ep3.darAlta(LocalDate.of(2024, 3, 8));  // LoS = 7

        enfermaria.adicionarEpisodio(ep1);
        enfermaria.adicionarEpisodio(ep2);
        enfermaria.adicionarEpisodio(ep3);
    }

    /** =============================================
    calcularScoreOcupacao
    ============================================= */

    @Test
    void testScoreOcupacaoAbaixo85() {
        assertEquals(1, AnalisadorEstatistico.calcularScoreOcupacao(50.0));
    }

    @Test
    void testScoreOcupacaoLimite85() {
        assertEquals(1, AnalisadorEstatistico.calcularScoreOcupacao(85.0));
    }

    @Test
    void testScoreOcupacaoEntre85e90() {
        assertEquals(2, AnalisadorEstatistico.calcularScoreOcupacao(88.0));
    }

    @Test
    void testScoreOcupacaoEntre90e95() {
        assertEquals(3, AnalisadorEstatistico.calcularScoreOcupacao(92.0));
    }

    @Test
    void testScoreOcupacaoEntre95e100() {
        assertEquals(4, AnalisadorEstatistico.calcularScoreOcupacao(98.0));
    }

    @Test
    void testScoreOcupacaoAcima100() {
        assertEquals(5, AnalisadorEstatistico.calcularScoreOcupacao(110.0));
    }

    /** =============================================
    calcularScoreTurnover
     ============================================= */

    @Test
    void testScoreTurnoverAbaixo10() {
        assertEquals(1, AnalisadorEstatistico.calcularScoreTurnover(5.0));
    }

    @Test
    void testScoreTurnoverEntre10e20() {
        assertEquals(2, AnalisadorEstatistico.calcularScoreTurnover(15.0));
    }

    @Test
    void testScoreTurnoverEntre20e30() {
        assertEquals(3, AnalisadorEstatistico.calcularScoreTurnover(25.0));
    }

    @Test
    void testScoreTurnoverEntre30e40() {
        assertEquals(4, AnalisadorEstatistico.calcularScoreTurnover(35.0));
    }

    @Test
    void testScoreTurnoverAcima40() {
        assertEquals(5, AnalisadorEstatistico.calcularScoreTurnover(50.0));
    }

    /** =============================================
    interpretarIndice
    ============================================= */

    @Test
    void testInterpretarIndiceBaixo() {
        assertEquals("Pressao Baixa", AnalisadorEstatistico.interpretarIndice(1.5));
    }

    @Test
    void testInterpretarIndiceModerado() {
        assertEquals("Pressao Moderada", AnalisadorEstatistico.interpretarIndice(3.0));
    }

    @Test
    void testInterpretarIndiceAlto() {
        assertEquals("Pressao Alta", AnalisadorEstatistico.interpretarIndice(4.0));
    }

    /** =============================================
    calcularEstatisticaLoS
    ============================================= */

    @Test
    void testCalcularEstatisticaLoSTotalEpisodios() {
        AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calcularEstatisticaLoS(enfermaria);
        assertEquals(3, sumario.getTotalEpisodios());
    }

    @Test
    void testCalcularEstatisticaLoSMedia() {
        /** (9 + 5 + 7) / 3 = 7.0 */
        AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calcularEstatisticaLoS(enfermaria);
        assertEquals(7.0, sumario.getMedia());
    }

    @Test
    void testCalcularEstatisticaLoSMinimo() {
        AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calcularEstatisticaLoS(enfermaria);
        assertEquals(5L, sumario.getMinimo());
    }

    @Test
    void testCalcularEstatisticaLoSMaximo() {
        AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calcularEstatisticaLoS(enfermaria);
        assertEquals(9L, sumario.getMaximo());
    }

    @Test
    void testCalcularEstatisticaLoSSemAltas() {
        EnfermariaGeral enfVazia = new EnfermariaGeral("G2", 4, 1, "09h-20h");
        enfVazia.adicionarEpisodio(new Episodio("C1", LocalDate.of(2024, 3, 1)));
        AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calcularEstatisticaLoS(enfVazia);
        assertEquals(0, sumario.getTotalEpisodios());
    }

    /** =============================================
    percentagemEmPressao
    ============================================= */

    @Test
    void testPercentagemEmPressaoZero() {
        List<Enfermaria> lista = new ArrayList<>();
        lista.add(enfermaria);
        /** enfermaria com 3 episódios em 4 camas = 75% — não está em pressão */
        assertEquals(0.0, AnalisadorEstatistico.percentagemEmPressao(lista, LocalDate.of(2024, 3, 2)));
    }

    @Test
    void testPercentagemEmPressao100() {
        EnfermariaGeral enfPressao = new EnfermariaGeral("G3", 2, 1, "09h-20h");
        enfPressao.adicionarEpisodio(new Episodio("C1", LocalDate.of(2024, 3, 1)));
        enfPressao.adicionarEpisodio(new Episodio("C2", LocalDate.of(2024, 3, 1)));
        List<Enfermaria> lista = new ArrayList<>();
        lista.add(enfPressao);
        /** 2 episódios em 2 camas = 100% — em pressão */
        assertEquals(100.0, AnalisadorEstatistico.percentagemEmPressao(lista, LocalDate.of(2024, 3, 5)));
    }

    @Test
    void testPercentagemEmPressaoListaVazia() {
        assertEquals(0.0, AnalisadorEstatistico.percentagemEmPressao(new ArrayList<>(), LocalDate.of(2024, 3, 5)));
    }
}