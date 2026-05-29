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
    private static final double LIMIAR_PRESSAO         = 85.0;




// MÉTODOS DE ARRANQUE E LEITURA

    public static Hospital configurarArranque(Scanner leitor, String nomeHospital) {
        System.out.println(SEPARADOR);
        System.out.println("  Arranque do Sistema (Iteracao 2)");
        System.out.println(SEPARADOR);
        System.out.println("  1 - Criacao automatica de objetos predefinidos no codigo");
        System.out.println("  2 - Iniciar o Hospital vazio");
        System.out.println("  3 - Carregar estado do hospital (.dat)");
        System.out.println(SEPARADOR);

        int escolha = lerInteiro(leitor, "Opcao de arranque: ", 1, 3);

        // Criamos um hospital base vazio primeiro
        Hospital hospital = new Hospital(nomeHospital);

        if (escolha == 1) {
            System.out.println("A carregar dados predefinidos...");
            carregarDadosPredefinidos(hospital);
            System.out.println("Dados predefinidos carregados com sucesso.");

        } else if (escolha == 3) {
            try {
                // Reutilizamos o método da Opção 11. Se correr bem, substitui a variável.
                hospital = carregarEstado(leitor);
            } catch (Exception e) {
                System.out.println("[ERRO] Falha ao carregar ficheiro binario: " + e.getMessage());
                System.out.println("A arrancar com o hospital vazio por seguranca.");
            }
        }

        // Se escolheu 2, devolve o hospital vazio criado ali em cima.
        return hospital;
    }


    // LEITURA DE INPUT


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


    // OPÇÕES DO MENU (1 a 11)

    // OPÇÃO 1
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

    // OPÇÃO 2
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

    // OPÇÃO 3
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


// Opção 4
    // RF3 + RF8 — gráficos de ocupacao (tabela + barras ASCII)
public static void mostrarGraficosOcupacao(Scanner leitor, Hospital hospital) throws HospitalException {
    System.out.println("\n--- Graficos de Ocupacao (ASCII) ---");
    validarHospitalNaoVazio(hospital);

    System.out.println("\nEscolha o formato do grafico:");
    System.out.println("  1 - Tabela de Evolucao (1 Enfermaria, Intervalo de datas)");
    System.out.println("  2 - Barras Horizontais (Todas as enfermarias, 1 Data)");
    System.out.println("  3 - Barras Verticais   (Todas as enfermarias, 1 Data)");

    int tipo = lerInteiro(leitor, "Opcao (1-3): ", 1, 3);

    System.out.print("Simbolo para preencher o grafico (ex: #, *, =): ");
    String inputSimbolo = leitor.nextLine();

     // Se o utilizador der apenas 'Enter', usa '#' por defeito
    char simbolo = inputSimbolo.isEmpty() ? '#' : inputSimbolo.charAt(0);

    if (tipo == 1) {
        System.out.print("ID da Enfermaria: ");
        String idEnfermaria = leitor.nextLine();
        LocalDate inicio = lerData(leitor, "Data de inicio (" + FORMATO_DATA_ESPERADO + "): ");
        LocalDate fim    = lerData(leitor, "Data de fim   (" + FORMATO_DATA_ESPERADO + "): ");
        validarIntervalo(inicio, fim);
        hospital.exibirTabelaHorizontal(idEnfermaria, inicio, fim, simbolo);

    } else if (tipo == 2) {
        LocalDate data = lerData(leitor, "Data de referencia (" + FORMATO_DATA_ESPERADO + "): ");
        hospital.exibirBarrasHorizontais(data, simbolo);

    } else {
        LocalDate data = lerData(leitor, "Data de referencia (" + FORMATO_DATA_ESPERADO + "): ");
        hospital.exibirBarrasVerticais(data, simbolo);
    }
}

    // OPÇÃO 5
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

    // OPÇÃO 6
    public static void mostrarListagensOrdenadas(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Listagens Ordenadas ---");
        validarHospitalNaoVazio(hospital);

        LocalDate data = lerData(leitor, "Data de referencia (" + FORMATO_DATA_ESPERADO + "): ");

        System.out.println("\nEnfermarias por Taxa de Ocupacao (decrescente):");
        for (Enfermaria enf : hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(data)) {
            System.out.printf("  %-10s | Taxa: %5.1f%% | %s%n",
                    enf.getIdentificador(),
                    enf.getTaxaOcupacao(data),
                    enf.emPressao(data) ? "Em pressao" : "Normal");
        }
    }

    // OPÇÃO 7
    // RF4 — alterar capacidade (chama método static de IndicePressao)
    public static void alterarCapacidadeEnfermarias(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Alterar Capacidade das Enfermarias ---");
        validarHospitalNaoVazio(hospital);

        // O lerDecimal já garante que o valor devolvido é um double válido, lidando com os try-catch internamente
        double percentagem = lerDecimal(leitor, "Percentagem de variacao (ex: 10 para +10%, -20.5 para -20.5%): ");

        Enfermaria.alterarCapacidade(hospital.getEnfermarias(), percentagem);
        System.out.printf("Capacidade ajustada em %.1f%%.%n", percentagem);
    }
    // OPÇÃO 8
    // RF5 — percentagem de enfermarias em pressao
    public static void mostrarEnfermariasEmPressao(Scanner leitor, Hospital hospital) throws HospitalException {
        System.out.println("\n--- Percentagem de Enfermarias em Pressao ---");
        validarHospitalNaoVazio(hospital);

        LocalDate data = lerData(leitor, "Data de referencia (" + FORMATO_DATA_ESPERADO + "): ");

        List<Enfermaria> lista = hospital.getEnfermarias();
        int emPressaoCount = 0;
        int total = lista.size();

        for (int i = 0; i < total; i++) {
            Enfermaria enf = lista.get(i);
            if (enf.emPressao(data)) {
                emPressaoCount++;
            }
        }

        double percentagem = (emPressaoCount * 100.0) / total;
        System.out.printf("Estado em %s: %d de %d enfermarias estao em pressao (%.1f%%).%n",
                data, emPressaoCount, total, percentagem);
    }

