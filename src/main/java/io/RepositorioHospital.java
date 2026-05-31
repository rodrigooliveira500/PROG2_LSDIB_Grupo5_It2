package io;

import exceptions.HospitalException;
import modelo.Hospital;

import java.io.*;

/**
 * Classe responsavel por gravar e carregar o estado do hospital usando serializacao de objetos.
 */
public class RepositorioHospital {

    /**
     * Converte o objeto Hospital em bytes e guarda-o num ficheiro.
     *
     * @param hospital O hospital a gravar.
     * @param ficheiro O nome do ficheiro destino.
     * @throws IOException Se houver um erro de escrita no disco.
     */
    public static void gravarEstado(Hospital hospital, String ficheiro) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ficheiro))) {
            oos.writeObject(hospital);
        }
    }

    /**
     * Le um ficheiro de bytes e reconstroi o objeto Hospital.
     *
     * @param ficheiro O nome do ficheiro a ler.
     * @return O objeto Hospital reconstruido.
     * @throws IOException Se o ficheiro nao existir ou não puder ser lido.
     * @throws HospitalException Se os dados dentro do ficheiro não corresponderem a um Hospital.
     */
    public static Hospital carregarEstado(String ficheiro) throws IOException, HospitalException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheiro))) {
            return (Hospital) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new HospitalException("O ficheiro nao contem um estado valido do Hospital.");
        }
    }
}