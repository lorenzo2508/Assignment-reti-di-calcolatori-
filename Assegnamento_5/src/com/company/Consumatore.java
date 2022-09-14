package com.company;

import java.io.File;


public class Consumatore implements Runnable{

    private ListaSincronizzata listaDaConsumare; //VARIABILE D'ISTANZA E RISORSA CONDIVISA.
    //COLORI
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    //COSTRUTTORE
    public Consumatore(ListaSincronizzata listaDaConsumare) {
        this.listaDaConsumare = listaDaConsumare;
    }

    @Override
    public void run() {

        /*while che cicla finché il non il produttore non ha finito di visitare le cartelle e sotto-cartelle
        * e finché la dimensione della lista non diventa zero, cioè finché non sono state consumate tutte le cartelle contenute
        * nel buffer condiviso e sincronizzato*/
        while (!listaDaConsumare.fineDelleCartelleDaVisitare || listaDaConsumare.size() > 0){
            File file = listaDaConsumare.take(); //prendo il file dalla lista
            if(file.isDirectory()) //se è una cartella lo stampo in rosso
                System.out.println(ANSI_RED + file + ANSI_RESET);
            else
                System.out.println(file); //altrimenti lo stampo normalmente

            if(file.isDirectory()){
                //creo un array che contiene tutti i file contenuti nella cartella che il consumatore sta guardando in questo momento
                File [] listaDiFile = file.listFiles();
                //per togliere il warning: listaDiFile potrebbe essere null
                assert listaDiFile != null;
                //per ogni file contenuto nell'array ne prendo il nome e lo stampo
                for(File files: listaDiFile){
                    //prendo il nome
                    String name = files.getName();
                    if(files.isDirectory()){
                        //se è una cartella stampo il nome in rosso
                        System.out.println(ANSI_RED + name + ANSI_RESET);
                    }
                    else
                        //se non è una cartella stampo il nome normalmente
                        System.out.println(name);
                }

            }

        }

    }

}
