package com.company;

import java.io.File;
import java.util.LinkedList;

public class Produttore implements Runnable{
    private ListaSincronizzata listaSincronizzata; //VARIABILE D'ISTANZA E RISORSA CONDIVISA
    private File cartellaRadice; //cartella di partenza

    //COSTRUTTORE
    public Produttore(ListaSincronizzata listaSincronizzata, File cartellaRadice ) {
        this.listaSincronizzata = listaSincronizzata;
        this.cartellaRadice = cartellaRadice;
    }

    @Override
    public void run() {
        //se la cartella radice passata dall'utente non è una cartella stampo "non è una cartella valida"
        if(!cartellaRadice.isDirectory()){
            System.err.println(cartellaRadice + " non è una cartella valida");
            System.exit(1);
        }
        /*aggiungo questa coda interna perché il ciclo while ha come condizione di ciclare finché la lista non è vuota, allora potrebbe succedere
        * che il consumatore svuoti la struttura dati condivisa e il produttore quindi veda che è una lista vuota e smetta la visita delle
        * sotto cartelle. Quindi ho usato questa struttura dati aggiuntiva che mi permette di tenre una traccia consistente delle cartelle e dei
        * file contenuti nelle varie sotto cartelle della radice. */
        LinkedList<File> codaInternaFile = new LinkedList<>();
        codaInternaFile.add(cartellaRadice);
        //per far stampare anche il contenuto della cartella radice
        listaSincronizzata.put(cartellaRadice);
        //finché la coda interna è piena continuo a ciclare
        while (!codaInternaFile.isEmpty()){
            //aggiungo la cartella nella struttura dati condivisa e la spaccetto mettendoci tutti i file che contiene
            File[] fileContenutiNellaCartella = codaInternaFile.removeFirst().listFiles();
            /*assert per togliere il warning generato da: fileContenutiNellaCartella potrebbe essere null */
            assert fileContenutiNellaCartella != null;
            for (File file : fileContenutiNellaCartella) {
                // Se incontro una directory, la metto in coda
                // per l'esplorazione e l'aggiungo anche alla coda sincronizzata dove il
                //consumatore andrà a prendere la roba da stampare
                if (file.isDirectory()) {
                    codaInternaFile.add(file); //aggiungo il file alla lista locale
                    listaSincronizzata.put(file); //aggiungo il file al buffer condiviso e notifico hai thread consumatori che ho aggiunto
                }

            }

        }
        //quando ho finito di navigare tra le cartelle notifico il fatto che ho finito, settando la variabile
        //fineDelleCartelleDaVisitare a true
        listaSincronizzata.fineVisitaAlberoDelledirectory();


    }




}







