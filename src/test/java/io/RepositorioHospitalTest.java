package io;

import exceptions.HospitalException;
import modelo.EnfermariaGeral;
import modelo.Episodio;
import modelo.Hospital;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe GestorSerializacao.
 */
public class RepositorioHospitalTest {

    private Hospital hospital;
    private static final String FICHEIRO_TESTE = "hospital_teste.ser";

    @BeforeAll
    static void setup() {
        System.out.println("Iniciando testes de GestorSerializacao...");
    }

    @AfterAll
    static void teardown() {
        // apaga o ficheiro de teste no fim
        File f = new File(FICHEIRO_TESTE);
        if (f.exists()) f.delete();
        System.out.println("Finalizando testes de GestorSerializacao...");
    }

    @BeforeEach
    void init() {
        hospital = new Hospital("Hospital XYZ");
        EnfermariaGeral enf = new EnfermariaGeral("G1", 4, 2, "09h-20h");
        enf.adicionarEpisodio(new Episodio("C1", LocalDate.of(2024, 3, 1)));
        hospital.adicionarEnfermaria(enf);
    }

    @Test
    void testGravarECarregarNome() throws IOException, HospitalException {
        RepositorioHospital.gravarEstado(hospital, FICHEIRO_TESTE);
        Hospital carregado = RepositorioHospital.carregarEstado(FICHEIRO_TESTE);
        assertEquals("Hospital XYZ", carregado.getNome());
    }

    @Test
    void testGravarECarregarEnfermarias() throws IOException, HospitalException {
        RepositorioHospital.gravarEstado(hospital, FICHEIRO_TESTE);
        Hospital carregado = RepositorioHospital.carregarEstado(FICHEIRO_TESTE);
        assertEquals(1, carregado.getEnfermarias().size());
    }

    @Test
    void testGravarECarregarEpisodios() throws IOException, HospitalException {
        RepositorioHospital.gravarEstado(hospital, FICHEIRO_TESTE);
        Hospital carregado = RepositorioHospital.carregarEstado(FICHEIRO_TESTE);
        assertEquals(1, carregado.obterEnfermaria("G1").getEpisodios().size());
    }

    @Test
    void testFicheiroExisteAposGravar() throws IOException {
        RepositorioHospital.gravarEstado(hospital, FICHEIRO_TESTE);
        assertTrue(new File(FICHEIRO_TESTE).exists());
    }

    @Test
    void testCarregarFicheiroInexistente() {
        boolean lancouExcecao = false;
        try {
            RepositorioHospital.carregarEstado("ficheiro_inexistente.ser");
        } catch (IOException e) {
            lancouExcecao = true;
        } catch (HospitalException e) {
            lancouExcecao = true;
        }
        assertTrue(lancouExcecao);
    }
}