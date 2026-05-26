
package io;

import modelo.Hospital;
import modelo.Enfermaria;
import modelo.Episodio;
import modelo.EnfermariaGeral;
import modelo.EnfermariaPsiquiatrica;
import modelo.EnfermariaCuidadosIntensivos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Classe responsável pela leitura e validação de dados a partir de ficheiros CSV.
 * Os erros de validação são registados no ficheiro {@code erros_validacao.log}.*
 * @author Grupo 5
 * @version 1.0
 */
public class GestorFicheiros {

    /** Caminho do ficheiro de log de erros de validação. */
    private static final String FICHEIRO_LOG = "erros_validacao.log";

    /** Capacidade mínima válida para uma enfermaria. */
    private static final int CAPACIDADE_MINIMA = 1;

    /**
     * Regista uma mensagem de erro no ficheiro de log.
     *
     * @param mensagem mensagem de erro a registar
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static void logErro(String mensagem) throws IOException {
        PrintWriter escritor = new PrintWriter(new FileWriter(FICHEIRO_LOG, true));
        escritor.println("[ERRO] " + LocalDate.now() + ": " + mensagem);
        escritor.flush();
        escritor.close();
    }

    /**
     * Limpa o conteúdo do ficheiro de log de erros no arranque da aplicação.
     *
     * @throws IOException se ocorrer erro ao aceder ao ficheiro de log
     */
    public static void limparLog() throws IOException {
        PrintWriter escritor = new PrintWriter(new FileWriter(FICHEIRO_LOG, false));
        escritor.close();
    }

    // Métodos de validação de campos — retornam apenas true/false, sem log

    /**
     * Valida se uma string não é nula nem composta apenas por espaços.
     *
     * @param valor string a validar
     * @return {@code true} se válida, {@code false} caso contrário
     */
    private static boolean validarString(String valor) {
        return valor != null && !valor.isBlank();
    }

    /**
     * Valida se uma string representa um número decimal válido.
     *
     * @param valor string a validar
     * @return {@code true} se for um decimal válido, {@code false} caso contrário
     */

