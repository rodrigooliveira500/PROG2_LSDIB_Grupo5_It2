package io;

import modelo.Hospital;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe responsável pela serialização e deserialização do Hospital.
 * Permite guardar e carregar o estado completo da aplicação em ficheiro.
 */
public class GestorSerializacao {

    /**
     * Grava o objeto Hospital num ficheiro .ser.
     *
     * @param hospital  objeto Hospital a gravar
     * @param caminho   caminho do ficheiro de destino (ex: "hospital.ser")
     */
    public static void gravar(Hospital hospital, String caminho) {
        try {
            FileOutputStream fileOut = new FileOutputStream(caminho);
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
            outStream.writeObject(hospital);
            outStream.close();
            fileOut.close();
            System.out.println("Dados gravados com sucesso em: " + caminho);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Carrega um objeto Hospital a partir de um ficheiro .ser.
     *
     * @param caminho   caminho do ficheiro a carregar (ex: "hospital.ser")
     * @return          objeto Hospital deserializado, ou null se falhar
     */
    public static Hospital carregar(String caminho) {
        Hospital hospital = null;
        try {
            FileInputStream fileIn = new FileInputStream(caminho);
            ObjectInputStream inStream = new ObjectInputStream(fileIn);
            hospital = (Hospital) inStream.readObject();
            inStream.close();
            fileIn.close();
            System.out.println("Dados carregados com sucesso de: " + caminho);
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Classe Hospital não encontrada.");
            c.printStackTrace();
        }
        return hospital;
    }
}