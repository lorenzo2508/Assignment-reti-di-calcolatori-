package com.company;

//CLASSE USATA PER TENERE CONTO DELLO SCORRERE DEL TEMPO
//HO USATO UN ALTRO THREAD IN QUANTO SIA IL MAIN CHE IL THREAD PERSONA VANNO IN SLEEP E QUESTO POTREBBE PORTARE A DEGLI ERRORI NEL
//CALCOLO DELLO SCORRERE DEL TEMPO. QUINDI HO PENSATO CHE L'IMPLEMENTAZIONE GIUSTA FOSSE QUELLA DI USARE UN THREAD A PARTE PER GESTIRE
//L'INTERVALLO DI TEMPO IN CUI LO SPORTELLO DELLA POSTA E' APERTO.

public class Time implements Runnable {
    private long orarioDiAperturaRelativo;
    @Override
    public void run() {
        //set del tempo:
        long startTime = System.currentTimeMillis();
        boolean timeToEnd = true;
        long time = 0;
        while (timeToEnd){
            long endTime = System.currentTimeMillis();
            time = (endTime - startTime);
            if( time >= orarioDiAperturaRelativo)
                timeToEnd = false;
        }

    }
    public void setOrarioDiAperturaRelativo(long orarioDiAperturaRelativo) {
        this.orarioDiAperturaRelativo = orarioDiAperturaRelativo * 1000;
    }
}