    public static boolean validarDecimal(String valor) {
        if (!validarString(valor)) return false;
        try {
            Double.parseDouble(valor.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se uma string representa uma data no formato AAAA-MM-DD.
     * Utiliza o mecanismo de try-catch nativo para validar os componentes numéricos.
     *
     * @param valor string a validar
     * @return {@code true} se for uma data válida, {@code false} caso contrário
     */
    public static boolean validarData(String valor) {
        if (!validarString(valor)) return false;
        try {
            String[] p = valor.trim().split("-");
            if (p.length != 3) return false;
            int mes = Integer.parseInt(p[1]);
            int dia = Integer.parseInt(p[2]);
            return mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se uma capacidade é maior ou igual ao mínimo permitido.
     *
     * @param capacidade valor a validar
     * @return {@code true} se válida, {@code false} caso contrário
     */
    private static boolean validarCapacidade(int capacidade) {
        return capacidade >= CAPACIDADE_MINIMA;
    }

    // Validação de linhas CSV completas — registam erro no log e retornam false


    /**
     * Valida todos os campos de uma linha CSV de enfermaria.
     * Verifica os campos base e delega a validação específica para o método do tipo correspondente.
     *
     * @param d     campos da linha CSV divididos por {@code ;}
     * @param linha número da linha no ficheiro (para log)
     * @return {@code true} se a linha for válida, {@code false} caso contrário
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    public static boolean validarLinhaEnfermaria(String[] d, int linha) throws IOException {
        if (d.length < 3 || !validarString(d[1])) {
            logErro("Linha " + linha + ": campos base invalidos (tipo, id).");
            return false;
        }

        try {
            int cap = Integer.parseInt(d[2].trim());
            if (!validarCapacidade(cap)) {
                logErro("Linha " + linha + ": capacidade invalida (" + cap + "). Minimo: " + CAPACIDADE_MINIMA + ".");
                return false;
            }
        } catch (NumberFormatException e) {
            logErro("Linha " + linha + ": capacidade nao e um numero inteiro valido (" + d[2].trim() + ").");
            return false;
        }

        String tipo = d[0].trim();

        if (tipo.equals("GERAL"))        return validarCamposGeral(d, linha);
        if (tipo.equals("PSIQUIATRICA")) return validarCamposPsiquiatrica(d, linha);
        if (tipo.equals("INTENSIVOS"))   return validarCamposCuidadosIntensivos(d, linha);

        logErro("Linha " + linha + ": tipo desconhecido (" + tipo + ").");
        return false;
    }

    /**
     * Valida os campos específicos de uma linha CSV do tipo GERAL.
     *
     * @param d     campos da linha CSV
     * @param linha número da linha no ficheiro (para log)
     * @return {@code true} se válida, {@code false} caso contrário
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static boolean validarCamposGeral(String[] d, int linha) throws IOException {
        boolean valido = false;
        try {
            valido = d.length >= 5 && Integer.parseInt(d[3].trim()) >= 0 && validarString(d[4]);
        } catch (NumberFormatException e) {
            valido = false;
        }
        if (!valido) logErro("Linha " + linha + ": GERAL — acompanhantes ou horario invalidos.");
        return valido;
    }

    /**
     * Valida os campos específicos de uma linha CSV do tipo PSIQUIATRICA.
     *
     * @param d     campos da linha CSV
     * @param linha número da linha no ficheiro (para log)
     * @return {@code true} se válida, {@code false} caso contrário
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static boolean validarCamposPsiquiatrica(String[] d, int linha) throws IOException {
        boolean valido = d.length >= 5 && validarString(d[3]) && validarString(d[4]);
        if (!valido) logErro("Linha " + linha + ": PSIQUIATRICA — horario ou nivel de seguranca invalidos.");
        return valido;
    }


    /**
     * Valida os campos específicos de uma linha CSV do tipo INTENSIVOS.
     *
     * @param d     campos da linha CSV
     * @param linha número da linha no ficheiro (para log)
     * @return {@code true} se válida, {@code false} caso contrário
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static boolean validarCamposCuidadosIntensivos(String[] d, int linha) throws IOException {
        if (d.length < 6 || !validarString(d[3]) || !validarDecimal(d[4]) || !validarDecimal(d[5])) {
            logErro("Linha " + linha + ": INTENSIVOS — horario ou pressoes invalidos.");
            return false;
        }
        return true;
    }


    /**
     * Carrega enfermarias a partir de um ficheiro CSV e adiciona-as ao hospital.
     * <p>
     * Formato esperado (separador {@code ;}):
     * <ul>
     * <li>GERAL;ID;CAPACIDADE;ACOMPANHANTES[;RECURSO1;RECURSO2;...]</li>
     * <li>PSIQUIATRICA;ID;CAPACIDADE;HORARIO;NIVEL_SEGURANCA</li>
     * <li>INTENSIVOS;ID;CAPACIDADE;HORARIO;PRESSAO;PRESSAO_REFERENCIA</li>
     * </ul>
     * A primeira linha é tratada como cabeçalho e ignorada.
     * Entradas inválidas são registadas no ficheiro de log através do mecanismo de exceções.
     *
     * @param path caminho para o ficheiro CSV das enfermarias
     * @param h    hospital onde as enfermarias serão adicionadas
     * @throws IOException           se ocorrer erro ao escrever no ficheiro de log
     * @throws FileNotFoundException se o ficheiro CSV não for encontrado
     */
    public static void carregarEnfermarias(String path, Hospital h)
            throws IOException, FileNotFoundException {

        File f = new File(path);
        if (!f.exists()) {
            System.out.println("Ficheiro nao encontrado: " + path);
            return;
        }

        Scanner sc = new Scanner(f);
        if (sc.hasNextLine()) {
            sc.nextLine(); // Ignora a linha do cabeçalho
        }

        int linha = 1;
        while (sc.hasNextLine()) {
            linha++;
            String[] d = sc.nextLine().trim().split(";");

            if (d.length < 3) {
                logErro("Linha " + linha + ": campos insuficientes.");
                continue; // Avança para a próxima linha
            }

            String tipo   = d[0].trim().toUpperCase();
            String id     = d[1].trim();
            String capStr = d[2].trim();

            if (!validarString(id)) {
                logErro("Linha " + linha + ": identificador invalido.");
                continue;
            }

            try {
                // Tenta converter diretamente. Se contiver letras ou espaços inválidos,
                // o Java dispara automaticamente.
                int cap = Integer.parseInt(capStr);

                if (!validarCapacidade(cap)) {
                    logErro("Linha " + linha + ": capacidade invalida (" + cap + "). A capacidade deve ser >= 1.");
                    continue;
                }

                // Encaminha para os métodos de processamento das subclasses
                if (tipo.equals("GERAL")) {
                    processarEnfermariaGeral(d, linha, id, cap, h);
                } else if (tipo.equals("PSIQUIATRICA")) {
                    processarEnfermariaPsiquiatrica(d, linha, id, cap, h);
                } else if (tipo.equals("INTENSIVOS")) {
                    processarEnfermariaCuidadosIntensivos(d, linha, id, cap, h);
                } else {
                    logErro("Linha " + linha + ": tipo desconhecido (" + tipo + ").");
                }

            } catch (NumberFormatException e) {
                // Captura nativamente o erro de conversão numérica sem precisar de funções manuais
                logErro("Linha " + linha + ": capacidade nao e um numero inteiro valido ('" + capStr + "').");
            }
        }
        sc.close();
    }

    /**
     * Processa e cria uma {@link EnfermariaGeral} a partir dos campos do CSV.
     * Utiliza o tratamento de exceções nativo para validar os campos numéricos.
     *
     * @param d       array de campos lidos do CSV
     * @param linha   número da linha no ficheiro (para log)
     * @param id      identificador da enfermaria
     * @param cap     número total de camas
     * @param h       hospital onde a enfermaria será adicionada
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static void processarEnfermariaGeral(String[] d, int linha,
                                                 String id, int cap, Hospital h)
            throws IOException {

        if (d.length < 5) {
            logErro("Linha " + linha + ": GERAL requer campos suficientes (tipo;id;capacidade;acompanhantes;horario).");
            return;
        }

        try {
            // Tenta converter o número de acompanhantes diretamente.
            // Se falhar (ex: conter letras), o Java dispara o NumberFormatException.
            int acomp = Integer.parseInt(d[3].trim());

            if (acomp < 0) {
                logErro("Linha " + linha + ": numero de acompanhantes nao pode ser negativo (" + acomp + ").");
            } else if (!validarString(d[4])) {
                logErro("Linha " + linha + ": horario de visitas em branco.");
            } else {
                EnfermariaGeral eg = new EnfermariaGeral(id, cap, acomp, d[4].trim());

                // Adiciona os recursos opcionais que possam existir a partir do índice 5
                for (int i = 5; i < d.length; i++) {
                    if (validarString(d[i])) {
                        eg.adicionarRecurso(d[i].trim());
                    }
                }

                h.adicionarEnfermaria(eg);
            }

        } catch (NumberFormatException e) {
            // O catch captura o erro se d[3] não for um número válido, eliminando o validarInteiro
            logErro("Linha " + linha + ": o numero de acompanhantes nao e um numero inteiro valido ('" + d[3].trim() + "').");
        }
    }

    /**
     * Processa e cria uma {@link EnfermariaPsiquiatrica} a partir dos campos do CSV.
     *
     * @param d       array de campos lidos do CSV
     * @param linha   número da linha no ficheiro (para log)
     * @param id      identificador da enfermaria
     * @param cap     número total de camas
     * @param h       hospital onde a enfermaria será adicionada
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static void processarEnfermariaPsiquiatrica(String[] d, int linha,
                                                        String id, int cap, Hospital h)
            throws IOException {

        if (d.length < 5 || !validarString(d[3]) || !validarString(d[4])) {
            logErro("Linha " + linha + ": PSIQUIATRICA requer horario e nivel de seguranca.");
        } else {
            h.adicionarEnfermaria(new EnfermariaPsiquiatrica(id, cap, d[3].trim(), d[4].trim()));
        }
    }

    /**
     * Processa e cria uma {@link EnfermariaCuidadosIntensivos} a partir dos campos do CSV.
     *
     * @param d       array de campos lidos do CSV
     * @param linha   número da linha no ficheiro (para log)
     * @param id      identificador da enfermaria
     * @param cap     número total de camas
     * @param h       hospital onde a enfermaria será adicionada
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    private static void processarEnfermariaCuidadosIntensivos(String[] d, int linha,
                                                              String id, int cap, Hospital h)
            throws IOException {

        if (d.length < 6 || !validarString(d[3]) || !validarDecimal(d[4]) || !validarDecimal(d[5])) {
            logErro("Linha " + linha + ": INTENSIVOS requer horario, pressao e pressao de referencia validos.");
        } else {
            double pressao    = Double.parseDouble(d[4].trim());
            double pressaoRef = Double.parseDouble(d[5].trim());
            if (pressao <= 0 || pressaoRef <= 0) {
                logErro("Linha " + linha + ": pressoes devem ser positivas.");
            } else {
                h.adicionarEnfermaria(new EnfermariaCuidadosIntensivos(id, cap, d[3].trim(), pressao, pressaoRef));
            }
        }
    }



    /**
     * Carrega episódios a partir de um ficheiro CSV e associa-os às enfermarias do hospital.
     * Formato esperado: ID_ENFERMARIA;ID_CAMA;DATA_ADMISSAO[;DATA_ALTA]
     * A primeira linha é tratada como cabeçalho e ignorada.
     * Entradas inválidas são registadas no ficheiro de log.
     *
     * @param path caminho para o ficheiro CSV dos episódios
     * @param hospital com as enfermarias já carregadas
     * @throws IOException           se ocorrer erro ao escrever no ficheiro de log
     * @throws FileNotFoundException se o ficheiro CSV não for encontrado
     */
    public static void carregarEpisodios(String path, Hospital hospital) throws IOException {
        File ficheiro = new File(path);

        // Saída limpa em vez de estoirar com FileNotFoundException
        if (!ficheiro.exists()) {
            System.out.println("  [AVISO] Ficheiro nao encontrado: " + path);
            return;
        }

        Scanner sc = new Scanner(ficheiro);

        if (sc.hasNextLine()) {
            sc.nextLine();
        }

        int linha = 1;
        while (sc.hasNextLine()) {
            linha++;
            processarLinhaEpisodio(sc.nextLine(), linha, hospital);
        }
        sc.close();
    }

    /**
     * Processa e valida uma linha do CSV de episódios, criando o episódio correspondente.
     *
     * @param linhaCsv linha de texto lida do ficheiro CSV
     * @param linha    número da linha no ficheiro (para log)
     * @param hospital hospital com as enfermarias já carregadas
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
private static void processarLinhaEpisodio(String linhaCsv, int linha, Hospital hospital) throws IOException {
    String conteudo = linhaCsv.trim();
    if (conteudo.isEmpty()) {
        return;
    }

    String[] dados = conteudo.split(";");

    // Barreira, se nao tiver todos os espaços da lista completos da erro
    if (dados.length < 3) {
        logErro("Linha " + linha + ": campos insuficientes no episodio.");
        return;
    }

    String idEnfermaria = dados[0].trim();
    String idCama = dados[1].trim();
    String dataAdmissaoStr = dados[2].trim();

    //Validação de Strings e Formatos dos dados do ficheiro CSV
    if (!validarString(idEnfermaria)) {
        logErro("Linha " + linha + ": ID de enfermaria invalido.");
        return;
    }
    if (!validarString(idCama)) {
        logErro("Linha " + linha + ": ID de cama invalido.");
        return;
    }
    if (!validarData(dataAdmissaoStr)) {
        logErro("Linha " + linha + ": data de admissao invalida.");
        return;
    }

    // Verificar se a enfermaria colocada no ficheiro CSV existe
    Enfermaria enfermaria = hospital.obterEnfermaria(idEnfermaria);
    if (enfermaria == null) {
        logErro("Linha " + linha + ": enfermaria nao encontrada (" + idEnfermaria + ").");
        return;
    }

    LocalDate admissao = LocalDate.parse(dataAdmissaoStr);
    Episodio episodio = new Episodio(idCama, admissao);

    if (dados.length >= 4 && validarString(dados[3])) {
        String dataAltaStr = dados[3].trim();

        if (!validarData(dataAltaStr)) {
            logErro("Linha " + linha + ": data de alta invalida.");
            return;
        }

        LocalDate alta = LocalDate.parse(dataAltaStr);
        if (!alta.isAfter(admissao)) {
            logErro("Linha " + linha + ": data de alta nao pode ser anterior ou igual a admissao.");
            return;
        }

        // Se passou em tudo, damos alta ao episódio
        episodio.darAlta(alta);
    }

    // Independentemente de ter alta ou não, é adicionado à enfermaria.
    enfermaria.adicionarEpisodio(episodio);
}

}


