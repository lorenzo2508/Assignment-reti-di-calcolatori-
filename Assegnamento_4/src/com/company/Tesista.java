package com.company;

public class Tesista extends Persona{



    //VARIABLI D'ISTANZA

    protected int numeroComputerUsatoDalTesista = 10;
    public Tesista(int identificativoPersona, Tutor tutor) {
        super(identificativoPersona, tutor);

    }

    /*======================================================================================================
     *                               IMPLEMENTAZIONE DEI METODI DELLA CLASSE
     *                               ASTRATTA PERSONA CHE SI TROVA NELLO STESSO
     *                               PACKAGE.
     * ======================================================================================================*/

    @Override
    public void richiestaDiAccesso() {
        System.out.println(ANSI_GREEN + "il tesista " + this.getIdentificativoPersona() + " vuole accedere al laboratorio" + ANSI_RESET);
        tutor.richiestaDelTesista(this, numeroComputerUsatoDalTesista);
        System.out.println(ANSI_GREEN +  "IL TESISTA " + this.getIdentificativoPersona() + " SI E' SEDUTO AL COMPUTER " +numeroComputerUsatoDalTesista + ANSI_RESET);

    }

    @Override
    public void uscitaLaboratorio() {
        System.out.println(ANSI_GREEN +"il tesista " + this.getIdentificativoPersona() + " esce dal laboratorio lasciando il pc " + numeroComputerUsatoDalTesista + " libero" + ANSI_RESET);
        tutor.uscitaDalLaboratorioDelTesista();

    }
}
