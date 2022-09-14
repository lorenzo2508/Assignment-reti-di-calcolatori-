package com.company;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerFileReader implements Runnable{
    //VARIABILI D'ISTANZA
    private String path; //percorso del file (non contiene il nome del file, esso viene concatenato sotto)

    /*USo QUESTA HASHMAP CON CHIAVI I TIPI DI MOVIMENTO E COME VALORE IL CONTATORE
    * A QUESTO PUNTO QUELLO CHE VIENE FATTO E' mettere i valori nella mappa e poi ciclando sul Json quando incontro un tipo di movimento
    * vado nella hashmap e incremento il valore che corrisponderà alla frequenza del tipo di movimento. poi per stamparli
    * navigo la mappa e stampo le entry */
    protected ConcurrentHashMap<String, Integer> mapForCausaliContiCorrenti = new ConcurrentHashMap();


    //COSTRUTTORE
    public WorkerFileReader(String path) {
        this.path = path + "file.json"; //concateno il nome del file al percorso in modo che possa essere trovato
        mapForCausaliContiCorrenti.put("F24", 0);
        mapForCausaliContiCorrenti.put("Accredito", 0);
        mapForCausaliContiCorrenti.put("Bonifico", 0);
        mapForCausaliContiCorrenti.put("Bollettino", 0);
        mapForCausaliContiCorrenti.put("PagoBancomat", 0);
    }

    @Override
    public void run() {
        //creo il thread pool che andrà a svolgere il compito di contare le occorrenze delle causali (tipi di movimento)
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try {
            //leggo il file Json e usando il metodo .fromJson() lo serializzo in oggetti della classe ContoCorrente
            JsonReader fileReader = new JsonReader(new InputStreamReader(new FileInputStream(path)));
            fileReader.beginArray();
            while (fileReader.hasNext()){
                Gson gson = new Gson();
                ContoCorrente contoCorrente = gson.fromJson(fileReader, ContoCorrente.class);
                threadPool.execute(new WorkersCounters(contoCorrente, this)); //passo l'oggetto serializzato dal Json al thread
                                                                                            //pool che ci lavora sopra contando le occorrenze
            }
            //chiudo il thread pool
            threadPool.shutdown();
            if(!threadPool.isTerminated()){
                threadPool.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        printContatori();


    }
    /*METODI SYNCHRONIZED USATI PER INCREMENTARE I CONTATORI DELLE OCCORRENZE DELLE CAUSALI PER OGNI CONTO CORRENTE */


    //METODO PER STAMPARE I CONTATORI
    private void printContatori(){

        for (Map.Entry<String, Integer> entry: mapForCausaliContiCorrenti.entrySet()) {
            System.out.println("numero di " + entry.getKey() + ":" + " " + entry.getValue());


        }
    }

    public ConcurrentHashMap<String, Integer> getMapForCausaliContiCorrenti() {
        return mapForCausaliContiCorrenti;
    }
}
