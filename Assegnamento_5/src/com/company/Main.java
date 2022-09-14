package com.company;
//                                                 ***TESTO***

/*Si scriva un programma Java che riceve in input un filepath che individua una directory D e
stampa le informazioni del contenuto di quella directory e, ricorsivamente, di tutti i file contenuti nelle sottodirectory di D.

Il programma deve essere strutturato come segue:

Attiva un thread produttore ed un insieme di k thread consumatori.
Il produttore comunica con i consumatori mediante una coda.
Il produttore visita ricorsivamente la directory data e eventualmente tutte le sottodirectory e
mette nella coda il nome di ogni directory individuata.
I consumatori prelevano dalla coda i nomi delle directory e stampano il loro contenuto.
La coda deve essere realizzata con una LinkedList. Ricordiamo che una LinkedList non è una struttura thread-safe. Dalle API Java:
"Note that the implementation is not synchronized.
If multiple threads access a linked list concurrently, and at least one of the threads modifies the list structurally,
it must be synchronized externally".*/


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //setup dei vari valori
        LinkedList<File> lista = new LinkedList<File>();
        //arrayList in cui salvo i riferimenti ai thread consumatori. Mi serviranno per poi fare la join dei k thread consumatori
        ArrayList<Thread> arrayList = new ArrayList<>();
        //serve per inserire da tastiera il numero di consumatori
        Scanner numeroConsumatori = new Scanner(System.in);
        System.out.println("inserisci il numero dei consumatori");
        //numero di consumatori
        int k = numeroConsumatori.nextInt();
        System.out.println("inserisci il percorso della cartella di partenza");
        //percorso di partenza (inizialmente viene considerata la cartella in cui il programma è salvato)
        Scanner percorsoDiPartenza = new Scanner(System.in);
        File cartellaDiPartenza= new File(percorsoDiPartenza.next());
        //costruisco la risorsa condivisa, ossia il buffer sincronizzato dove produttore e consumatore lavorano
        ListaSincronizzata listaSincronizzata = new ListaSincronizzata(lista);
        //creo thread produttore
        Thread produttore = new Thread(new Produttore(listaSincronizzata, cartellaDiPartenza)) ;
        //avvio il produttore
        produttore.start();
        //avvio dei k consumatori
        for (int i = 0; i<k; i++){
            Thread consumatori = new Thread(new Consumatore(listaSincronizzata));
            consumatori.start();
            //metto i riferimenti ai consumatori in una lista in questo modo posso fare la join su di essi
            arrayList.add(consumatori);

        }

        //join dei k thread consumatori e del thread produttore
        try {
            produttore.join(); //join produttore
            for (Thread t: arrayList) {
                t.join(); //join dei vari thread consumatori
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
