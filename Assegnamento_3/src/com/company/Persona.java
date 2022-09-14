package com.company;



import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/*Classe astratta che implementa il metodo run e contiene i vari attributi che definiscono una entità di tipo persona
* astratta perché rimanda l'implementazione dei metodi "richiesta di accesso" e "uscitaLaboratorio" alle classi concrete che
* estendono persona, rispettivamente:
* -Studente
* -Professore
* -Tesista
* Una persona può fare un numero di accessi casuale al laboratorio, dunque questa cosa è stata implementata con una variabile di'stanza
* chiamata "numeroAccessi" che contiene un numero casuale tra 1 e 10. il metodo run del thread contiene un ciclo for(int i = 0; i < numeroAccessi; i++)
* che fa fare alla persona richieste di accesso e uscite da laboratorio fin quando non si arriva al valore "numeroAccessi" */
public abstract class Persona implements Runnable{

    //VARIABILI D'ISTANZA CHE FANNO DA COLORI PER LA STAMPA
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[36m";
    public static final String ANSI_BLUE = "\u001B[34m";

    //VARIABLI D'ISTANZA

    protected int numeroAccessi ; //numero di accessi che una persona può fare
    protected long permanenzaInLaboratorio; //tempo di lavoro di una persona in laboratorio
    protected long permanenzaFuoriLaboratorio ; //tempo che una persona passa fuori dal laboratorio (tipo esce per andare al bagno)
    protected int identificativoPersona;
    protected Tutor tutor;

    public Persona(int identificativoPersona, Tutor tutor) {
        this.identificativoPersona = identificativoPersona;
        this.tutor = tutor;
        this.numeroAccessi = ThreadLocalRandom.current().nextInt(10);
    }

    @Override
    public void run() {
        for(int i = 0; i < numeroAccessi; i++){
            //richiedo accesso al laboratorio
            richiestaDiAccesso();
            //dormo per tot millisecondi, simulando lo svolgimento di task nel laboratorio
            try {
                Thread.sleep(getPermanenzaInLaboratorio());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //esco dal laboratorio, e dormo tot millisecondi simulando l'uscita dal laboratorio per fare una pausa o un generico task al di fori
            //di esso.
            uscitaLaboratorio();
            try {
                Thread.sleep(getPermanenzaFuoriLaboratorio());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //GETTERS AND SETTERS

    public int getNumeroAccessi() {
        return numeroAccessi;
    }

    private long getPermanenzaInLaboratorio() {
        Random rand = new Random();
        return this.permanenzaInLaboratorio = ThreadLocalRandom.current().nextInt(2000);
    }

    private long getPermanenzaFuoriLaboratorio() {
        Random rand = new Random();
        return this.permanenzaFuoriLaboratorio = ThreadLocalRandom.current().nextInt(5000);
    }

    public int getIdentificativoPersona() {
        return identificativoPersona;
    }

    /****************************************************************************************************************
     *                                                QUESTI DUE SONO METODI ASTRATTI CHE
     *                                                VENGONO IMPLEMENTATI NELLE VARIE CLASSI CHE ESTENDONO
     *                                                LA CLASSE PERSONA E SERVONO RISPETTIVAMENTE PER LA RICHIESTA
     *                                                DI ACCESSO E PER IL RILASCIO DEL LABORATORIO
     * **************************************************************************************************************/

    public abstract void richiestaDiAccesso();

    public abstract void uscitaLaboratorio();
}
