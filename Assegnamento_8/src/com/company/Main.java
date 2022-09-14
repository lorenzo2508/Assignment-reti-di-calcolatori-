package com.company;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;


public class Main {


    public static void main(String[] args) throws IOException {
        /*Creo una lista di conti correnti che per ogni conto corrente presente nella lista ha a sua volta una lista
        * di movimenti*/
        ArrayList<ContoCorrente> listaContiCorrenti = new ArrayList<>();
        Random random = new Random();
        int numeroCasualeDiContiCorrenti = random.nextInt(1000);
        int numeroCasualeDiMovimenti = random.nextInt(1000);
        for(int i = 0; i < numeroCasualeDiContiCorrenti; i++){
            ContoCorrente contoCorrente = new ContoCorrente();
            for (int j = 0; j<numeroCasualeDiMovimenti; j++){
                Movimento movimento = new Movimento();
                contoCorrente.addMovimentiAtContoCorrente(movimento);
            }
            numeroCasualeDiMovimenti = random.nextInt(1000);
            listaContiCorrenti.add(contoCorrente);

        }
        /*=====================================CREO IL FILE JSON========================================*/

        //converto la lista dei contiCorrenti in Json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int firstTime = 0;
        String conto;

        String path = "src/com/company/"; //percorso dove viene messo il file
        FileOutputStream fileOutputStream = new FileOutputStream(path + "file.json");
        FileChannel channel = fileOutputStream.getChannel();
        if(listaContiCorrenti.isEmpty()){
            System.err.println("Lista dei conti correnti vuota" );
            System.exit(1);
        }

        //prendo i singoli oggetti e li scrivo sul file
        for (ContoCorrente contoCorrente : listaContiCorrenti) {

            //se sono alla prima scrittura (quindi al primo oggetto da serializzare sul Json) aggiungo il carattere "["
            if(firstTime == 0 ){
                conto ="[" + "\n" + gson.toJson(contoCorrente) + "," + "\n"; //metto anche la virgola e l'accapo in modo da formattare correttamente
                ByteBuffer buffer = ByteBuffer.allocateDirect(conto.length()); //alloco il buffer
                buffer.put(conto.getBytes(StandardCharsets.UTF_8)); //metto sul buffer i byte dell'oggetto
                buffer.flip(); //preparo per scrivere sul canale (quindi sul file)
                while(buffer.hasRemaining()){ //finché c'è roba sul buffer scrivo sul canale
                    channel.write(buffer); //scrivo sul canale (quindi sul file)
                }
                buffer.clear(); //pulisco il buffer per una nuova scrittura (in realtà quello che viene fatto è rimettere il
                                //puntatore position a zero
                firstTime++; //aggiorno il contatore in modo da segnalare che non sono più al primo oggetto da serializzare
            }
            //se questa condizione si verifica significa che sono all'ultimo oggetto da serializzare sul Json
            if(firstTime == (listaContiCorrenti.size())){
                conto = gson.toJson(contoCorrente)  + "\n" + "]" ; //formatto in modo corretto per l'ultimo oggetto

                ByteBuffer buffer = ByteBuffer.allocateDirect(conto.length()); //alloco il buffer
                buffer.put(conto.getBytes(StandardCharsets.UTF_8));//metto sul buffer i byte dell'oggetto
                buffer.flip(); //preparo per scrivere sul canale (quindi sul file)
                while(buffer.hasRemaining()){ //finché c'è roba sul buffer scrivo sul canale
                    channel.write(buffer); //scrivo sul canale (quindi sul file)
                }
            }
            //caso in cui sto serializzando oggetti compresi tra il primo oggetto e l'ultimo (quindi quelli di mezzo)
            else {
                conto = gson.toJson(contoCorrente);
                conto += ","; //formatto la stringa in modo corretto (aggiungendo la virgola che separa i vari campi del Json
                ByteBuffer buffer = ByteBuffer.allocateDirect(conto.length()); //alloco il buffer
                buffer.put(conto.getBytes(StandardCharsets.UTF_8));//metto sul buffer i byte dell'oggetto
                buffer.flip(); //preparo per la scrittura sul canale
                while(buffer.hasRemaining()){//finché c'è roba sul buffer scrivo sul canale
                    channel.write(buffer); //scrivo sul canale
                }
                firstTime++; //incremento il contatore di oggetti
            }

        }







        /*================================================================================================================

        * ==============================AVVIO LA LETTURA DEL FILE JSON AVVIANDO=========================================
        * ==============================IL THREAD CHE SE NE OCCUPA ESSO OLTRE ==========================================
        * ==============================ALLA LETTURA SI OCCUPA ANCHE, USANDO ===========================================
        * ==============================UN THREAD POOL DI CONTARE LE OCCORENZE =========================================
        * ==============================DELLE CAUSALI DEI MOVIMENTI E ALLA FINE ========================================
        * ==============================STAMPA PER OGNI POSSIBILE CUSALE IL ============================================
        * ==============================NUMERO DELLE OCCORENZE==========================================================

        ==================================================================================================================*/

        WorkerFileReader workerFileReader = new WorkerFileReader(path);
        Thread thread = new Thread(workerFileReader);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
