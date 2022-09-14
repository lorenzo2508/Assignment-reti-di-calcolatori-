package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Tutor {

    //VARIABILI D'ISTANZA CHE FANNO DA COLORI PER LA STAMPA
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[36m";
    public static final String ANSI_BLUE = "\u001B[34m";

    //VARIABLI D'ISTANZA

    private int computerPerTesisti ;//identifica l'indice del computer che usano i tesisti (perché è l'unico ad avere il sw che a loro serve)
    protected Laboratorio laboratorio;
    private Condition waitStudenti; //===============================================================
    private Condition waitProfessori; /* conditional variables per professori, studenti e tesisti*/
    private Condition waitTesisti;  //================================================================
    private ReentrantLock labLock;

    //COSTRUTTORE
    public Tutor(int computerPerTesisti, Laboratorio laboratorio) {
        this.computerPerTesisti = computerPerTesisti;
        this.laboratorio = laboratorio;
        labLock = new ReentrantLock();
        waitStudenti = labLock.newCondition();
        waitProfessori = labLock.newCondition();
        waitTesisti = labLock.newCondition();
    }


    //ritorna vero se il computer numero 10 (quello col sw di cui il tesista ha bisogno) è libero
    //@work
    public boolean checkFreeComputerForTesista(int numeroComputerUsatoDalTesista){
        return laboratorio.computers.get(numeroComputerUsatoDalTesista);
    }

    //===================================================================================================================================

    //occupa il computer per un tesista mettendo a false il valore della entry di posizione "computerPerTesisti"
    //@work
    public void occupaPcTesista (int computerPerTesisti){
        laboratorio.computers.set(computerPerTesisti, false);
    }

    //===================================================================================================================================


    //ritorna il posto (l'indice dell'array) che lo studente può occupare in laboratorio
    //ritorna -1 se non ci sono computer liberi
    //@work
    public int checkFreeComputerForStudente(){
        for (Boolean computer : laboratorio.computers) {
            if(computer) return laboratorio.computers.indexOf(computer); /* se il posto è libero ritorna il suo indice*/

        }
        return -1; //se non ci sono posti liberi ritorna -1
    }

    //===================================================================================================================================


    //ritorna vero solo se tutto il laboratorio è vuoto quindi tutti i posti disponibili.
    //@work
    public  boolean checkFreeComputerForProfessore(){
        return laboratorio.laboratorioLibero();
    }

    //===================================================================================================================================


    //occupa tutti i posti del laboratorio per un professore mettendo a false ogni entry dell'arraylist
    //@work
    public void occupaLaboratorioPerProfessore(){
        laboratorio.setComputers(false);
    }

    //===================================================================================================================================


    //effettua una richiesta di accesso al laboratorio, essendo un professore occupa tutto il laboratorio
    public void richiestaDelProfessore(Professore professore){
        labLock.lock();
        try {
            while(!checkFreeComputerForProfessore()){
                System.out.println(ANSI_BLUE+"Tutor: professore " + professore.getIdentificativoPersona() +" in attessa di entrare in laboratorio" + ANSI_RESET);
                waitProfessori.await();
            }
            occupaLaboratorioPerProfessore(); //si occupa tutto il laboratorio mettendo a false l'arraylist

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            labLock.unlock();
        }
    }

    //===================================================================================================================================

    //effettua una richiesta di accesso al laboratorio per il tesista che accede al computer d' indice "computerPerTesisti"
    public void richiestaDelTesista(Tesista tesista, int computerPerTesisti){
        labLock.lock();
        try{
            while(!checkFreeComputerForTesista(computerPerTesisti) || labLock.hasWaiters(waitProfessori)){
                System.out.println(ANSI_GREEN + "Tutor: tesista " + tesista.getIdentificativoPersona() +" in attessa di entrare in laboratorio" + ANSI_RESET);
                waitTesisti.await();
            }
            occupaPcTesista(computerPerTesisti);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            labLock.unlock();
        }

    }

    //===================================================================================================================================


    //ritorna l'indice dell'array list usato per simulare il laboratorio
    //termini non informatici: ritorna il valore del posto in laboratorio assegnato allo studente
    public int richiestaDelloStudente(Studente studente){
        labLock.lock();
        int computerLibero = -1;
        try{
            while((checkFreeComputerForStudente() == -1) || labLock.hasWaiters(waitProfessori)){
                System.out.println(ANSI_RED + "Tutor: studente " + studente.getIdentificativoPersona() +" in attessa di entrare in laboratorio"+ ANSI_RESET);
                waitStudenti.await();
            }
            computerLibero = checkFreeComputerForStudente(); //indice del computer libero
            laboratorio.computers.set(computerLibero, false); //setto tale computer a "false" facendolo diventare occupato
            System.out.println(ANSI_RED + "Tutor: lo studente " + studente.getIdentificativoPersona() + " ha acquisito il computer " + computerLibero + ANSI_RESET);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            labLock.unlock();
        }
        return  computerLibero; //ritorna il valore del computer che è stato assegnato allo studente


    }

    //===================================================================================================================================


    //libera tutte le entry del laboratorio che erano state occupate dal professore
    public void uscitaDalLaboratorioDelProfessore(){
        labLock.lock();
        //vengono rilasciati tutti i computer del laboratorio (dunque tutti risettati a liberi "true")
        laboratorio.setComputers(true);

        //risveglio i professori se ci sono, dando loro la precedenza
        if (labLock.hasWaiters(waitProfessori))
            waitProfessori.signal();
        //risveglio i tesisiti se ci sono e do loro la precedenza
        else{
            if(labLock.hasWaiters(waitTesisti))
                waitTesisti.signal();
            waitStudenti.signalAll();


        }
        labLock.unlock();


    }

    //===================================================================================================================================


    //libera la entry numero "computerPerTesisti" usata dai tesisti
    public void uscitaDalLaboratorioDelTesista(){
        labLock.lock();
        //rilascio il computer usato dai tesisti mettendo a true il valore della entry di indice "computerPerTesisti"
        laboratorio.computers.set(computerPerTesisti, true);

        //controllo la presenza di professori in coda e se il laboratorio è tutto libero, in tal caso faccio passare il professore
        if(labLock.hasWaiters(waitProfessori) && laboratorio.laboratorioLibero()){
            waitProfessori.signal();
        }
        //altrimenti controllo se ci sono altri tesisti e sveglio gli studenti.
        else{
            if(labLock.hasWaiters(waitTesisti))
                waitTesisti.signal();
            waitStudenti.signalAll();

        }
        labLock.unlock();


    }

    //===================================================================================================================================


    //libera la entry numero "numeroDiPostoInLaboratorio" usata dagli studenti
    public void uscitaDalLaboratorioDelloStudente(int numeroDiPostoInLaboratorio, Studente studente){
        labLock.lock();
        //rilascio il computer usato dallo studente mettendo a true il valore della entry di indice "numeroDiPostoInLaboratorio"
        laboratorio.computers.set(numeroDiPostoInLaboratorio, true);
        //controllo la presenza di professori in coda e se il laboratorio è tutto libero, in tal caso faccio passare il professore
        if(labLock.hasWaiters(waitProfessori) && laboratorio.laboratorioLibero()){
            waitProfessori.signal();
        }
        //altrimenti controllo se ci sono altri tesisti e sveglio gli studenti.
        else{
            if(labLock.hasWaiters(waitTesisti))
                waitTesisti.signal();
            waitStudenti.signalAll();

        }
        labLock.unlock();



    }
}
