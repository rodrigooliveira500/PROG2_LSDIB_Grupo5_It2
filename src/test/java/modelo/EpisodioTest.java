package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Episodio.
 */
public class EpisodioTest {

    private Episodio episodioSemAlta;
    private Episodio episodioComAlta;

    @BeforeAll
    static void setup() {
        System.out.println("Iniciando testes de Episodio...");
    }

    @AfterAll
    static void teardown() {
        System.out.println("Finalizando testes de Episodio...");
    }

    @BeforeEach
    void init() {
        episodioSemAlta = new Episodio("C1", LocalDate.of(2024, 3, 1));
        episodioComAlta = new Episodio("C2", LocalDate.of(2024, 3, 1));
        episodioComAlta.darAlta(LocalDate.of(2024, 3, 10));
    }

    @Test
    void testTemAltaFalse() {
        assertFalse(episodioSemAlta.temAlta());
    }

    @Test
    void testTemAltaTrue() {
        assertTrue(episodioComAlta.temAlta());
    }

    @Test
    void testCalcularLoSSemAlta() {
        assertEquals(-1, episodioSemAlta.calcularLoS());
    }

    @Test
    void testCalcularLoSComAlta() {
        assertEquals(9, episodioComAlta.calcularLoS());
    }

    @Test
    void testDarAltaDataInvalida() {
        Episodio ep = new Episodio("C3", LocalDate.of(2024, 3, 5));
        ep.darAlta(LocalDate.of(2024, 3, 1)); // data anterior à admissão
        assertFalse(ep.temAlta());
    }

    @Test
    void testEstaAtivoEmDataAdmissao() {
        assertTrue(episodioSemAlta.estaAtivoEm(LocalDate.of(2024, 3, 1)));
    }

    @Test
    void testEstaAtivoEmDataAnteriorAdmissao() {
        assertFalse(episodioSemAlta.estaAtivoEm(LocalDate.of(2024, 2, 28)));
    }

    @Test
    void testEstaAtivoEmDepoisAlta() {
        assertFalse(episodioComAlta.estaAtivoEm(LocalDate.of(2024, 3, 15)));
    }

    @Test
    void testGetEstadoAtivo() {
        assertEquals("ATIVO", episodioSemAlta.getEstado());
    }

    @Test
    void testGetEstadoAltaAposAlta() {
        assertEquals("ALTA", episodioComAlta.getEstado());
    }

    @Test
    void testGetCamaId() {
        assertEquals("C1", episodioSemAlta.getCamaId());
    }

    @Test
    void testGetDataAdmissao() {
        assertEquals(LocalDate.of(2024, 3, 1), episodioSemAlta.getDataAdmissao());
    }
}