// Opção 9
    // RF6 — ranking indice de pressao
// OPÇÃO 9
public static void mostrarRankingIndicePressao(Scanner leitor, Hospital hospital) throws HospitalException {
    System.out.println("\n--- Ranking de Indice de Pressao ---");
    validarHospitalNaoVazio(hospital);

    LocalDate inicio = lerData(leitor, "Data de inicio (" + FORMATO_DATA_ESPERADO + "): ");
    LocalDate fim    = lerData(leitor, "Data de fim   (" + FORMATO_DATA_ESPERADO + "): ");
    validarIntervalo(inicio, fim);

    List<Enfermaria> lista = hospital.getEnfermarias();
    int n = lista.size();

    // Ordenação Bubble Sort (Decrescente)
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            Enfermaria enf1 = lista.get(j);
            Enfermaria enf2 = lista.get(j + 1);

            double taxa1 = enf1.getPercentagemDiasEmPressao(inicio, fim);
            double taxa2 = enf2.getPercentagemDiasEmPressao(inicio, fim);

            if (taxa1 < taxa2) {
                lista.set(j, enf2);
                lista.set(j + 1, enf1);
            }
        }
    }

    System.out.println("\nRanking (Dias em pressao no intervalo):");
    for (int i = 0; i < lista.size(); i++) {
        Enfermaria enf = lista.get(i);
        double taxa = enf.getPercentagemDiasEmPressao(inicio, fim);
        System.out.printf("  %dº - %-10s | %5.1f%% dos dias%n", (i + 1), enf.getIdentificador(), taxa);
    }
}


    // RF7 — serialização
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

    // MÉTODOS AUXILIARES E VALIDAÇÕES

    public static void carregarDadosPredefinidos(Hospital hospital) {
        Enfermaria e1 = new modelo.EnfermariaGeral("GERAL_1", 30, 2, "14h-19h");
        Enfermaria e2 = new modelo.EnfermariaPsiquiatrica("PSI_1", 10, "10h-12h", "ALTO");
        hospital.adicionarEnfermaria(e1);
        hospital.adicionarEnfermaria(e2);
        e1.adicionarEpisodio(new Episodio("CAMA_1", LocalDate.now().minusDays(5)));
    }

    private static void validarDiretorioCSV(String diretorio) throws HospitalException {
        File pasta = new File(diretorio);
        if (!pasta.exists() || !pasta.isDirectory() ||
                !new File(pasta, NOME_FICHEIRO_ENFERMARIAS).exists() ||
                !new File(pasta, NOME_FICHEIRO_EPISODIOS).exists()) {
            throw new HospitalException("Diretorio invalido ou ficheiros CSV em falta.");
        }
    }

    private static void validarHospitalNaoVazio(Hospital hospital) throws HospitalException {
        if (hospital == null || hospital.getEnfermarias().isEmpty()) {
            throw new HospitalException("Nao existem enfermarias carregadas no hospital.");
        }
    }

    private static void validarIntervalo(LocalDate inicio, LocalDate fim) throws HospitalException {
        if (inicio.isAfter(fim)) throw new HospitalException("A data de inicio nao pode ser posterior ao fim.");
    }

}




