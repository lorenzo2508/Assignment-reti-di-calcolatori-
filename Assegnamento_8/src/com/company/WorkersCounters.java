package com.company;

import java.util.ArrayList;

public class WorkersCounters implements Runnable {
    //VARIABILI D'ISTANZA
    ContoCorrente contoCorrente;
    WorkerFileReader workerFileReader;

    //COSTRUTTORE
    public WorkersCounters(ContoCorrente contoCorrente, WorkerFileReader workerFileReader1) {
        this.contoCorrente = contoCorrente;
        workerFileReader = workerFileReader1;
    }

    @Override
    public void run() {
        ArrayList<Movimento> listaDeiMovimentiPerContoCorrente = contoCorrente.getListaDeiMovimenti();
        /*per ogni movimento (che si trova nella lista dei movimenti relativi al conto corrente passato
         nel costruttore) relativo al conto corrente passato conto le occorrenze di ogni tipo di movimento aggiornando un contatore
         globale che si trova nella classe WorkerFileReader tra le sue variabili d'istanza
         i metodi forniti per aggiornare i contatori globali sono synchronized in modo da avere un accesso thread safe alle risorse condivise
         */

        int contatoreF24 = 0;
        int contatoreBollettini = 0;
        int contatoreAccredito = 0;
        int contatoreBonifico = 0;
        int contatorePagoBancomat = 0;
        for (Movimento movimento: listaDeiMovimentiPerContoCorrente) {



            //a seconda del tipo di movimento aggiorno il contatore
            if(movimento.getTipoMovimento().equals("F24")){ //movimento di tipo: F24
                //aggiorno il contatore nella hashmap (alla chiave corrisondente F24)
                workerFileReader.getMapForCausaliContiCorrenti().put("F24", workerFileReader.getMapForCausaliContiCorrenti().get("F24") + 1);


            }
            if(movimento.getTipoMovimento().equals("Bonifico")){//movimento di tipo: Bonifico
                //aggiorno il contatore nella hashmap (alla chiave corrisondente Bonifico )
                workerFileReader.getMapForCausaliContiCorrenti().put("Bonifico", workerFileReader.getMapForCausaliContiCorrenti().get("Bonifico") + 1);

            }
            if(movimento.getTipoMovimento().equals("Accredito")){//movimento di tipo: Accredito
                //aggiorno il contatore nella hashmap (alla chiave corrisondente Accredito )
                workerFileReader.getMapForCausaliContiCorrenti().put("Accredito", workerFileReader.getMapForCausaliContiCorrenti().get("Accredito") + 1);
            }
            if(movimento.getTipoMovimento().equals("Bollettino")){//movimento di tipo: Bollettino
                //aggiorno il contatore nella hashmap (alla chiave corrisondente Bollettino )
                workerFileReader.getMapForCausaliContiCorrenti().put("Bollettino", workerFileReader.getMapForCausaliContiCorrenti().get("Bollettino") + 1);


            }
            if(movimento.getTipoMovimento().equals("PagoBancomat")){//movimento di tipo: pagoBancomat
                //aggiorno il contatore nella hashmap (alla chiave corrisondente PagoBancomat )
                workerFileReader.getMapForCausaliContiCorrenti().put("PagoBancomat", workerFileReader.getMapForCausaliContiCorrenti().get("PagoBancomat") + 1);


            }

        }


    }
}
