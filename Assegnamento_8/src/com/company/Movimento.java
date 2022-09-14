package com.company;



import java.util.ArrayList;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Movimento {
    //VARIABILI D'ISTANZA
    private String dataDelMovimeto;
    private String tipoMovimento;

    //COSTRUTTORE
    public Movimento() {
        this.dataDelMovimeto = generaData(); // METODO CHE GENERA UNA DATA CASUALE PER IL MOVIMENTO
        this.tipoMovimento = generaMovimento();// METODO CHE GENERA CASUALMENTE IL TIPO DI MOVIMENTO
    }

    /*GENERO CASUALMENTE LA DATA*/
    private String generaData(){
        int giorno = ThreadLocalRandom.current().nextInt(1, 31); //genero casualmente giorni tra 1 e 30
        int mese = ThreadLocalRandom.current().nextInt(1, 13); // genero casualmente mese tra 1 e 12
        int anno = ThreadLocalRandom.current().nextInt(2019, 2022); // genero casualmente anno tra 2019 e 2021
        //ho messo questo if perché il programma lo sto facendo a novembre e la data di consegna sarà il 23 novembre
        //quindi mi sembra giusto non andare oltre con le date dei conti correnti visto che il 2021 non è ancora finito.
        if(anno == 2021){
            giorno = ThreadLocalRandom.current().nextInt(1, 24);
            mese = ThreadLocalRandom.current().nextInt(1, 12);
        }
        String data = Integer.toString(giorno) + "/" + Integer.toString(mese) + "/" + Integer.toString(anno); //compongo la data
        return data;
    }


    //METODO CHE RITORNA LA FATA DEL MOVIMENTO
    public String getDataDelMovimeto() {
        return dataDelMovimeto;
    }

    //MEDODO CHE RITORNA IL TIPO DEL MOVIMENTO
    public String getTipoMovimento() {
        return tipoMovimento;
    }


    //METODO CHE GENERA IL TIPO DI MOVIMENTO
    private String generaMovimento(){
        /* inserisco i tipi di movimento all'interno di un arrayList. Dopo di che genero un numero casuale che è compreso tra 0 e la
        * lunghezza dell'ArrayList. Questo numero fungerà da indice dell'arrayList ed il suo uso è quello di tirare giù casualmente
        * dall'arrayListi un tipo di movimento (implementati come delle stringhe)*/
        ArrayList<String> listaMovimentiPossibili = new ArrayList<>();
        listaMovimentiPossibili.add("F24");
        listaMovimentiPossibili.add("Bonifico");
        listaMovimentiPossibili.add("Accredito");
        listaMovimentiPossibili.add("Bollettino");
        listaMovimentiPossibili.add("PagoBancomat");
        int i = listaMovimentiPossibili.size(); //assegno ad i la lunghezza dell'array list così setto il bound per il random
        Random generatoreIndice= new Random();
        int index = generatoreIndice.nextInt(i); //genero l'indice casualmente tra 0 e i dove i = lunghezza ArrayList
        return listaMovimentiPossibili.get(index); //prendo dall'arraylist un tipo di movimento, casualmente (perché l'indice è casuale)

    }
}
