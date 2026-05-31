package modelo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe EnfermariaGeral.
 */
public class EnfermariaGeralTest {

    private EnfermariaGeral enfermaria;

    @BeforeAll
    static void setup() {
        System.out.println("Iniciando testes de EnfermariaGeral...");
    }

    @AfterAll
    static void teardown() {
        System.out.println("Finalizando testes de EnfermariaGeral...");
    }

    @BeforeEach
    void init() {
        enfermaria = new EnfermariaGeral("G1", 4, 2, "09h-20h");
    }

    @Test
    void testAdicionarRecursoValido() {
        enfermaria.adicionarRecurso("Ventilador");
        assertEquals(1, enfermaria.getRecursosDisponiveis().size());
    }

    @Test
    void testAdicionarRecursoNulo() {
        enfermaria.adicionarRecurso(null);
        assertEquals(0, enfermaria.getRecursosDisponiveis().size());
    }

    @Test
    void testAdicionarRecursoVazio() {
        enfermaria.adicionarRecurso("   ");
        assertEquals(0, enfermaria.getRecursosDisponiveis().size());
    }

    @Test
    void testRemoverRecursoExistente() {
        enfermaria.adicionarRecurso("Ventilador");
        assertTrue(enfermaria.removerRecurso("Ventilador"));
    }

    @Test
    void testRemoverRecursoInexistente() {
        assertFalse(enfermaria.removerRecurso("Monitor"));
    }

    @Test
    void testGetRecursosDisponiveisCopiaDefensiva() {
        enfermaria.adicionarRecurso("Ventilador");
        List<String> recursos = enfermaria.getRecursosDisponiveis();
        recursos.add("Intruso");
        /** a lista interna não deve ter sido alterada */
        assertEquals(1, enfermaria.getRecursosDisponiveis().size());
    }

    @Test
    void testGetLimiteAcompanhantes() {
        assertEquals(2, enfermaria.getLimiteAcompanhantes());
    }

    @Test
    void testGetHorarioVisitas() {
        assertEquals("09h-20h", enfermaria.getHorariosVisitas());
    }
}