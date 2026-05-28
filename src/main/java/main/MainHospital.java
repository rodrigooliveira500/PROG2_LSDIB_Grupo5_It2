package main;

import io.GestorMenu;
import modelo.Hospital;

import java.util.Scanner;

public class Main {

    private static final String SEPARADOR = "-".repeat(60);
    private static final int OPCAO_MAXIMA = 9;

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Hospital hospital = new Hospital("Hospital XYZ");

        // Cumpre o requisito de arranque inicial sem sujar o Main
        GestorMenu.configurarArranque(leitor, hospital);

        int opcao = -1;

        while (opcao != 0) {
            printMenu(hospital.getNome());
            // CORREÇÃO: Chama o GestorMenu e não o GestorConsola apagado
            opcao = GestorMenu.lerInteiro(leitor, "Opcao: ", 0, OPCAO_MAXIMA);

            try {
                switch (opcao) {
                    case 1  -> GestorMenu.carregarCSV(leitor, hospital);
                    case 2  -> GestorMenu.inserirEnfermaria(leitor, hospital);
                    case 3  -> GestorMenu.inserirEpisodio(leitor, hospital);
                    case 4  -> GestorMenu.mostrarIndicadoresOcupacao(leitor, hospital);
                    case 5  -> GestorMenu.analisarPressaoIntervalo(leitor, hospital);
                    case 6  -> GestorMenu.mostrarListagensOrdenadas(leitor, hospital);
                    case 7  -> GestorMenu.alterarCapacidadeEnfermarias(leitor, hospital);
                    case 8  -> GestorMenu.gravarEstado(leitor, hospital);
                    case 9  -> hospital = GestorMenu.carregarEstado(leitor); // Atualiza a variável no Main
                    case 0  -> System.out.println("\nA sair... Ate logo!");
                    default -> System.out.println("\n[AVISO] Opcao invalida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        }
        leitor.close();
    }

    private static void printMenu(String nomeHospital) {
        System.out.println("\n" + SEPARADOR);
        System.out.println("  " + nomeHospital + " - Menu Principal");
        System.out.println(SEPARADOR);
        System.out.println("  1 - Carregar dados de ficheiros CSV");
        System.out.println("  2 - Inserir enfermaria manualmente");
        System.out.println("  3 - Inserir episodio manualmente");
        System.out.println("  4 - Indicadores de ocupacao (Graficos ASCII)");
        System.out.println("  5 - Analise de pressao por intervalo");
        System.out.println("  6 - Listagens ordenadas");
        System.out.println("  7 - Alterar capacidade das enfermarias (%)");
        System.out.println("  8 - Gravar estado do hospital");
        System.out.println("  9 - Carregar estado do hospital");
        System.out.println("  0 - Sair");
        System.out.println(SEPARADOR);
    }
}