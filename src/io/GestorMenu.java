package io;

import exceptions.HospitalException;
import modelo.Enfermaria;
import modelo.Hospital;
import utils.AnalisadorEstatistico;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class GestorMenu {

    public static int lerInteiro(Scanner leitor, String mensagem, int min, int max) {
        while (true) {
            System.out.print(mensagem);
            if (leitor.hasNextInt()) {
                int valor = leitor.nextInt();
                leitor.nextLine();
                if (valor >= min && valor <= max) return valor;
                System.out.println("[ERRO] Valor deve estar entre " + min + " e " + max + ".");
            } else {
                System.out.println("[ERRO] Entrada invalida.");
                leitor.nextLine();
            }
        }
    }

    public static LocalDate lerData(Scanner leitor, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return LocalDate.parse(leitor.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("[ERRO] Formato invalido. Use AAAA-MM-DD.");
            }
        }
    }

    public static void carregarCSV(Scanner leitor, Hospital hospital) throws HospitalException, IOException {
        System.out.println("\n--- Carregar Dados ---");
        System.out.print("Diretorio: ");
        String diretorio = leitor.nextLine().trim();

        validarDiretorioCSV(diretorio);
        GestorFicheiros.limparLog();

        GestorFicheiros.carregarEnfermarias(new File(diretorio, "enfermarias.csv").getPath(), hospital);
        GestorFicheiros.carregarEpisodios(new File(diretorio, "episodios.csv").getPath(), hospital);
        System.out.println("Processo concluido.");
    }

    public static void mostrarIndicadoresOcupacao(Scanner leitor, Hospital hospital) throws HospitalException {
        validarHospitalNaoVazio(hospital);
        LocalDate data = lerData(leitor, "Data (AAAA-MM-DD): ");

        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("%n%s | Ocupacao: %d/%d | Taxa: %.1f%% | %s%n",
                    enf.getIdentificador(), enf.getOcupacaoAbsoluta(data),
                    enf.getNumeroCamas(), enf.getTaxaOcupacao(data),
                    enf.emPressao(data) ? "Em pressao" : "Normal");

            System.out.printf("Sumario LoS: %s%n", AnalisadorEstatistico.calculasEstatisticaLoS(enf));
        }
    }

    public static void analisarPressaoIntervalo(Scanner leitor, Hospital hospital) throws HospitalException {
        validarHospitalNaoVazio(hospital);
        LocalDate inicio = lerData(leitor, "Data inicio: ");
        LocalDate fim = lerData(leitor, "Data fim: ");
        validarIntervalo(inicio, fim);

        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("%n%s:%n", enf.getIdentificador());
            AnalisadorEstatistico.analisarPressaoPorIntervalo(enf, inicio, fim);
        }
    }

    public static void mostrarListagensOrdenadas(Scanner leitor, Hospital hospital) throws HospitalException {
        validarHospitalNaoVazio(hospital);
        LocalDate data = lerData(leitor, "Data (AAAA-MM-DD): ");

        List<Enfermaria> ordenadas = hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(data);
        for (Enfermaria enf : ordenadas) {
            System.out.printf("%s | Taxa: %.1f%%%n", enf.getIdentificador(), enf.getTaxaOcupacao(data));
        }
    }

    public static void alterarCapacidadeEnfermarias(Scanner leitor, Hospital hospital) throws HospitalException {
        validarHospitalNaoVazio(hospital);
        System.out.print("Percentagem de variacao: ");

        try {
            double percentagem = Double.parseDouble(leitor.nextLine().trim().replace(',', '.'));
            Enfermaria.alterarCapacidade(hospital.getEnfermarias(), percentagem);
            System.out.printf("Capacidade ajustada em %.1f%%.%n", percentagem);
        } catch (NumberFormatException e) {
            throw new HospitalException("Percentagem invalida.");
        }
    }

    public static void gravarEstado(Scanner leitor, Hospital hospital) throws HospitalException, IOException {
        validarHospitalNaoVazio(hospital);
        System.out.print("Ficheiro (.dat): ");
        String ficheiro = leitor.nextLine().trim();
        if (ficheiro.isBlank()) throw new HospitalException("Nome vazio.");

        GestorSerializacao.gravarEstado(hospital, ficheiro);
    }

    public static Hospital carregarEstado(Scanner leitor) throws HospitalException, IOException {
        System.out.print("Ficheiro (.dat): ");
        String ficheiro = leitor.nextLine().trim();
        if (!new File(ficheiro).exists()) throw new HospitalException("Ficheiro nao existe.");

        return GestorSerializacao.carregarEstado(ficheiro);
    }

    public static void inserirEnfermaria(Scanner leitor, Hospital hospital) { System.out.println("Em desenvolvimento."); }
    public static void inserirEpisodio(Scanner leitor, Hospital hospital) { System.out.println("Em desenvolvimento."); }
    public static void renomearEnfermaria(Scanner leitor, Hospital hospital) { System.out.println("Em desenvolvimento."); }

    public static void validarHospitalNaoVazio(Hospital hospital) throws HospitalException {
        if (hospital == null || hospital.getEnfermarias().isEmpty()) {
            throw new HospitalException("Hospital vazio.");
        }
    }

    private static void validarDiretorioCSV(String diretorio) throws HospitalException {
        File pasta = new File(diretorio);
        if (!pasta.exists() || !pasta.isDirectory()) throw new HospitalException("Diretorio invalido.");
        if (!new File(pasta, "enfermarias.csv").exists() || !new File(pasta, "episodios.csv").exists()) {
            throw new HospitalException("Ficheiros CSV em falta.");
        }
    }

    private static void validarIntervalo(LocalDate inicio, LocalDate fim) throws HospitalException {
        if (inicio.isAfter(fim)) throw new HospitalException("Intervalo invalido.");
    }
}