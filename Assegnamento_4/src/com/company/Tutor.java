package com.company;


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
    private  int contatoreProfessori = 0; //MONITOR CHE MI SERVE PER CAPIRE SE CI SONO ANCORA DEI PROFESSORI, SE CI SONO ALLORA STUDENTI E
    private  int contatoreTesisti = 0; //MONITOR CHE MI SERVE PER CAPIRE SE CI SONO DEI TESISTI CHE ASPETTANO


    //COSTRUTTORE
    public Tutor(int computerPerTesisti, Laboratorio laboratorio) {
        this.computerPerTesisti = computerPerTesisti;
        this.laboratorio = laboratorio;

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
            if(computer) return laboratorio.computers.indexOf(true); /* se il posto è libero ritorna il suo indice*/

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

    /*==================================================================================================================================
     *
     *                                                   METODI CHE HANNO BISOGNO DI
     *                                                   SINCORONIZZAZIONE. DA QUI IN GIU'.
     *
     *=====================================================================================================================================*/


    //effettua una richiesta di accesso al laboratorio, essendo un professore occupa tutto il laboratorio
    public synchronized void richiestaDelProfessore(Professore professore){
        try {
            //professore aspetta di ricevere il laboratorio
            contatoreProfessori++;
            while(!checkFreeComputerForProfessore()){
                System.out.println(ANSI_BLUE+"Tutor: professore " + professore.getIdentificativoPersona() +" in attessa di entrare in laboratorio" + ANSI_RESET);
                wait();
            }

            // ha finito di aspettare.
            contatoreProfessori--;
            occupaLaboratorioPerProfessore(); //si occupa tutto il laboratorio mettendo a false l'arraylist
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //===================================================================================================================================

    //effettua una richiesta di accesso al laboratorio per il tesista che accede al computer d' indice "computerPerTesisti"
    public synchronized void richiestaDelTesista(Tesista tesista, int computerPerTesisti){
        try {
            contatoreTesisti++;
            while(!checkFreeComputerForTesista(computerPerTesisti) || (contatoreProfessori > 0)){
                System.out.println(ANSI_GREEN + "Tutor: tesista " + tesista.getIdentificativoPersona() +" in attessa di entrare in laboratorio" + ANSI_RESET);
                wait();
            }

            contatoreTesisti--;
            occupaPcTesista(computerPerTesisti);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //===================================================================================================================================


    //ritorna l'indice dell'array list usato per simulare il laboratorio
    //termini non informatici: ritorna il valore del posto in laboratorio assegnato allo studente
    public synchronized int richiestaDelloStudente(Studente studente){

        int computerLibero = checkFreeComputerForStudente();
        try{

            while (contatoreProfessori > 0 || computerLibero == -1 ||  contatoreTesisti > 0 || (computerLibero == 10 && contatoreTesisti > 0)) {
                System.out.println(ANSI_RED + "Tutor: studente " + studente.getIdentificativoPersona() +" in attessa di entrare in laboratorio"+ ANSI_RESET);
                computerLibero = checkFreeComputerForStudente(); //controlla se c'è un posto libero per lo studente, altrimenti la condizione del while non viene calcolata bene perché computerLibero sarebbe sempre == -1
                wait();
            }
            laboratorio.computers.set(computerLibero, false); //setto tale computer a "false" facendolo diventare occupato
            System.out.println(ANSI_RED + "Tutor: lo studente " + studente.getIdentificativoPersona() + " ha acquisito il computer " + computerLibero + ANSI_RESET);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return computerLibero; //ritorna il valore del computer che è stato assegnato allo studente
    }

    //===================================================================================================================================


    //libera tutte le entry del laboratorio che erano state occupate dal professore
    public synchronized void uscitaDalLaboratorioDelProfessore(Professore professore){

        //vengono rilasciati tutti i computer del laboratorio (dunque tutti risettati a liberi "true")
        laboratorio.setComputers(true);
        notifyAll();

    }

    //===================================================================================================================================


    //libera la entry numero "computerPerTesisti" usata dai tesisti
    public synchronized void uscitaDalLaboratorioDelTesista(){

        //rilascio il computer usato dai tesisti mettendo a true il valore della entry di indice "computerPerTesisti"
        laboratorio.computers.set(computerPerTesisti, true);
        notifyAll();


    }

    //===================================================================================================================================


    //libera la entry numero "numeroDiPostoInLaboratorio" usata dagli studenti
    public synchronized void uscitaDalLaboratorioDelloStudente(int numeroDiPostoInLaboratorio, Studente studente){

        //rilascio il computer usato dallo studente mettendo a true il valore della entry di indice "numeroDiPostoInLaboratorio"
        laboratorio.computers.set(numeroDiPostoInLaboratorio, true);
        notifyAll();

    }
}
