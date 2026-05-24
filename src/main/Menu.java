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
                System.out.println("\n[ERRO] Por favor, introduza um numero inteiro valido.");
            }
        }
    }

    }