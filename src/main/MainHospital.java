package main;

import io.GestorFicheiros;
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

/**
 * Classe principal da aplicação.
 */
public class MainHospital {

    /** Separador visual. */
    private static final String SEPARADOR = "=".repeat(60);

    /** Identificador da enfermaria usada no exemplo. */
    private static final String ID_ENFERMARIA_EXEMPLO = "G1";

    /**
     * Executa a demonstração principal.
     *
     * @param args argumentos da linha de comandos
     * @throws IOException se ocorrer erro no acesso aos ficheiros
     */
    public static void main(String[] args) throws IOException {
        Scanner leitor = new Scanner(System.in);

        System.out.println(SEPARADOR);
        System.out.println("  Hospital XYZ - Sistema de Monitorizacao de Camas");
        System.out.println(SEPARADOR);
        System.out.println();
        System.out.println("Menu de configuracao inicial");
        System.out.println(SEPARADOR);

        String diretorioCsv = lerDiretorioCsv(leitor);
        LocalDate dataReferencia = lerData(leitor, "Introduza a data de referencia (AAAA-MM-DD): ");
        LocalDate dataInicio = lerData(leitor, "Introduza a data inicial do intervalo (AAAA-MM-DD): ");
        LocalDate dataFim = lerDataFim(leitor, dataInicio);

        Hospital hospital = new Hospital("Hospital Central XYZ");
        GestorFicheiros.limparLog();

        System.out.println("A carregar enfermarias do ficheiro CSV...");
        GestorFicheiros.carregarEnfermarias(construirCaminho(diretorioCsv, "enfermarias.csv"), hospital);
        System.out.println("     Enfermarias carregadas: " + hospital.getEnfermarias().size());

        System.out.println(" A carregar episodios do ficheiro CSV...");
        GestorFicheiros.carregarEpisodios(construirCaminho(diretorioCsv, "episodios.csv"), hospital);
        System.out.println("     Consulte 'erros_validacao.log' para entradas rejeitadas.");

        System.out.println("\n" + SEPARADOR);
        System.out.println("  Estrutura carregada");
        System.out.println(SEPARADOR);
        System.out.println(hospital);
        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.println("  " + enf);
        }

        // RF2 - INDICADORES DE OCUPAÇÃO
        System.out.println("\n" + SEPARADOR);
        System.out.printf(" Indicadores de Ocupacao em %s%n", dataReferencia);
        System.out.println(SEPARADOR);

        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("%n  Enfermaria : %s%n", enf.getIdentificador());
            System.out.printf("  Ocupacao   : %d / %d camas%n",
                    enf.getOcupacaoAbsoluta(dataReferencia), enf.getNumeroCamas());
            System.out.printf("  Taxa       : %.1f%%%n",
                    enf.getTaxaOcupacao(dataReferencia));
            System.out.printf("  Estado     : %s%n",
                    enf.emPressao(dataReferencia) ? "Em pressao" : "Estado normal");
        }

        System.out.println("\n" + SEPARADOR);
        System.out.println(" Sumario de Length of Stay (LoS) por Enfermaria");
        System.out.println(SEPARADOR);

        for (Enfermaria enf : hospital.getEnfermarias()) {
            AnalisadorEstatistico.SumarioLoS sumario = AnalisadorEstatistico.calculasEstatisticaLoS(enf);
            System.out.printf("  %s: %s%n", enf.getIdentificador(), sumario);
        }

        // RF3 - ANÁLISE DE PRESSÃO POR INTERVALO DE DATAS
        System.out.println("\n" + SEPARADOR);
        System.out.printf("Analise de Pressao [%s a %s]%n", dataInicio, dataFim);
        System.out.println(SEPARADOR);

        for (Enfermaria enf : hospital.getEnfermarias()) {
            System.out.printf("%nEnfermaria %s:%n", enf.getIdentificador());
            AnalisadorEstatistico.analisarPressaoPorIntervalo(enf, dataInicio, dataFim);
        }

        System.out.println("\n" + SEPARADOR);
        System.out.printf("Episodios da Enfermaria %s %n", ID_ENFERMARIA_EXEMPLO);
        System.out.println(SEPARADOR);

        Enfermaria enfermariaExemplo = hospital.obterEnfermaria(ID_ENFERMARIA_EXEMPLO);
        if (enfermariaExemplo != null) {
            List<Episodio> episodiosOrdenados = enfermariaExemplo.getEpisodiosOrdenadosPorAdmissao();
            for (Episodio ep : episodiosOrdenados) {
                System.out.println("  " + ep);
            }
        }

        System.out.println("\n" + SEPARADOR);
        System.out.printf(" Enfermarias por Taxa de Ocupacao em %s%n", dataReferencia);
        System.out.println(SEPARADOR);

        List<Enfermaria> enfermariasOrdenadas = hospital.listarEnfermariasOrdenadasPorTaxaOcupacao(dataReferencia);
        for (Enfermaria enf : enfermariasOrdenadas) {
            System.out.printf("  %-6s | Taxa: %5.1f%% | %s%n",
                    enf.getIdentificador(),
                    enf.getTaxaOcupacao(dataReferencia),
                    enf.emPressao(dataReferencia) ? "Em pressao" : "Estado normal");
        }

        System.out.println("\n" + SEPARADOR);
        System.out.println("  Fim.");
        System.out.println(SEPARADOR);

        leitor.close();
    }

    /**
     * Lê o diretório onde estão os ficheiros CSV.
     *
     * @param leitor leitor do teclado
     * @return diretório válido
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
                 * @param leitor leitor do teclado
                 * @param mensagem mensagem a apresentar
                 * @return data válida
                 */
                public static LocalDate lerData(Scanner leitor, String mensagem) {
                    while (true) {
                        System.out.print(mensagem);
                        String texto = leitor.nextLine().trim();

                        // Usamos o teu método de validação como barreira.
                        if (GestorFicheiros.validarData(texto)) {
                            return LocalDate.parse(texto);
                        } else {
                            System.out.println("Data invalida. Use o formato AAAA-MM-DD.");
                        }
                    }
                }


        /**
         * Lê a data final do intervalo.
         *
         * @param leitor leitor do teclado
         * @param dataInicio data inicial do intervalo
         * @return data final válida
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
     * Constrói o caminho completo de um ficheiro de forma compatível entre sistemas.
     *
     * @param diretorio diretório base
     * @param nomeFicheiro nome do ficheiro
     * @return caminho completo
     */
    public static String construirCaminho(String diretorio, String nomeFicheiro) {
        return new File(diretorio, nomeFicheiro).getPath();
    }
}
