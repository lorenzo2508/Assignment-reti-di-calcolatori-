
/*                                                          TESTO ASSEGNAMENTO 6
*
                        * Scrivere un programma Java che implementi un server HTTP che gestisca richieste
                        * di trasferimento di file di diverso tipo (es. immagini jpeg, gif) provenienti da un browser Web.

                        *Il server sta in ascolto su una porta nota al client (es. 6789).
                        *Il server gestisce richieste HTTP di tipo GET alla request URL http://localhost:port/filename.
                        *Le connessioni possono essere non persistenti.
                        *Usare le classi Socket e ServerSocket per sviluppare il programma server.
                        *Per inviare al server le richieste, utilizzare un qualsiasi browser. In alternativa, se avete un sistema Unix-based
                        *(oppure il WSL su Windows) potete utilizzare cURL da terminale o wget.
* */

import java.io.*;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        /*inizializzazione della porta*/
        System.out.println("Inserisci la porta su cui vuoi che il server stia in ascolto: ");
        Scanner numPorta = new Scanner(System.in);
        int porta = numPorta.nextInt(); //porta su cui sta in ascolto il server. La faccio inserire da tastiera all'utente.


        System.out.println("Server is running");
        //alloco il ServerSocket e gli passo la porta su cui sta in ascolto
        ServerSocket listenSocket = new ServerSocket(porta); //Creo il socket su cui ascolta il sever, e aspetta per una richiesta di connessione

        //alloco un thread pool per gestire le connessioni di più client
        ExecutorService threadPool = Executors.newCachedThreadPool();
        boolean run = true;
        while (run) {
            /*avvio un thread pool fatto da WorkerServer che si occuperanno di servire i client connessi
             * ho deciso di usare un thread pool in questo modo posso avere più connessioni da più client al server
             * ogni thread invierà il file richiesto. i file disponibili si trovano nella cartella MediaFile*/
            threadPool.execute(new WorkerServer(listenSocket.accept()));

        }
        threadPool.shutdown();
        if(!threadPool.isTerminated()){
            threadPool.shutdownNow();
        }
    }
}
