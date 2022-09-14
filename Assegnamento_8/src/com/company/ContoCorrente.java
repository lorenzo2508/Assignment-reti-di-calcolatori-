package com.company;

import java.util.ArrayList;
import java.util.UUID;


public class ContoCorrente {
    //VARIABILI D'ISTANZA
    private String nomeTitolareContoCorrente;
    private ArrayList<Movimento> movimenti = new ArrayList<>(); //ARRAYLIST CHE CONTIENE I MOVIMENTI, UNO PER OGNI CONTO CORRENTE

    //COSTRUTTORE
    public ContoCorrente() {
        this.nomeTitolareContoCorrente = UUID.randomUUID().toString(); // METODO CHE MI GENERA UNA STRINGA UNIVOCA COMPOSTA DA CARATTERI ALFA
                                                                        //NUMERICI CHE IDENTIFICA UN SINGOLO CORRENTISTA DELLA BANCA

    }
    //METODO PER AGGIUNGERE MOVIMENTI ALLA LISTA
    //I MOVIMENTI AGGIUNTI SONO GENERATI CASUALMENTE QUANDO VIENE COSTRUITO UN OGGETTO DI TIPO MOVIMENTO
    public void addMovimentiAtContoCorrente(Movimento movimento){
        movimenti.add(movimento);
    }

    public String getNomeTitolareContoCorrente() {
        return nomeTitolareContoCorrente;
    }

    public void getMovimenti() {
        for (Movimento movimento:
             movimenti) {
            System.out.println(movimento.getTipoMovimento());
        }
    }
    public  ArrayList<Movimento> getListaDeiMovimenti(){
        return movimenti;
    }
}
