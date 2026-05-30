package modelo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Enfermaria.
 */
public class EnfermariaTest {

    private EnfermariaGeral enfermaria;

    @BeforeAll
    static void setup() {
        System.out.println("Iniciando testes de Enfermaria...");
    }

    @AfterAll
    static void teardown() {
        System.out.println("Finalizando testes de Enfermaria...");
    }

    @BeforeEach
    void init() {
        enfermaria = new EnfermariaGeral("G1", 4, 2, "09h-20h");

        Episodio ep1 = new Episodio("C1", LocalDate.of(2024, 3, 1));
        ep1.darAlta(LocalDate.of(2024, 3, 10));

        Episodio ep2 = new Episodio("C2", LocalDate.of(2024, 3, 5));

        enfermaria.adicionarEpisodio(ep1);
        enfermaria.adicionarEpisodio(ep2);
    }

    @Test
    void testAdicionarEpisodioValido() {
        Episodio ep = new Episodio("C3", LocalDate.of(2024, 3, 1));
        assertTrue(enfermaria.adicionarEpisodio(ep));
    }

    @Test
    void testAdicionarEpisodioNulo() {
        assertFalse(enfermaria.adicionarEpisodio(null));
    }

    @Test
    void testGetOcupacaoAbsoluta() {
        assertEquals(2, enfermaria.getOcupacaoAbsoluta(LocalDate.of(2024, 3, 5)));
    }

    @Test
    void testGetOcupacaoAbsolutaForaDoIntervalo() {
        assertEquals(0, enfermaria.getOcupacaoAbsoluta(LocalDate.of(2024, 1, 1)));
    }

    @Test
    void testGetTaxaOcupacao() {
        assertEquals(50.0, enfermaria.getTaxaOcupacao(LocalDate.of(2024, 3, 5)));
    }

    @Test
    void testEmPressaoFalse() {
        assertFalse(enfermaria.emPressao(LocalDate.of(2024, 3, 5)));
    }

    @Test
    void testEmPressaoTrue() {
        EnfermariaGeral enfPressao = new EnfermariaGeral("G2", 2, 1, "09h-20h");
        enfPressao.adicionarEpisodio(new Episodio("C1", LocalDate.of(2024, 3, 1)));
        enfPressao.adicionarEpisodio(new Episodio("C2", LocalDate.of(2024, 3, 1)));
        assertTrue(enfPressao.emPressao(LocalDate.of(2024, 3, 5)));
    }

    @Test
    void testGetNumeroAdmissoes() {
        assertEquals(1, enfermaria.getNumeroAdmissoes(LocalDate.of(2024, 3, 1)));
    }

    @Test
    void testGetNumeroAltas() {
        assertEquals(1, enfermaria.getNumeroAltas(LocalDate.of(2024, 3, 10)));
    }

    @Test
    void testGetValoresLoS() {
        List<Long> valores = enfermaria.getValoresLoS();
        assertEquals(1, valores.size());
        assertEquals(9L, valores.get(0));
    }

    @Test
    void testAlterarCapacidade() {
        List<Enfermaria> lista = new ArrayList<>();
        lista.add(enfermaria);
        Enfermaria.alterarCapacidade(lista, 50.0);
        assertEquals(6, enfermaria.getNumeroCamas());
    }

    @Test
    void testAlterarCapacidadeMinima() {
        List<Enfermaria> lista = new ArrayList<>();
        lista.add(enfermaria);
        Enfermaria.alterarCapacidade(lista, -100.0);
        assertEquals(1, enfermaria.getNumeroCamas());
    }

    @Test
    void testGetEpisodiosOrdenadosPorAdmissaoOrdem() {
        EnfermariaGeral enf = new EnfermariaGeral("G3", 4, 1, "09h-20h");
        Episodio ep1 = new Episodio("C1", LocalDate.of(2024, 3, 10));
        Episodio ep2 = new Episodio("C2", LocalDate.of(2024, 3, 1));
        Episodio ep3 = new Episodio("C3", LocalDate.of(2024, 3, 5));
        enf.adicionarEpisodio(ep1);
        enf.adicionarEpisodio(ep2);
        enf.adicionarEpisodio(ep3);

        List<Episodio> ordenados = enf.getEpisodiosOrdenadosPorAdmissao();

        /** Verifica que o primeiro é o mais antigo */
        assertEquals(LocalDate.of(2024, 3, 1), ordenados.get(0).getDataAdmissao());
        /** Verifica que o segundo é o do meio */
        assertEquals(LocalDate.of(2024, 3, 5), ordenados.get(1).getDataAdmissao());
        /** Verifica que o último é o mais recente */
        assertEquals(LocalDate.of(2024, 3, 10), ordenados.get(2).getDataAdmissao());
    }

    @Test
    void testExisteConflitoDeCamaSemConflito() {
        /** C1 tem alta em 10/03, novo episódio começa em 11/03 — sem conflito */
        Episodio novoEp = new Episodio("C1", LocalDate.of(2024, 3, 11));
        assertFalse(enfermaria.existeConflitoDeCama(novoEp));
    }

    @Test
    void testExisteConflitoDeCamaComConflito() {
        /** C2 já está ativo desde 05/03 sem alta — novo episódio na mesma cama conflitua */
        Episodio novoEp = new Episodio("C2", LocalDate.of(2024, 3, 8));
        assertTrue(enfermaria.existeConflitoDeCama(novoEp));
    }

    @Test
    void testExisteConflitoDeCamaDiferente() {
        /** Cama diferente — nunca há conflito */
        Episodio novoEp = new Episodio("C4", LocalDate.of(2024, 3, 1));
        assertFalse(enfermaria.existeConflitoDeCama(novoEp));
    }
}