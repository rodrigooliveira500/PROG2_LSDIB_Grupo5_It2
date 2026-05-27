package main.exceptions;

/**
 * Exceção personalizada para erros de negócio da aplicação hospitalar.
 * Utilizada para sinalizar situações inválidas como enfermarias duplicadas,
 * datas inválidas, intervalos incorretos ou operações impossíveis.
 *
 * @author Grupo 5
 * @version 1.0
 */
public class HospitalException extends Exception {

    /**
     * Cria uma nova exceção com a mensagem indicada.
     *
     * @param mensagem descrição do erro ocorrido
     */
    public HospitalException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria uma nova exceção com mensagem e causa original.
     *
     * @param mensagem descrição do erro ocorrido
     * @param causa    exceção original que causou este erro
     */
    public HospitalException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}