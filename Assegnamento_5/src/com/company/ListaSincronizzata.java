package com.company;

import java.io.File;
import java.util.LinkedList;

public class ListaSincronizzata {
    //VARIABILI D'ISTANZA
    public  LinkedList <File> listaSincronizzata ; //lista su cui si può lavorare coi metodi sincronizzati
    public boolean fineDelleCartelleDaVisitare = false; //serve per notificare il fatto che è finita la visita dell'albero di cartelle


    public ListaSincronizzata(LinkedList<File> listaSincronizzata) {
        this.listaSincronizzata = listaSincronizzata;
    }

    //metodo che ritorna il primo elemento dalla lista, cioè il file da stampare a video
    public synchronized File take() {
        //se è vuoto il buffer aspetto finché non lo riempie il produttore
        if (listaSincronizzata.isEmpty()) {
            while (listaSincronizzata.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
        //rimuovo il file dalla lista
        File file = listaSincronizzata.removeFirst();

        //ritonro il file
        return file;

    }

    //metodo per inserire file all'interno della lista
    public synchronized void put(File file){
        //aggiungo il file al buffer
        listaSincronizzata.add(file);
        notifyAll();


    }

    //metodo per comunicare che la visita dell'albero delle cartelle, da parte del produttore, è finita
    public synchronized void fineVisitaAlberoDelledirectory (){
        fineDelleCartelleDaVisitare = true;
        notifyAll();
    }

    /*Essendo il medoto .size() di linkedList non sincronizzato ho pensato di implementare il metodo sottostante sincronizzato
    * che mi ritorna il numero di elementi nella lista. In questo modo non rischio di accedere ad una variabile
    * condivisa in modo non safe*/
    public synchronized int size (){
        return listaSincronizzata.size(); //ritorno il numero di elementi nella lista
    }

}
