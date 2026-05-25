package main;

import modelo.Hospital;
import modelo.Enfermaria;
import java.util.Scanner;

public class Menu {

    private static final String SEP  = "=".repeat(60);
    private static final String LINE = "-".repeat(60);

    private final Hospital hospital;
    private final Scanner leitor;
    private final LeitorConsola lc;

    public Menu(Hospital hospital, Scanner leitor) {
        this.hospital = hospital;
        this.leitor   = leitor;
        this.lc       = new LeitorConsola(leitor);
    }

    public void exibirMenuPrincipal() {
        boolean running = true;
        while (running) {
            printMenu();
            int op = lerOpcao(0, 9);
            switch (op) {
                case 1 -> menuInserirDados();
                case 2 -> menuTabela();
                case 3 -> menuAlterarCamas();
                case 4 -> menuPressao();
                case 5 -> menuRanking();
                case 6 -> menuLoS();
                case 7 -> menuGraficos();
                case 8 -> menuFicheiros();
                case 9 -> listarEnfermarias();
                case 0 -> running = false;
            }
        }
    }

    // ---------- menu principal ----------

    private void printMenu() {
        System.out.println("\n" + SEP);
        System.out.println("  " + hospital.getNome());
        System.out.println(SEP);
        System.out.println("  1. Inserir dados");
        System.out.println("  2. Ver tabela de ocupação");
        System.out.println("  3. Alterar nº de camas (%)");
        System.out.println("  4. Enfermarias em pressão");
        System.out.println("  5. Ranking índice de pressão");
        System.out.println("  6. Estatísticas LoS");
        System.out.println("  7. Gráficos de barras");
        System.out.println("  8. Guardar / Carregar dados");
        System.out.println("  9. Listar enfermarias");
        System.out.println("  0. Sair");
        System.out.println(LINE);
    }
    /**
     * Encaminha a opção escolhida para o método correspondente.
     */
    // ---------- RF2 - inserir dados ----------

    private void menuInserirDados() {
        System.out.println("\n" + SEP);
        System.out.println("  INSERIR DADOS");
        System.out.println(LINE);
        System.out.println("  1. Nova enfermaria");
        System.out.println("  2. Novo episódio");
        System.out.println("  0. Voltar");

        int op = lerOpcao(0, 2);
        if (op == 1) novaEnfermaria();
        else if (op == 2) novoEpisodio();
    }
    /**
     * Opção 1: Listagem simples de controlo das enfermarias em memória.
     */
    private void ejecutarListagemEnfermarias() {
        System.out.println("\n--- ENFERMARIAS EM MEMÓRIA ---");
        if (hospital.getEnfermarias().isEmpty()) {
            System.out.println("Nao existem enfermarias carregadas.");
            return;
        }
        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.println(enf);
        }
    }

    /**
     * Opção 4: Executa o Requisito Funcional 4 (Alterar Capacidade).
     */
    private void executarAlterarCapacidade() {
        System.out.println("\n--- ALTERAR CAPACIDADE DAS ENFERMARIAS (RF4) ---");
        System.out.print("Introduza a percentagem de alteracao (ex: 10 para aumentar, -5 para diminuir): ");

        try {
            double percentagem = Double.parseDouble(leitor.nextLine().trim());

            // Invoca o método estático que criámos na classe Enfermaria
            Enfermaria.alterarCapacidade(hospital.getEnfermarias(), percentagem);

            System.out.println("\n[SUCESSO] Capacidade de todas as enfermarias alterada com sucesso!");

        } catch (NumberFormatException e) {
            System.out.println("\n[ERRO] Percentagem invalida. Introduza um valor numerico.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERRO] Falha na validacao: " + e.getMessage());
        }
    }

    }

