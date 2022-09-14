package com.company;

public class Professore extends Persona{
    public Professore(int identificativoPersona, Tutor tutor) {
        super(identificativoPersona, tutor);
    }

    /*======================================================================================================
     *                               IMPLEMENTAZIONE DEI METODI DELLA CLASSE
     *                               ASTRATTA PERSONA CHE SI TROVA NELLO STESSO
     *                               PACKAGE.
     * ======================================================================================================*/

    @Override
    public void richiestaDiAccesso() {
        System.out.println(ANSI_BLUE  + "professore " + this.getIdentificativoPersona() + " vuole accedere al laboratorio" + ANSI_RESET);
        tutor.richiestaDelProfessore(this);
        System.out.println(ANSI_BLUE  +"IL PROFESSORE " +this.getIdentificativoPersona()+ " E' ENTRATO IN LABORATORIO"+ ANSI_RESET);

    }

    @Override
    public void uscitaLaboratorio() {
        System.out.println(ANSI_BLUE + "professore " + this.getIdentificativoPersona() + " esce dal laboratorio" + ANSI_RESET);
        tutor.uscitaDalLaboratorioDelProfessore();

    }

}
