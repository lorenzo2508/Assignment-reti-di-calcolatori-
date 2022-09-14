

public class Timer implements Runnable {
    private long returnTime;
    private boolean stopTimer = false;
    //COSTRUTTORE
    public Timer(long returnTime) {
        this.returnTime = returnTime;
    }


    @Override
    public void run() {
        //Prendo il tempo attuale
        while (!Thread.currentThread().isInterrupted() && !stopTimer) {
            long endTime = System.currentTimeMillis();
            returnTime = (endTime);

        }


    }

    //METODO PER SETTARE STOPTIMER A TRUE (IN QUESTO MODO FALSIFICO LA GUARDIA DEL WHILE SOPRA E IL THREAD TERMINA)
    public synchronized void setStopTimer(boolean stopTimer) {
        this.stopTimer = stopTimer;
    }



    public long getReturnTime() {
        return returnTime;
    }

}
