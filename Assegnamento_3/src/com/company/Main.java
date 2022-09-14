package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main  {
/*ASSEGNAMENTO 3 sulle lock e variabili condizione. Nessuno può essere interrotto, quindi se arriva un professore e ci sono studenti comunque
* non vengono mandati via dal laboratorio
* fare 3 thread, uno per lo studente uno per il professore e uno per il tesista, mentre il laboratorio è la risorsa condivisa
* deve essere pensata come una classe con i metodi per gestire gli accessi ad esso. */

    protected int computerPerTesisti = 10;

    public static void main(String[] args) {

        //VENGONO SETTATI I VARI VINCOLI DELLA SPECIFICA

        Main objOnlyForSetComputerPerTesisti = new Main(); //oggetto creato solo per settare computerPerTesisti in modo da poter essere più facilmente modificabile.
        ArrayList <Boolean> computers = new ArrayList<>(20);//laboratorio, simulato come un arraylist di booleani con capacità (posti a sedere) = 20

        Laboratorio laboratorio = new Laboratorio(computers); //istanza di laboratorio
        //setto inizialmente il laboratorio a completamente vuoto, simulando una situazione dove inizialmente il laboratorio è completamente libero
        //immaginando la mattina preso di un classico giorno settimanale.
        laboratorio.setComputers(true);
        //istanza del tutor che è colui che si occupa della gestione del laboratorio e degli accessi ad esso
        Tutor tutor = new Tutor(objOnlyForSetComputerPerTesisti.computerPerTesisti, laboratorio);

        /*
        *                                           CODICE PER GLI ARGOMENTI CHE DEVONO ESSERE
        *                                           PASSATI DA TASTIERA.
        * */
        int numeroDiStudenti;
        int numeroDiTesisti;
        int numeroDiProfessori;
        Scanner newInteger = new Scanner(System.in);
        System.out.println("inserisci il numero di PROFESSORI con cui vuoi avviare la simulazione");
        /*NUMERO DI PROFESSORI E NUMERO DI STUDENTI ERANO INVERTITI NELLA CONSEGNA QUINDI L'ESECUZIONE NON E' DEL TUTTO GIUSTA
        * BESTAVA INERTIRE "numeroDiProfessori" E "numeroDiStudenti"  */

        numeroDiProfessori= newInteger.nextInt();
        System.out.println("inserisci il numero di STUDENTI con cui vuoi avviare la simulazione");
        numeroDiStudenti = newInteger.nextInt();
        System.out.println("inserisci il numero di TESISTI con cui vuoi avviare la simulazione");
        numeroDiTesisti = newInteger.nextInt();

        /*CREO I VARI THREAD CHE ANDRANNO A SIMULARE LE PERSONE CHE ACCEDONO AL LABORATORIO. */


        //creo professori

        for(int i = 0; i < numeroDiProfessori; i++){
            Thread professori = new Thread(new Professore(i, tutor));
            professori.start();
        }



        //creo studenti
        for(int i = 0; i < numeroDiStudenti; i++){
            Thread studenti = new Thread(new Studente(i, tutor));
            studenti.start();
        }

        //creo tesisti
        for(int i = 0; i < numeroDiTesisti; i++){
            Thread tesisti = new Thread(new Tesista(i, tutor));
            tesisti.start();
        }





    }
}
