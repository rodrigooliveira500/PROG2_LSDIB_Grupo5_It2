
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
        * Valida todos os campos de uma linha CSV de episódio.
         * Método público para ser reutilizável no GestorConsola e testado com JUnit.
     * @param d        campos da linha CSV divididos por {@code ;}
     * @param linha    número da linha no ficheiro (para log)
     * @param hospital hospital com enfermarias carregadas
     * @return {@code true} se a linha for válida, {@code false} caso contrário
     * @throws IOException se ocorrer erro ao escrever no ficheiro de log
     */
    public static boolean validarLinhaEpisodio(String[] d, int linha, Hospital hospital) throws IOException {
        if (d.length < 3 || !validarString(d[0]) || !validarString(d[1]) || !validarData(d[2])) {
            logErro("Linha " + linha + ": id de enfermaria, id de cama ou data de admissao invalidos.");
            return false;
        }

        if (hospital.obterEnfermaria(d[0].trim()) == null) {
            logErro("Linha " + linha + ": enfermaria nao encontrada (" + d[0].trim() + ").");
            return false;
        }

        boolean altaValida = d.length < 4 || !validarString(d[3])
                || (validarData(d[3]) && LocalDate.parse(d[3].trim()).isAfter(LocalDate.parse(d[2].trim())));

        if (!altaValida) logErro("Linha " + linha + ": data de alta invalida ou anterior/igual a admissao.");
        return altaValida;
    }

    // Carregamento de CSV

    /**
     * Carrega enfermarias a partir de um ficheiro CSV e adiciona-as ao hospital.
     * Cada linha é validada com {@link #validarLinhaEnfermaria} antes de ser processada.
     *
     * @param path caminho para o ficheiro CSV das enfermarias
     * @param h    hospital onde as enfermarias serão adicionadas
     * @throws IOException se ocorrer erro ao ler o ficheiro ou escrever no ficheiro de log
     */
    public static void carregarEnfermarias(String path, Hospital h) throws IOException {
        File f = new File(path);

        if (!f.exists()) {
            System.out.println("Ficheiro nao encontrado: " + path);
            return;
        }

        try (Scanner sc = new Scanner(f)) {
            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            int linha = 1;
            while (sc.hasNextLine()) {
                linha++;
                String[] d = sc.nextLine().trim().split(";");

                if (validarLinhaEnfermaria(d, linha)) {
                    processarEnfermaria(d, linha, h);
                }
            }
        }
    }

    /**
     * Delega o processamento da linha CSV para o método correspondente ao tipo de enfermaria.
     * Só deve ser chamado após {@link #validarLinhaEnfermaria} retornar {@code true}.
     *
     * @param d     array de campos lidos do CSV
     * @param linha número da linha no ficheiro (para log)
     * @param h     hospital onde a enfermaria será adicionada
     */
    private static void processarEnfermaria(String[] d, int linha, Hospital h) throws IOException {
        String tipo = d[0].trim();
        String id   = d[1].trim();
        int    cap  = Integer.parseInt(d[2].trim());

        switch (tipo) {
            case "GERAL" -> processarEnfermariaGeral(d, linha, id, cap, h);
            case "PSIQUIATRICA" -> processarEnfermariaPsiquiatrica(d, linha, id, cap, h);
            case "INTENSIVOS" -> processarEnfermariaCuidadosIntensivos(d, linha, id, cap, h);
        }
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
        try (Scanner sc = new Scanner(ficheiro)) {
            if (sc.hasNextLine()) {
                sc.nextLine(); // Ignorar o cabeçalho
            }

            int linha = 1;
            while (sc.hasNextLine()) {
                linha++;

                // Extrair e separar os campos corretamente
                String[] d = sc.nextLine().trim().split(";");

                // Barreira de defesa: só processa se a validação passar
                if (validarLinhaEpisodio(d, linha, hospital)) {
                    processarEpisodio(d, hospital);
                }
            }
        }
    }

    /**
     * Processa e cria um episódio a partir dos campos do CSV, associando-o à enfermaria.
     * Só deve ser chamado após {@link #validarLinhaEpisodio} retornar {@code true}.
     *
     * @param d        array de campos lidos do CSV
     * @param hospital hospital com as enfermarias já carregadas
     */
    private static void processarEpisodio(String[] d, Hospital hospital) {
        Enfermaria enfermaria = hospital.obterEnfermaria(d[0].trim());
        LocalDate  admissao   = LocalDate.parse(d[2].trim());
        Episodio   episodio   = new Episodio(d[1].trim(), admissao);

        if (d.length >= 4 && validarString(d[3])) {
            episodio.darAlta(LocalDate.parse(d[3].trim()));
        }

        enfermaria.adicionarEpisodio(episodio);
    }
}




