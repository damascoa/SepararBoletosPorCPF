/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pdfread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Renato
 */
public class Principal {

    static String pathDirectory = "E:\\Boleto Entrada\\";
    static String pathDirectoryOutput = "E:\\Boleto Saida\\";

    public static void main(String[] args) {
        Set<String> cpfs = new HashSet<>();

        File folder = new File(pathDirectory);
        File[] listOfFiles = folder.listFiles();

        carregarCPFS(listOfFiles, pathDirectory, cpfs);

        criarDiretoriosSaida(cpfs, pathDirectoryOutput);

        separarBoletos(listOfFiles, pathDirectory, cpfs);

    }

    private static void criarDiretoriosSaida(Set<String> cpfs, String pathDirectoryOutput) {
        for (String cpf : cpfs) {
            cpf = cpf.replace(".", "").replace("-", "").trim();
            if (!new File(pathDirectoryOutput + cpf).exists()) {
                new File(pathDirectoryOutput + cpf).mkdir();
            }
        }
    }

    private static void carregarCPFS(File[] listOfFiles, String pathDirectory, Set<String> cpfs) {
        for (int i = 0; i < listOfFiles.length; i++) {
            try {
                PDDocument document = PDDocument.load(new File(pathDirectory + listOfFiles[i].getName()));
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    cpfs.add(getCPF(text));
                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void separarBoletos(File[] listOfFiles, String pathDirectory, Set<String> cpfs) {

        for (int i = 0; i < listOfFiles.length; i++) {
            try {
                PDDocument document = PDDocument.load(new File(pathDirectory + listOfFiles[i].getName()));
                if (!document.isEncrypted()) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    String cpf = getCPF(text);
                    String data = getVencimento(text);
                    System.out.println("DATA: "+data);
                    System.out.println("COPIANDO O BOLETO: "+listOfFiles[i].getName());
                    copyFileUsingStream(listOfFiles[i], new File(pathDirectoryOutput + cpf.replace(".", "").replace("-", "").trim()+"\\"+listOfFiles[i].getName()));
//                    
                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static String getVencimento(String texto) {
        String rxCPF = "(\\d{2})\\/(\\d{2})\\/(\\d{4})";
        Pattern p = Pattern.compile(rxCPF);
        Matcher matcher = p.matcher(texto);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static String getCPF(String texto) {
        String rxCPF = "([0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2})";
        Pattern p = Pattern.compile(rxCPF);
        Matcher matcher = p.matcher(texto);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
