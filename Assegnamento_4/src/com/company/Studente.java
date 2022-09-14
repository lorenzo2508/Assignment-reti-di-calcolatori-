package com.company;

public class Studente extends Persona{

    //VARIABLI D'ISTANZA

    protected int numeroComputer;
    public Studente(int identificativoPersona, Tutor tutor) {
        super(identificativoPersona, tutor);

    }

    /*======================================================================================================
     *                               IMPLEMENTAZIONE DEI METODI DELLA CLASSE
     *                               ASTRATTA PERSONA CHE SI TROVA NELLO STESSO
     *                               PACKAGE.
     * ======================================================================================================*/

    @Override
    public void richiestaDiAccesso() {

        System.out.println( ANSI_RED +  "lo studente " + this.getIdentificativoPersona() + " vuole accedere al laboratorio" + ANSI_RESET);
        numeroComputer = tutor.richiestaDelloStudente(this);
        System.out.println(ANSI_RED +"LO STUDENTE " + this.getIdentificativoPersona() + " SI E' SEDUTO AL COMPUTER " +  numeroComputer + ANSI_RESET);

    }

    @Override
    public void uscitaLaboratorio() {
        System.out.println(ANSI_RED + "lo studente " + this.getIdentificativoPersona() + " esce dal laboratorio"+ ANSI_RESET);
        tutor.uscitaDalLaboratorioDelloStudente(numeroComputer, this);
        System.out.println(ANSI_RED +  "il posto che si è liberato è il numero " + numeroComputer + ANSI_RESET);

    }
}
