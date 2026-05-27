package main.main;

import main.io.GestorFicheiros;
import main.modelo.Hospital;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Classe principal da aplicação.
 */
public class MainHospital {

    private static final String SEPARADOR = "=".repeat(60);

    public static void main(String[] args) throws IOException {
        Scanner leitor = new Scanner(System.in);

        System.out.println(SEPARADOR);
        System.out.println("  Hospital XYZ - Sistema de Monitorizacao de Camas");
        System.out.println(SEPARADOR);
        System.out.println("\n--- CONFIGURAÇÃO INICIAL (CARREGAMENTO DE DADOS) ---");

        // 1. Mantém a tua lógica segura de pedir o diretório dos CSVs
        String diretorioCsv = lerDiretorioCsv(leitor);

        // Inicializa o contentor principal
        Hospital hospital = new Hospital("Hospital Central XYZ");
        GestorFicheiros.limparLog();

        // 2. Carrega os dados dos ficheiros para a memória
        System.out.println("A carregar enfermarias do ficheiro CSV...");
        GestorFicheiros.carregarEnfermarias(construirCaminho(diretorioCsv, "enfermarias.csv"), hospital);
        System.out.println("     Enfermarias carregadas: " + hospital.getEnfermarias().size());

        System.out.println("A carregar episodios do ficheiro CSV...");
        GestorFicheiros.carregarEpisodios(construirCaminho(diretorioCsv, "episodios.csv"), hospital);
        System.out.println("     Configuração concluída com sucesso.");
        System.out.println(SEPARADOR);

        // 3. Passa o controlo da aplicação para a nova classe Menu (RF1)
        Menu menu = new Menu(hospital, leitor);
        menu.exibirMenuPrincipal();

        // Quando o ciclo do menu terminar (opção 0), fecha o leitor
        leitor.close();
        System.out.println("\n" + SEPARADOR);
        System.out.println("  Programa terminado.");
        System.out.println(SEPARADOR);
    }

    /**
     * Lê o diretório onde estão os ficheiros CSV.
     */
    public static String lerDiretorioCsv(Scanner leitor) {
        while (true) {
            System.out.print("Introduza o diretorio dos CSV: ");
            String diretorio = leitor.nextLine().trim();

            if (diretorio.isEmpty()) {
                System.out.println("Diretorio invalido.");
            } else {
                File pasta = new File(diretorio);
                File ficheiroEnfermarias = new File(pasta, "enfermarias.csv");
                File ficheiroEpisodios = new File(pasta, "episodios.csv");

                if (!pasta.exists() || !pasta.isDirectory()) {
                    System.out.println("O diretorio indicado nao existe.");
                } else if (!ficheiroEnfermarias.exists() || !ficheiroEpisodios.exists()) {
                    System.out.println("O diretorio tem de conter os ficheiros enfermarias.csv e episodios.csv.");
                } else {
                    return diretorio;
                }
            }
        }
    }

    /**
     * Lê uma data introduzida pelo utilizador.
     */
    public static LocalDate lerData(Scanner leitor, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String texto = leitor.nextLine().trim();

            if (GestorFicheiros.validarData(texto)) {
                return LocalDate.parse(texto);
            } else {
                System.out.println("Data invalida. Use o formato AAAA-MM-DD.");
            }
        }
    }

    /**
     * Lê a data final do intervalo.
     */
    public static LocalDate lerDataFim(Scanner leitor, LocalDate dataInicio) {
        while (true) {
            LocalDate dataFim = lerData(leitor, "Introduza a data final do intervalo (AAAA-MM-DD): ");
            if (dataFim.isBefore(dataInicio)) {
                System.out.println("A data final nao pode ser anterior a data inicial.");
            } else {
                return dataFim;
            }
        }
    }

    /**
     * Constrói o caminho completo de um ficheiro.
     */
    public static String construirCaminho(String diretorio, String nomeFicheiro) {
        return new File(diretorio, nomeFicheiro).getPath();
    }
}