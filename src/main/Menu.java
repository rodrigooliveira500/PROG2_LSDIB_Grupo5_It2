package main;

import exceptions.HospitalException;
import io.GestorFicheiros;
import io.GestorSerializacao;
import modelo.Enfermaria;
import modelo.Hospital;
import utils.AnalisadorEstatistico;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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

    /**
     * Cria um novo menu para o hospital indicado.
     *
     * @param hospital hospital a gerir
     * @param leitor   scanner para leitura da consola
     */
    public Menu(Hospital hospital, Scanner leitor) {
        this.hospital = hospital;
        this.leitor   = leitor;
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
        switch (opcao) {
            case 1  -> carregarCSV();
            case 2  -> GestorConsola.inserirEnfermaria(leitor, hospital);
            case 3  -> GestorConsola.inserirEpisodio(leitor, hospital);
            case 4  -> mostrarIndicadoresOcupacao();
            case 5  -> analisarPressaoIntervalo();
            case 6  -> mostrarListagensOrdenadas();
            case 7  -> alterarCapacidadeEnfermarias();
            case 8  -> gravarEstado();
            case 9  -> carregarEstado();
            case 0  -> System.out.println("\nA sair... Ate logo!");
            default -> System.out.println("\n[AVISO] Opcao invalida! Tente novamente.");
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


    // OPCAO 5 — Análise de pressão por intervalo

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

    // OPCAO 6 — Listagens ordenadas

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

// OPCAO 7 — Alterar capacidade

    /**
     * Solicita uma percentagem e altera a capacidade de todas as enfermarias.
     * Lança exceção se não existirem enfermarias ou a percentagem for inválida.
     *
     * @throws HospitalException se não existirem enfermarias ou a percentagem for inválida
     */
    private void alterarCapacidadeEnfermarias() throws HospitalException {
        System.out.println("\n--- Alterar Capacidade das Enfermarias ---");
        validarHospitalNaoVazio();

        System.out.print("Percentagem de variacao (ex: 10 para +10%, -20 para -20%): ");
        String texto = leitor.nextLine().trim();

        double percentagem = validarPercentagem(texto);

        Enfermaria.alterarCapacidade(hospital.getEnfermarias(), percentagem);
        System.out.printf("Capacidade ajustada em %.1f%%.%n", percentagem);

        System.out.println("\nEstado atual:");
        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("  %s | Camas: %d%n", enf.getIdentificador(), enf.getNumeroCamas());
        }
    }

    /**
     * Converte e valida uma string como percentagem de variação.
     * Utilizado nos testes unitários para verificar a lógica de validação.
     *
     * @param texto texto introduzido pelo utilizador
     * @return valor decimal da percentagem
     * @throws HospitalException se o texto não for um número válido
     */
    public static double validarPercentagem(String texto) throws HospitalException {
        if (texto == null || texto.isBlank()) {
            throw new HospitalException("A percentagem nao pode estar vazia.");
        }
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new HospitalException("Percentagem invalida: '" + texto + "'. Introduza um numero.", e);
        }
    }

// OPCAO 8 — Gravar estado (serialização)

    /**
     * Solicita o caminho do ficheiro e grava o estado do hospital por serialização.
     * Lança exceção se não existirem dados para gravar ou ocorrer erro de I/O.
     *
     * @throws HospitalException se não existirem enfermarias para gravar
     * @throws IOException       se ocorrer erro na escrita do ficheiro
     */
    private void gravarEstado() throws HospitalException, IOException {
        System.out.println("\n--- Gravar Estado do Hospital ---");
        validarHospitalNaoVazio();

        System.out.print("Nome do ficheiro de gravacao (ex: hospital.dat): ");
        String nomeFicheiro = leitor.nextLine().trim();

        if (nomeFicheiro.isBlank()) {
            throw new HospitalException("O nome do ficheiro nao pode estar vazio.");
        }

        GestorSerializacao.gravarEstado(hospital, nomeFicheiro);
        System.out.println("Estado gravado com sucesso em '" + nomeFicheiro + "'.");
    }

// OPCAO 9 — Carregar estado (deserialização)

    /**
     * Solicita o caminho do ficheiro e carrega o estado do hospital por deserialização.
     * Lança exceção se o ficheiro não existir ou ocorrer erro de leitura.
     *
     * @throws HospitalException se o ficheiro não existir ou os dados forem inválidos
     * @throws IOException       se ocorrer erro na leitura do ficheiro
     */
    private void carregarEstado() throws HospitalException, IOException {
        System.out.println("\n--- Carregar Estado do Hospital ---");

        System.out.print("Nome do ficheiro a carregar (ex: hospital.dat): ");
        String nomeFicheiro = leitor.nextLine().trim();

        if (nomeFicheiro.isBlank()) {
            throw new HospitalException("O nome do ficheiro nao pode estar vazio.");
        }

        if (!new File(nomeFicheiro).exists()) {
            throw new HospitalException("Ficheiro '" + nomeFicheiro + "' nao encontrado.");
        }

        Hospital hospitalCarregado = GestorSerializacao.carregarEstado(nomeFicheiro);
        this.hospital = hospitalCarregado;
        System.out.println("Estado carregado com sucesso. Enfermarias: "
                + hospital.getEnfermarias().size());
    }



