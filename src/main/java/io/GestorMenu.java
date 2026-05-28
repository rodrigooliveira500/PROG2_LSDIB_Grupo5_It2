package io;

import exceptions.HospitalException;
import modelo.Enfermaria;
import modelo.Episodio;
import modelo.Hospital;
import utils.AnalisadorEstatistico;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;


public class GestorMenu {

    // CONSTANTES DE CONFIGURAÇÃO

    private static final String SEPARADOR = "-".repeat(60);
    private static final String SEPARADOR_TABELA = "-".repeat(90);

    private static final int CAPACIDADE_MAXIMA_CAMAS = 1000;
    private static final int MAX_ACOMPANHANTES = 100;
    private static final int TAMANHO_BARRA_ASCII = 50;

    private static final String NOME_FICHEIRO_ENFERMARIAS = "enfermarias.csv";
    private static final String NOME_FICHEIRO_EPISODIOS = "episodios.csv";

    private static final String FORMATO_DATA_ESPERADO = "AAAA-MM-DD";
}



    // OPÇÕES DO MENU
    // ---------------------------------------------------------

    public static void processarOpcao(int opcao, Scanner leitor, Hospital hospital) throws Exception {
        switch (opcao) {
            case 1  -> carregarCSV(leitor, hospital);
            case 2  -> inserirEnfermaria(leitor, hospital);
            case 3  -> inserirEpisodio(leitor, hospital);
            case 4  -> mostrarIndicadoresOcupacao(leitor, hospital);
            case 5  -> analisarPressaoIntervalo(leitor, hospital);
            case 6  -> mostrarListagensOrdenadas(leitor, hospital);
            case 7  -> alterarCapacidadeEnfermarias(leitor, hospital);
            case 8  -> gravarEstado(leitor, hospital);
            case 0  -> System.out.println("\nA sair...");
            default -> System.out.println("\n[AVISO] Opcao invalida! Tente novamente.");
        }
    }

    public static void carregarCSV(Scanner leitor, Hospital hospital) throws HospitalException, IOException {
        System.out.println("\n--- Carregar Dados de Ficheiros CSV ---");
        System.out.print("Diretorio dos ficheiros CSV: ");
        String diretorio = leitor.nextLine();

        validarDiretorioCSV(diretorio);
        GestorFicheiros.limparLog();

        System.out.println("A carregar enfermarias...");
        GestorFicheiros.carregarEnfermarias(new File(diretorio, "enfermarias.csv").getPath(), hospital);
        System.out.println("  Enfermarias carregadas: " + hospital.getEnfermarias().size());

        System.out.println("A carregar episodios...");
        GestorFicheiros.carregarEpisodios(new File(diretorio, "episodios.csv").getPath(), hospital);
        System.out.println("  Consulte 'erros_validacao.log' para entradas rejeitadas.");
    }

    public static void inserirEnfermaria(Scanner leitor, Hospital hospital) {
        System.out.println("\n--- Inserir Enfermaria Manualmente ---");
        System.out.print("Introduza o nome/ID da enfermaria: ");
        String id = leitor.nextLine();

        if (id.isEmpty()) {
            System.out.println("[ERRO] O nome nao pode estar vazio.");
            return;
        }
        if (hospital.obterEnfermaria(id) != null) {
            System.out.println("[ERRO] Ja existe uma enfermaria com esse nome.");
            return;
        }

        int capacidade = lerInteiro(leitor, "Capacidade (numero total de camas): ", 1, 1000);

        System.out.println("\nTipos de Enfermaria:");
        System.out.println("  1 - Geral | 2 - Psiquiatrica | 3 - Cuidados Intensivos");
        int tipo = lerInteiro(leitor, "Escolha o tipo (1-3): ", 1, 3);

        switch (tipo) {
            case 1 -> {
                int acompanhantes = lerInteiro(leitor, "Numero maximo de acompanhantes: ", 0, 100);
                System.out.print("Horario de visitas (ex: 14h-18h): ");
                hospital.adicionarEnfermaria(new modelo.EnfermariaGeral(id, capacidade, acompanhantes, leitor.nextLine()));
            }
            case 2 -> {
                System.out.print("Horario de visitas: ");
                String horario = leitor.nextLine();
                System.out.print("Nivel de seguranca (ex: ALTO, MEDIO): ");
                hospital.adicionarEnfermaria(new modelo.EnfermariaPsiquiatrica(id, capacidade, horario, leitor.nextLine()));
            }
            case 3 -> {
                System.out.print("Horario de visitas: ");
                String horario = leitor.nextLine();
                double pressao = lerDecimal(leitor, "Pressao atual: ");
                double pressaoRef = lerDecimal(leitor, "Pressao de referencia: ");
                hospital.adicionarEnfermaria(new modelo.EnfermariaCuidadosIntensivos(id, capacidade, horario, pressao, pressaoRef));
            }
        }
        System.out.println("[SUCESSO] Enfermaria adicionada.");
    }
