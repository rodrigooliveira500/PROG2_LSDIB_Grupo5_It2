package main;

import modelo.Hospital;
import modelo.Enfermaria;
import java.util.Scanner;

/**
 * Classe responsável pela interface de utilizador e controlo do menu.
 */
public class Menu {

    private final Hospital hospital;
    private final Scanner leitor;
    private static final String SEPARADOR = "-".repeat(50);

    /**
     * Construtor do Menu.
     *
     * @param hospital Instância do hospital com os dados carregados
     * @param leitor   Instância do Scanner partilhada com o Main
     */
    public Menu(Hospital hospital, Scanner leitor) {
        this.hospital = hospital;
        this.leitor = leitor;
    }

    /**
     * Exibe o menu principal e gere o ciclo de escolhas do utilizador.
     */
    public void exibirMenuPrincipal() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n" + SEPARADOR);
            System.out.println("                MENU PRINCIPAL");
            System.out.println(SEPARADOR);
            System.out.println("1 - Listar Enfermarias (Exemplo)");
            System.out.println("4 - Alterar Capacidade de Enfermarias (RF4)");
            System.out.println("0 - Sair");
            System.out.println(SEPARADOR);
            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(leitor.nextLine().trim());
                processarOpcao(opcao);
            } catch (NumberFormatException e) {
                System.out.println("\n Por favor, introduza um numero inteiro valido.");
            }
        }
    }

    /**
     * Encaminha a opção escolhida para o método correspondente.
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                executarListagemEnfermarias();
                break;
            case 4:
                executarAlterarCapacidade();
                break;
            case 0:
                System.out.println("\nA sair do menu...");
                break;
            default:
                System.out.println("\n[AVISO] Opcao invalida! Tente novamente.");
        }
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

