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



// MÉTODOS DE ARRANQUE E LEITURA

public static void configurarArranque(Scanner leitor, Hospital hospital) {
    System.out.println(SEPARADOR);
    System.out.println("  Arranque do Sistema (Iteracao 2)");
    System.out.println(SEPARADOR);
    System.out.println("  1 - Criacao automatica de objetos predefinidos no codigo");
    System.out.println("  2 - Iniciar o Hospital vazio ");
    System.out.println(SEPARADOR);

    int escolha = lerInteiro(leitor, "Opcao de arranque: ", 1, 2);

    if (escolha == 1) {
        System.out.println("A carregar dados predefinidos ...");
        carregarDadosPredefinidos(hospital);
        System.out.println("Dados predefinidos carregados com sucesso.");
    }
}

    public static int lerInteiro(Scanner leitor, String mensagem, int min, int max) {
        int valor = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            String input = leitor.nextLine();

            try {
                valor = Integer.parseInt(input);

                if (valor >= min && valor <= max) {
                    entradaValida = true;
                } else {
                    System.out.println("[ERRO] Valor deve estar entre " + min + " e " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Entrada invalida. Introduza um numero inteiro.");
            }
        }
        return valor;
    }

    public static double lerDecimal(Scanner leitor, String mensagem) {
        double valor = 0.0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            String input = leitor.nextLine();

            try {
                valor = Double.parseDouble(input);
                entradaValida = true;
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Introduza um numero decimal valido (use ponto '.' em vez de virgula).");
            }
        }
        return valor;
    }

    public static LocalDate lerData(Scanner leitor, String mensagem) {
        LocalDate data = null;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print(mensagem);
            String input = leitor.nextLine();
            try {
                data = LocalDate.parse(input);
                entradaValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("[ERRO] Formato invalido. Use " + FORMATO_DATA_ESPERADO + ".");
            }
        }
        return data;
    }


    // OPÇÕES DO MENU

    public static void carregarCSV(Scanner leitor, Hospital hospital) throws HospitalException, IOException {
        System.out.println("\n--- Carregar Dados de Ficheiros CSV ---");
        System.out.print("Diretorio dos ficheiros CSV: ");
        String diretorio = leitor.nextLine();

        validarDiretorioCSV(diretorio);
        GestorFicheiros.limparLog();

        System.out.println("A carregar enfermarias...");
        GestorFicheiros.carregarEnfermarias(new File(diretorio, NOME_FICHEIRO_ENFERMARIAS).getPath(), hospital);
        System.out.println("  Enfermarias carregadas: " + hospital.getEnfermarias().size());

        System.out.println("A carregar episodios...");
        GestorFicheiros.carregarEpisodios(new File(diretorio, NOME_FICHEIRO_EPISODIOS).getPath(), hospital);
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

        int capacidade = lerInteiro(leitor, "Capacidade (numero total de camas): ", 1, CAPACIDADE_MAXIMA_CAMAS);

        System.out.println("\nTipos de Enfermaria:");
        System.out.println("  1 - Geral | 2 - Psiquiatrica | 3 - Cuidados Intensivos");
        int tipo = lerInteiro(leitor, "Escolha o tipo (1-3): ", 1, 3);

        switch (tipo) {
            case 1 -> {
                int acompanhantes = lerInteiro(leitor, "Numero maximo de acompanhantes: ", 0, MAX_ACOMPANHANTES);
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

    public static void inserirEpisodio(Scanner leitor, Hospital hospital) {
        System.out.println("\n--- Inserir Episodio Manualmente ---");
        if (hospital.getEnfermarias().isEmpty()) {
            System.out.println("[ERRO] Nao existem enfermarias no hospital.");
            return;
        }

        System.out.print("ID da Enfermaria de destino: ");
        String idEnfermaria = leitor.nextLine();
        Enfermaria enfermaria = hospital.obterEnfermaria(idEnfermaria);

        if (enfermaria == null) {
            System.out.println("[ERRO] Enfermaria nao encontrada.");
            return;
        }

        System.out.print("ID da Cama: ");
        String idCama = leitor.nextLine();
        LocalDate dataAdmissao = lerData(leitor, "Data de Admissao (" + FORMATO_DATA_ESPERADO + "): ");
        Episodio episodio = new Episodio(idCama, dataAdmissao);

        System.out.print("O paciente ja teve alta? (S/N): ");
        String respostaAlta = leitor.nextLine();

        while (!respostaAlta.equals("S") && !respostaAlta.equals("s") && !respostaAlta.equals("N") && !respostaAlta.equals("n")) {
            System.out.print("[ERRO] Resposta invalida. Responda apenas com S ou N: ");
            respostaAlta = leitor.nextLine();
        }

        if (respostaAlta.equals("S") || respostaAlta.equals("s")) {
            LocalDate dataAlta = lerData(leitor, "Data de Alta (" + FORMATO_DATA_ESPERADO + "): ");
            if (!dataAlta.isBefore(dataAdmissao)) {
                episodio.darAlta(dataAlta);
            } else {
                System.out.println("[ERRO] Data de alta invalida. Alta ignorada.");
            }
        }

        enfermaria.adicionarEpisodio(episodio);
        System.out.println("[SUCESSO] Episodio adicionado.");
    }

    public static void mostrarIndicadoresOcupacao(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Tabela de Ocupacao e Graficos ASCII ---");
        validarHospitalNaoVazio(hospital);

        LocalDate data = lerData(leitor, "Data de referencia (" + FORMATO_DATA_ESPERADO + "): ");

        System.out.println("\nEnfermaria | Data       | Ocupadas/Totais | PercOcup | Grafico de Ocupacao");
        System.out.println(SEPARADOR_TABELA);

        List<Enfermaria> lista = hospital.getEnfermarias();
        for (int i = 0; i < lista.size(); i++) {
            Enfermaria enf = lista.get(i); // Vai buscar a enfermaria na posição i

            int ocupadas = enf.getOcupacaoAbsoluta(data);
            int totais = enf.getNumeroCamas();
            double taxa = enf.getTaxaOcupacao(data);

            System.out.printf("%-10s | %-10s | %8d/%-6d | %7.1f%% | %s%n",
                    enf.getIdentificador(), data.toString(), ocupadas, totais, taxa, gerarBarraAscii(taxa));
        }
    }

    public static void analisarPressaoIntervalo(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Analise de Pressao por Intervalo ---");
        validarHospitalNaoVazio(hospital);

        LocalDate inicio = lerData(leitor, "Data de inicio (" + FORMATO_DATA_ESPERADO + "): ");
        LocalDate fim    = lerData(leitor, "Data de fim   (" + FORMATO_DATA_ESPERADO + "): ");
        validarIntervalo(inicio, fim);

        List<Enfermaria> lista = hospital.getEnfermarias();
        for (int i = 0; i < lista.size(); i++) {
            Enfermaria enf = lista.get(i);
            System.out.printf("%nEnfermaria %s:%n", enf.getIdentificador());
            AnalisadorEstatistico.analisarPressaoPorIntervalo(enf, inicio, fim);
        }
    }

    public static void mostrarListagensOrdenadas(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Listagens Ordenadas ---");
        validarHospitalNaoVazio(hospital);

        LocalDate data = lerData(leitor, "Data de referencia (" + FORMATO_DATA_ESPERADO + "): ");

        System.out.println("\nEnfermarias por Taxa de Ocupacao (decrescente):");

        List<Enfermaria> ordenadas = hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(data);
        for (int i = 0; i < ordenadas.size(); i++) {
            Enfermaria enf = ordenadas.get(i);
            System.out.printf("  %-6s | Taxa: %5.1f%% | %s%n",
                    enf.getIdentificador(), enf.getTaxaOcupacao(data), enf.emPressao(data) ? "Em pressao" : "Normal");
        }
    }

    public static void alterarCapacidadeEnfermarias(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Alterar Capacidade das Enfermarias ---");
        validarHospitalNaoVazio(hospital);

        // O lerDecimal já garante que o valor devolvido é um double válido, lidando com os try-catch internamente
        double percentagem = lerDecimal(leitor, "Percentagem de variacao (ex: 10 para +10%, -20.5 para -20.5%): ");

        Enfermaria.alterarCapacidade(hospital.getEnfermarias(), percentagem);
        System.out.printf("Capacidade ajustada em %.1f%%.%n", percentagem);
    }

    public static void gravarEstado(Scanner leitor, Hospital hospital) throws HospitalException, IOException {
        System.out.println("\n--- Gravar Estado do Hospital ---");
        validarHospitalNaoVazio(hospital);

        System.out.print("Nome do ficheiro de gravacao (ex: hospital.dat): ");
        String ficheiro = leitor.nextLine();
        if (ficheiro.isEmpty()) throw new HospitalException("O nome do ficheiro nao pode estar vazio.");

        GestorSerializacao.gravarEstado(hospital, ficheiro);
        System.out.println("Estado gravado com sucesso.");
    }

    public static Hospital carregarEstado(Scanner leitor) throws HospitalException, IOException {
        System.out.println("\n--- Carregar Estado do Hospital ---");
        System.out.print("Nome do ficheiro a carregar (ex: hospital.dat): ");
        String ficheiro = leitor.nextLine();

        if (ficheiro.isEmpty() || !new File(ficheiro).exists()) {
            throw new HospitalException("Ficheiro invalido ou nao encontrado.");
        }

        Hospital hospitalCarregado = GestorSerializacao.carregarEstado(ficheiro);
        System.out.println("Estado carregado com sucesso. Enfermarias: " + hospitalCarregado.getEnfermarias().size());
        return hospitalCarregado;
    }




}
