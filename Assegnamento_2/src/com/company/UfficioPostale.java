package com.company;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class UfficioPostale {
    //VARIABILI D'ISTANZA
    private ArrayBlockingQueue <Runnable> filaDifronteAgliSportelli; //coda del pool
    private int numeroSportelliPostali; //numero di thread del pool
    private ArrayList<Persona> salaDiAttesa; //sala di attesa dell'ufficio


    //costruttore
    public UfficioPostale(ArrayBlockingQueue<Runnable> arrayBlockingQueue) {
        this.filaDifronteAgliSportelli = arrayBlockingQueue;
    }
    //getters
    public ArrayBlockingQueue<Runnable> getFilaDifronteAgliSportelli() {
        return filaDifronteAgliSportelli;
    }

    public int getNumeroSportelliPostali() {
        return numeroSportelliPostali;
    }

    public ArrayList<Persona> getSalaDiAttesa() {
        return salaDiAttesa;
    }
    //setters
    public void setNumeroSportelliPostali(int numeroSportelliPostali) {
        this.numeroSportelliPostali = numeroSportelliPostali;
    }

    public void setSalaDiAttesa(ArrayList<Persona> salaDiAttesa) {
        this.salaDiAttesa = salaDiAttesa;
    }
}
