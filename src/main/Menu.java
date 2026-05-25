package main;

import modelo.Hospital;
import modelo.Enfermaria;
import java.util.Scanner;

/**
 * Classe responsável pela apresentação e gestão do menu principal da aplicação.
 * Liga todas as funcionalidades do sistema, utiliza exceções para tratar
 * situações inválidas e suporta serialização do estado do hospital.
 *
 * @author Grupo 5
 * @version 2.0
 */
public class Menu {

    private static final String SEPARADOR = "-".repeat(60);
    private static final int OPCAO_MAXIMA = 9;
    private final Hospital hospital;
    private final Scanner leitor;
    private final LeitorConsola lc;

    /**
     * Cria um novo menu para o hospital indicado.
     *
     * @param hospital hospital a gerir
     * @param leitor   scanner para leitura da consola
     */
    public Menu(Hospital hospital, Scanner leitor) {
        this.hospital = hospital;
        this.leitor   = leitor;
        this.lc  = new LeitorConsola(leitor);
    }

    public void executar() throws IOException {
        int opcao = -1;
        while (opcao != 0) {
            apresentarMenu();
            opcao = GestorConsola.lerInteiro(leitor, "Opcao: ", 0, OPCAO_MAXIMA);
            try {
                processarOpcao(opcao);
            } catch (HospitalException e) {
                System.out.println("[ERRO] " + e.getMessage());
            }
        }
    }

    /**
     * Processa a opção escolhida pelo utilizador, delegando para o método correspondente.
     * Lança {@link HospitalException} se a operação não for possível.
     *
     * @param opcao opção escolhida pelo utilizador
     * @throws HospitalException se ocorrer um erro de negócio
     * @throws IOException       se ocorrer erro de acesso a ficheiros
     */
    private void processarOpcao(int opcao) throws HospitalException, IOException {
        if (opcao == 1) {
            carregarCSV();
        } else if (opcao == 2) {
            GestorConsola.inserirEnfermaria(leitor, hospital);
        } else if (opcao == 3) {
            GestorConsola.inserirEpisodio(leitor, hospital);
        } else if (opcao == 4) {
            mostrarIndicadoresOcupacao();
        } else if (opcao == 5) {
            analisarPressaoIntervalo();
        } else if (opcao == 6) {
            mostrarListagensOrdenadas();
        } else if (opcao == 7) {
            alterarCapacidadeEnfermarias();
        } else if (opcao == 8) {
            gravarEstado();
        } else if (opcao == 9) {
            carregarEstado();
        } else if (opcao == 0) {
            System.out.println("\nA sair...");
        }
    }

    // ---------- menu principal ----------

    private void printMenu() {
        System.out.println("\n" + SEPARADOR);
        System.out.println("  " + hospital.getNome());
        System.out.println(SEPARADOR);
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
        System.out.println(SEPARADOR);
    }

