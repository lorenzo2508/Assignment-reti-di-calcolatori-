package com.company;
//=============================================================================================================
            /*se viene inserito come input (dimensione della coda del pool) 20, il pool termina con
            * .shutDownNow, mentre con 10 il pool termina normalmente con .shutdown (ovvimante tenendo questa
            * combinazione di tempi nella sleep di persona  che rappresenta
            * il [tempo che ci impiega una persona a svolgere il suo task] cambiando l'intervallo di tempo
            * della sleep, e della dimensione della coda del pool,  cambia anche il comportamento della
            * simulazione dell'ufficio postale. */
//============================================================================================================


import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;


public class Main {
    //COLORI PER STAMPE
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[36m";
    public static final String ANSI_BLUE = "\u001B[34m";
    private static final long terminationDelay = 8000;

    public static void main(String[] args) {
        System.out.print("\r    IL PROGRAMMA SI INTERROMPERA' AUTONOMAMENTE DOPO 1 MINUTO SIMULANDO L'ORARIO DI CHIUSURA DI UN UFFICO POSTALE");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //=============================================================================================
        //                 SET DEI VINCOLI SULL'UFFICIO POSTALE, DUNQUE SUL POOL
        //=============================================================================================
        //set della dimensione della coda del pool
        System.out.print("\r    inserisci la dimensione della fila difronte agli sportelli ");
        //dimensione coda
        Scanner dimFila = new Scanner(System.in);
        int size = dimFila.nextInt();
        //coda bloccante del thread pool
        ArrayBlockingQueue<Runnable> filaDifronteAgliSportelli = new ArrayBlockingQueue(size);
        //istazio oggetto di tipo ufficio postale per poter accedere alle sue variabili d'istanza e poter settare il pool
        UfficioPostale ufficioPostale = new UfficioPostale(filaDifronteAgliSportelli);
        //set del numero di sportelli
        ufficioPostale.setNumeroSportelliPostali(4);
        //sala d'attesa (quella grande)
        ArrayList<Persona> salaDiAttesa = new ArrayList<>();
        ufficioPostale.setSalaDiAttesa(salaDiAttesa);
        //creazione del pool
        ExecutorService pool = new ThreadPoolExecutor(ufficioPostale.getNumeroSportelliPostali(), ufficioPostale.getNumeroSportelliPostali(),
                /************************************************/ 0, TimeUnit.MILLISECONDS, ufficioPostale.getFilaDifronteAgliSportelli(),
                /******************************************************/new ThreadPoolExecutor.AbortPolicy());



        //===========================================================================================
        /*SCOMMENTANDO IL CODICE SOTTOSTANTE SI PUO' INSERIRE UN VALORE A PIACERE PER
         * IL TEMPO DI DURATA DELLA SIMULAZIONE, ANZI CHE FARLA DURARE 1 MINUTO.
         * IL TEMPO DEVE ESSERE INSERITO IN SECONDI
         * DEVE ESSERE CAMBIATO IL VALORE DENTRO AL METODO SET ORARIO METTENDO LA VARIABILE
         * "SECONDI"*/
        //===========================================================================================

        //****set del tempo di simulazione dell'ufficio postale.

//        System.out.print("\r inserisci l'orario di apertura dell'ufficio (quanto vuoi, in secondi, che l'ufficio stia aperto) ");
//
        //*****orario ufficio postale

//        Scanner orario = new Scanner(System.in);
//        long secondi = orario.nextInt();
//          secondi = secondi * 1000;


        int i = 0;
        Time time = new Time();
        time.setOrarioDiAperturaRelativo(60); //<---------------- QUI METTERE "secondi" AL POSTO DI 60 UNA VOLTA SCOMMENTATO IL CODICE SOPRASTANTE
        Thread chronometer = new Thread(time);
        chronometer.start();

        while(chronometer.getState() != Thread.State.TERMINATED){
            //faccio dormire il thread main per qualche secondo in modo tale da simulare uno stream di clienti che arriva ogni 0,1 secondi
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // CREO L'ISTANZE DI PERSONA CHE ANDRANNO NEL POOL E NELLA SALA DI ATTESA
            Persona persona = new Persona(i);
            /*Blocco try catch in caso qualche task tentasse di andare nella coda del pool anche se piena
            * in tal caso viene respinto e sollevata l'eccezione con la AbortPolicy */
            try {

                //vari if per la gestione della coda

                //SE IL LA SALA D'ATTESA E' VUOTA E C'E' SPAZIO NELLA CODA ALLORA SI AGGIUNGE DIRETTAMENTE ALLA CODA DEL POOL
                if ((ufficioPostale.getFilaDifronteAgliSportelli().size() < size) && ufficioPostale.getSalaDiAttesa().isEmpty()){
                    System.out.println( ANSI_BLUE + "direttamente in coda per gli sportelli" + ANSI_RESET);
                    pool.execute(persona); //aggiungo alla coda del pool
                }
                //SE LA CODA DEL POOL E' PIENA SI AGGIUNGE IN SALA D'ATTESA [IMPLEMENTATA COME UN ARRAY LIST]
                else if (!(ufficioPostale.getFilaDifronteAgliSportelli().size() < size)){
                    //controllo di non superare MAX_VALUE per non adare in overflow
                    if(ufficioPostale.getSalaDiAttesa().size() < Integer.MAX_VALUE) {
                        System.out.println(ANSI_GREEN + "coda piena sala di attesa biglietto numero: "+ persona.getNumeroDellaMatrice() + ANSI_RESET);
                        ufficioPostale.getSalaDiAttesa().add(persona); //aggiungo in sala d'attesa
                    }
                    /*se sono in overflow faccio dormire un po' il main in questo modo faccio svuotare la coda del pool
                    * in questo modo posso far affluire dalla sala (arraylist) al pool un po' di persone così facendo svuoto un po'
                    * l'arraylist ((ho pensato a questa implementazione per simulare al meglio un ufficio postale
                    * immaginando la situazione in cui alcune persone aspettano fuori dalla sala)) */
                    else{
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                //SE LA CODA DEL POOL SI LIBERA DI 1 O PIU' POSTI E LA SALA DI ATTESA HA PERSONE IN FILA ESSE VENGONO AGGIUNTE ALLA CODA DEL POOL
                else if((ufficioPostale.getFilaDifronteAgliSportelli().size() < size) && !ufficioPostale.getSalaDiAttesa().isEmpty()){
                    System.out.println(ANSI_YELLOW + "dalla sala di attesa alla coda biglietto numero: " + ufficioPostale.getSalaDiAttesa().get(0).getNumeroDellaMatrice() + ANSI_RESET);
                    pool.execute(ufficioPostale.getSalaDiAttesa().get(0));
                    ufficioPostale.getSalaDiAttesa().remove(0);
                }
                //IN TUTTI GLI ALTRI CASI AGGIUNGO IN SALA D'ATTESA
                else {
                    if(ufficioPostale.getSalaDiAttesa().size() < Integer.MAX_VALUE) {
                        System.out.println(ANSI_GREEN + "coda piena sala di attesa biglietto numero: " +persona.getNumeroDellaMatrice() +  ANSI_RESET);
                        ufficioPostale.getSalaDiAttesa().add(persona);
                    }

                }

            }catch (RejectedExecutionException e){
                System.err.println("persona con biglietto " + persona.getNumeroDellaMatrice() + " ha tentato di entrare nella coda piena");
            }
            i++;

        }
        System.out.println(ANSI_RED + "SIAMO GIUNTI ALL'ORARIO DI CHIUSURA, LE POSTE CHIUDONO. verranno serviti gli ultimi clienti rimasti" + ANSI_RESET);
        //terminazione del pool
        pool.shutdown();

        //se l'intervallo di tempo di 8 secondi non è sufficiente per far terminare il pool con la shutdown
        //il pool viene terminato immediatamente con la shutdownNow
        try {
            if(!pool.awaitTermination(terminationDelay, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
                System.out.println(ANSI_RED + "Tempo a disposizione per la chiusura terminato, CHIUSURA FORZATA");
            }
        }catch (InterruptedException e){
            pool.shutdownNow();

        }





    }
}
