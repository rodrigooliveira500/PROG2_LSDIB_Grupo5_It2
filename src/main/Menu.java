package main;

import exceptions.HospitalException;
import io.GestorMenu;
import modelo.Hospital;

import java.io.IOException;
import java.util.Scanner;

public class Menu {

    private static final String SEPARADOR = "-".repeat(60);
    private Hospital hospital;
    private final Scanner leitor;

    public Menu(Hospital hospital, Scanner leitor) {
        this.hospital = hospital;
        this.leitor = leitor;
    }

    public void executar() throws IOException {
        int opcao = -1;
        while (opcao != 0) {
            apresentarMenu();
            opcao = GestorMenu.lerInteiro(leitor, "Opcao: ", 0, 10);
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
                    case 9  -> this.hospital = GestorMenu.carregarEstado(leitor);
                    case 10 -> GestorMenu.renomearEnfermaria(leitor, hospital);
                    case 0  -> System.out.println("\nA sair...");
                    default -> System.out.println("\n[AVISO] Opcao invalida.");
                }
            } catch (HospitalException e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        }
    }

    private void apresentarMenu() {
        System.out.println("\n" + SEPARADOR);
        System.out.println("  Hospital XYZ - Menu Principal");
        System.out.println(SEPARADOR);
        System.out.println("  1 - Carregar dados de ficheiros CSV");
        System.out.println("  2 - Inserir enfermaria manualmente");
        System.out.println("  3 - Inserir episodio manualmente");
        System.out.println("  4 - Indicadores de ocupacao");
        System.out.println("  5 - Analise de pressao por intervalo");
        System.out.println("  6 - Listagens ordenadas");
        System.out.println("  7 - Alterar capacidade das enfermarias (%)");
        System.out.println("  8 - Gravar estado do hospital");
        System.out.println("  9 - Carregar estado do hospital");
        System.out.println(" 10 - Renomear enfermaria");
        System.out.println("  0 - Sair");
        System.out.println(SEPARADOR);
    }
}