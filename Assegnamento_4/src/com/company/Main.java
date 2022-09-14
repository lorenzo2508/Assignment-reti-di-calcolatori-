package com.company;

/*IL TESTO DELL'ASSEGNAMENTO E' ESATAMENTE LO STESSTO DELL'ASSEGNAMENTO 3, SOLO CHE ADESSO VA IMPLEMENTATO CON
* COSTRUTTI DI ALTO LIVELLO PER LE LOCK (MONITOR E SYNCHRONIZED)*/


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    protected int computerPerTesisti = 10;

    public static void main(String[] args)  {

        //VENGONO SETTATI I VARI VINCOLI DELLA SPECIFICA

        Main objOnlyForSetComputerPerTesisti = new Main(); //oggetto creato solo per settare computerPerTesisti in modo da poter essere più facilmente modificabile.
        ArrayList <Boolean> computers = new ArrayList<>(20);//laboratorio, simulato come un arraylist di booleani con capacità (posti a sedere) = 20

        Laboratorio laboratorio = new Laboratorio(computers); //istanza di laboratorio
        //setto inizialmente il laboratorio a completamente vuoto, simulando una situazione dove inizialmente il laboratorio è completamente libero
        //immaginando la mattina preso di un classico giorno settimanale.
        laboratorio.setComputers(true);
        //istanza del tutor che è colui che si occupa della gestione del laboratorio e degli accessi ad esso
        Tutor tutor = new Tutor(objOnlyForSetComputerPerTesisti.computerPerTesisti, laboratorio);

        /*
         *                                           CODICE PER GLI ARGOMENTI CHE DEVONO ESSERE
         *                                           PASSATI DA TASTIERA.
         * */
        int numeroDiStudenti;
        int numeroDiTesisti;
        int numeroDiProfessori;
        Scanner newInteger = new Scanner(System.in);

        /*L'ERRORE DI OUTPUT NEL CODICE PRECEDENTE STAVA QUI PERCHE' ERANO INVERTITI GLI SCANNER DI
        * "numeroDiProfessori" E "numeroDiStudenti" METTO GLI ASETERISCHI PER FAR VEDERE COSA C'ERA DI INVERITO */
        System.out.println("inserisci il numero di PROFESSORI con cui vuoi avviare la simulazione");
        numeroDiProfessori = newInteger.nextInt(); //***

        System.out.println("inserisci il numero di STUDENTI con cui vuoi avviare la simulazione");
        numeroDiStudenti = newInteger.nextInt(); //***

        System.out.println("inserisci il numero di TESISTI con cui vuoi avviare la simulazione");
        numeroDiTesisti = newInteger.nextInt();

        /*LE DUE SLEEP INTRODOTTE, SEGNATE CON: "*** 1" "***2",  SERVONO PER ASSICURARMI CHE CI SIA UN CERTO ORDINE NELLA PARTENZA DEI THREAD CIOE' CHE I PROFESSORI RIESCANO AD OTTERENER
        * PER PRIMI LA RISORSA DEL LABORATORIO INTRODUCENTO UN CERTO DELAY SULLA CREAZIONE E LA PARTENZA DEI THREAD DI TIPO "TESISTA" E "STUDENTE"*/

        //creo professori

        for(int i = 0; i < numeroDiProfessori; i++){
            Thread professori = new Thread(new Professore(i, tutor));
            professori.setPriority(10);
            professori.start();



        }

        //***1
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //creo tesisti
        for(int i = 0; i < numeroDiTesisti; i++){
            Thread tesisti = new Thread(new Tesista(i, tutor));
            tesisti.setPriority(2);
            tesisti.start();


        }

        //***2
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }





        //creo studenti
        for(int i = 0; i < numeroDiStudenti; i++){
            Thread studenti = new Thread(new Studente(i, tutor));
            studenti.setPriority(2);
            studenti.start();

        }




    }
}

