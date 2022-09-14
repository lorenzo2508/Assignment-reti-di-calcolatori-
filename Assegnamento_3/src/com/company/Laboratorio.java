package com.company;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

public class Laboratorio {
    protected ArrayList <Boolean> computers;


    public Laboratorio(ArrayList<Boolean> computers) {
        this.computers = computers;
        for (int i = 0; i < 20; i ++){
            this.computers.add(true);
        }
    }

    public ArrayList<Boolean> getComputers() {
        return computers;
    }

    //setta tutti i computer a true. Quindi avviabili all'utilizo
    //@work
    public void setComputers(boolean bool) {
        for(int i = 0; i < 20; i++){
            computers.set(i, bool);
        }
    }

    //se tutti i computer sono liberi il metodo ritorna vero
    //@work
    public boolean laboratorioLibero() {
        for (Boolean computer : computers) {
            if (!computer)
                return false;
        }
        return true;
    }

}
