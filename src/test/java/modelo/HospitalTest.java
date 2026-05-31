package modelo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Hospital.
 */
public class HospitalTest {

    private Hospital hospital;
    private EnfermariaGeral enf1;
    private EnfermariaGeral enf2;

    @BeforeAll
    static void setup() {
        System.out.println("Iniciando testes de Hospital...");
    }

    @AfterAll
    static void teardown() {
        System.out.println("Finalizando testes de Hospital...");
    }

    @BeforeEach
    void init() {
        hospital = new Hospital("Hospital XYZ");

        enf1 = new EnfermariaGeral("G1", 4, 2, "09h-20h");
        enf2 = new EnfermariaGeral("G2", 4, 2, "09h-20h");

        /** enf1 com 4 episódios ativos — 100% de ocupação */
        enf1.adicionarEpisodio(new Episodio("C1", LocalDate.of(2024, 3, 1)));
        enf1.adicionarEpisodio(new Episodio("C2", LocalDate.of(2024, 3, 1)));
        enf1.adicionarEpisodio(new Episodio("C3", LocalDate.of(2024, 3, 1)));
        enf1.adicionarEpisodio(new Episodio("C4", LocalDate.of(2024, 3, 1)));

        /** enf2 com 2 episódios ativos — 50% de ocupação */
        enf2.adicionarEpisodio(new Episodio("C1", LocalDate.of(2024, 3, 1)));
        enf2.adicionarEpisodio(new Episodio("C2", LocalDate.of(2024, 3, 1)));

        hospital.adicionarEnfermaria(enf1);
        hospital.adicionarEnfermaria(enf2);
    }

    @Test
    void testGetNome() {
        assertEquals("Hospital XYZ", hospital.getNome());
    }

    @Test
    void testAdicionarEnfermariaValida() {
        EnfermariaGeral enf3 = new EnfermariaGeral("G3", 5, 1, "09h-20h");
        assertTrue(hospital.adicionarEnfermaria(enf3));
    }

    @Test
    void testAdicionarEnfermariaNull() {
        assertFalse(hospital.adicionarEnfermaria(null));
    }

    @Test
    void testAdicionarEnfermariaDuplicada() {
        EnfermariaGeral duplicada = new EnfermariaGeral("G1", 3, 1, "09h-20h");
        assertFalse(hospital.adicionarEnfermaria(duplicada));
    }

    @Test
    void testObterEnfermariaExistente() {
        assertNotNull(hospital.obterEnfermaria("G1"));
    }

    @Test
    void testObterEnfermariaInexistente() {
        assertNull(hospital.obterEnfermaria("X99"));
    }

    @Test
    void testObterEnfermariaInsensivelaCase() {
        assertNotNull(hospital.obterEnfermaria("g1"));
    }

    @Test
    void testGetEnfermariasSize() {
        assertEquals(2, hospital.getEnfermarias().size());
    }

    @Test
    void testListarEnfermariasOrdenadasPorTaxaPrimeira() {
        List<Enfermaria> ordenadas = hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(LocalDate.of(2024, 3, 5));
        assertEquals("G1", ordenadas.get(0).getIdentificador());
    }

    @Test
    void testListarEnfermariasOrdenadasPorTaxaUltima() {
        List<Enfermaria> ordenadas = hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(LocalDate.of(2024, 3, 5));
        assertEquals("G2", ordenadas.get(1).getIdentificador());
    }

    @Test
    void testToString() {
        assertEquals("Hospital XYZ | Enfermarias: 2", hospital.toString());
    }
}