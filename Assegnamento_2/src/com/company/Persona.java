package com.company;

import java.util.concurrent.ThreadLocalRandom;

public class Persona implements Runnable {

    //VARIABILI D'ISTANZA
    private int numeroDellaMatrice; //numerino preso per la fila

    public Persona(int numeroDellaMatrice) {
        this.numeroDellaMatrice = numeroDellaMatrice; //viene settato il numero della matrice nel ciclo while del main
    }

    public int getNumeroDellaMatrice() {
        return numeroDellaMatrice;
    }

    //task
    @Override
    public void run() {
        System.out.println("Cliente postale numero " + numeroDellaMatrice + " sta venendo servito");
        int randomValueToWait = ThreadLocalRandom.current().nextInt(1000, 2000); /*giocando con questa sleep si può cambiare il tempo che ci mette una persona a fare un task
                                                                                                si può anche stressare mettendo tempi come 5 e 10 sec che il programma "dovrebbe" non crashare*/
        try {
            Thread.sleep(randomValueToWait);
        } catch (InterruptedException e) {
//            System.out.println("interruzione sulla sleep causata da chiusura threadPool veloce ");
//            e.printStackTrace();


            Thread.currentThread().interrupt(); /*Un thread non può processare una interruzione mentre sta dormendo
                                                  quindi in questo modo viene ripristinato lo stato interrotto del thread e non vengono
                                                  stampate a video le eccezioni. Se viene decommentato il codice soprastante e commentata la riga
                                                  a cui questo commento si riferisce quello che succede è che le eccezioni dell'interruzione
                                                  vengono stampate a video*/
        }
        System.out.println("Cliente postale numero " + numeroDellaMatrice + " ha terminato di fare le sue cose");
    }
}