    private void carregarCSV() throws HospitalException, IOException {
        System.out.println("\n--- Carregar Dados de Ficheiros CSV ---");
        System.out.print("Diretorio dos ficheiros CSV: ");
        String diretorio = leitor.nextLine().trim();

        validarDiretorioCSV(diretorio);

        GestorFicheiros.limparLog();

        System.out.println("A carregar enfermarias...");
        GestorFicheiros.carregarEnfermarias(
                new File(diretorio, "enfermarias.csv").getPath(), hospital);
        System.out.println("  Enfermarias carregadas: " + hospital.getEnfermarias().size());

        System.out.println("A carregar episodios...");
        GestorFicheiros.carregarEpisodios(
                new File(diretorio, "episodios.csv").getPath(), hospital);
        System.out.println("  Consulte 'erros_validacao.log' para entradas rejeitadas.");
    }
    /**
     * Valida se o diretório existe e contém os ficheiros CSV necessários.
     * Utilizado também nos testes unitários para verificar a lógica de validação.
     *
     * @param diretorio caminho do diretório a validar
     * @throws HospitalException se o diretório não existir ou faltar algum ficheiro CSV
     */
    public static void validarDiretorioCSV(String diretorio) throws HospitalException {
        if (diretorio == null || diretorio.isBlank()) {
            throw new HospitalException("O caminho do diretorio nao pode estar vazio.");
        }

        File pasta = new File(diretorio);
        if (!pasta.exists() || !pasta.isDirectory()) {
            throw new HospitalException("O diretorio '" + diretorio + "' nao existe.");
        }

        if (!new File(pasta, "enfermarias.csv").exists()) {
            throw new HospitalException("Ficheiro 'enfermarias.csv' nao encontrado em: " + diretorio);
        }

        if (!new File(pasta, "episodios.csv").exists()) {
            throw new HospitalException("Ficheiro 'episodios.csv' nao encontrado em: " + diretorio);
        }
    }

/**
 * Apresenta os indicadores de ocupação de todas as enfermarias numa data introduzida.
 * Lança exceção se não existirem enfermarias carregadas.
 *
 * @throws HospitalException se não existirem enfermarias no hospital
 */

private void mostrarIndicadoresOcupacao() throws HospitalException {
    System.out.println("\n--- Indicadores de Ocupacao ---");
    validarHospitalNaoVazio();

    LocalDate data = GestorConsola.lerData(leitor, "Data de referencia (AAAA-MM-DD): ");
    validarData(data);

    System.out.println("\n" + SEPARADOR);
    System.out.printf("  Ocupacao em %s%n", data);
    System.out.println(SEPARADOR);

    for (Enfermaria enf : hospital.getEnfermarias()) {
        System.out.printf("%n  Enfermaria : %s%n", enf.getIdentificador());
        System.out.printf("  Ocupacao   : %d / %d camas%n", enf.getOcupacaoAbsoluta(data), enf.getNumeroCamas());
        System.out.printf("  Taxa       : %.1f%%%n", enf.getTaxaOcupacao(data));
        System.out.printf("  Estado     : %s%n", enf.emPressao(data) ? "Em pressao" : "Estado normal");
    }

    System.out.println("\n" + SEPARADOR);
    System.out.println("  Sumario de LoS por Enfermaria");
    System.out.println(SEPARADOR);

    for (Enfermaria enf : hospital.getEnfermarias()) {
        AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calculasEstatisticaLoS(enf);
        System.out.printf("  %s: %s%n", enf.getIdentificador(), sumario);
    }
}
    /**
     * Solicita um intervalo de datas e apresenta a análise de pressão diária.
     * Lança exceção se não existirem enfermarias ou se o intervalo for inválido.
     *
     * @throws HospitalException se não existirem enfermarias ou o intervalo for inválido
     */
    private void analisarPressaoIntervalo() throws HospitalException {
        System.out.println("\n--- Analise de Pressao por Intervalo ---");
        validarHospitalNaoVazio();

        LocalDate inicio = GestorConsola.lerData(leitor, "Data de inicio (AAAA-MM-DD): ");
        LocalDate fim    = GestorConsola.lerData(leitor, "Data de fim   (AAAA-MM-DD): ");

        validarIntervalo(inicio, fim);

        System.out.println();
        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("%nEnfermaria %s:%n", enf.getIdentificador());
            AnalisadorEstatistico.analisarPressaoPorIntervalo(enf, inicio, fim);
        }
    }

    /**
     * Apresenta as listagens de enfermarias e episódios ordenados.
     * Lança exceção se não existirem enfermarias carregadas.
     *
     * @throws HospitalException se não existirem enfermarias no hospital
     */
    private void mostrarListagensOrdenadas() throws HospitalException {
        System.out.println("\n--- Listagens Ordenadas ---");
        validarHospitalNaoVazio();

        LocalDate data = GestorConsola.lerData(leitor, "Data de referencia (AAAA-MM-DD): ");
        validarData(data);

        System.out.println("\n" + SEPARADOR);
        System.out.printf("  Enfermarias por Taxa de Ocupacao em %s (decrescente)%n", data);
        System.out.println(SEPARADOR);

        List<Enfermaria> ordenadas = hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(data);
        for (Enfermaria enf : ordenadas) {
            System.out.printf("  %-6s | Taxa: %5.1f%% | %s%n",
                    enf.getIdentificador(),
                    enf.getTaxaOcupacao(data),
                    enf.emPressao(data) ? "Em pressao" : "Estado normal");
        }

        System.out.println("\n" + SEPARADOR);
        System.out.println("  Episodios por Enfermaria (ordenados por admissao)");
        System.out.println(SEPARADOR);

        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("%n  Enfermaria %s:%n", enf.getIdentificador());
            for (modelo.Episodio ep : enf.getEpisodiosOrdenadosPorAdmissao()) {
                System.out.println("    " + ep);
            }
        }
    }


