


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;//Usata per fare il parsing della richiesta HTTP del client
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerServer implements Runnable{

    //VARIABILI D'ISTANZA
     public Socket connectionSocket; //socket a su cui manderanno richiesta(il client) e risposta(il server)


    //Costruttore, a cui passo nel main listenSocket.accept() che è un oggetto di tipo ServerSocket che mediante il metodo accept()
    //crea un nuovo socket dove client e server si scambiano richieste e risposte mediante stream di dati.
    public WorkerServer(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        String requestMessageLine; //contiene la prima riga della richiesta HTTP
        String fileName = null; //contiene il nome del file nella richiesta HTTP

        /*il try scritto in questo modo con le parentesi tonde e delle risorse allocate al suo interno è un "try with RESOURCES"
        * Praticamente quello che fa è chiudere le risorse (connessioni, stream, connessioni a database per esempio)
        * quando il blocco try termina, quindi in questo caso chiude gli stream.
        * OSSERVAZIONE (importante): le variabili che rappresentano le risorse NON devono essere riutilizzate
        */
        try (BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outputStreamToClient = new DataOutputStream(connectionSocket.getOutputStream())){
            System.out.println("client connesso sul socket: " + connectionSocket);
            System.out.println("client servito dal worker: " + Thread.currentThread().getId());

            requestMessageLine = inputFromClient.readLine();
            //tokenizzazione della richiesta del client
                fileName = tokenization(requestMessageLine);
                //controllo che la richiesta sia stata fatta bene cioè che sia di tipo GET
                if(fileName.equals("Bed Request")){
                    System.err.println("Bed Request");
                    System.exit(1);
                }
                //per togliere il warning: "fileName could be null"
                assert fileName != null;
                File file = new File(fileName);
                //prendo la lunghezza del file che mi servirà dopo per mandare sul socket lo stream del file al client
                int numeroDiByte = (int) file.length();
                //questo stream mi serve per ottenere i byte del file salvato in locale
                FileInputStream inputFileFromFileSystem = new FileInputStream(fileName);
                //creo un array di byte che conterrà i byte letti da inputFileFromFileSystem
                byte[] arryDiByteDelFile = new byte[numeroDiByte];
                //Ecco, qui leggo i byte del file e li salvo all'interno del vettore arryDiByteDelFile
                inputFileFromFileSystem.read(arryDiByteDelFile);
                inputFileFromFileSystem.close(); //chiudo lo stream da cui leggo i byte del file che poi manderò al client

                /*                                          FINITA LA GESTIONE DELLA RICHIESTA DEL CLIENT
                 *                                           ORA COMICIO LA GESTIONE DELLA RISPOSTA DEL SERVER
                 *                                           CHE MANDERA' AL CLIENT*/

                costruzioneRisposta(fileName, outputStreamToClient);

                //quenste due linee di codice invece mandano la lunghezza del contenuto che mandiamo al client e deve precedere
                //l'invio del file
                outputStreamToClient.writeBytes("Content-Lenght: " + numeroDiByte + "\r\n");
                outputStreamToClient.writeBytes("\r\n");

                //invio effettivo del file
                outputStreamToClient.write(arryDiByteDelFile, 0,  numeroDiByte); //[***qui](mi riferisco al commento sopra costruzioneRisposta)

                //chiusura della connesione tra client e server
                connectionSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //metodo per costruire la risposta che il server manda al client
    //per ogni file sopportato viene mandato il messaggio content-type con il contenuto specificato
    //il vero e proprio invio del file al client, sul socket "connectionSocket" mediante lo stream outputStreamToClient avviene nel
    //metodo run [cioè sopra ***]
    private void costruzioneRisposta(String fileName, DataOutputStream outputStreamToClient) throws IOException {
        //questo comando è OBBLIGATORIO. Questo comando prepara l'header della risposta HTTP
        // (penso che debba essere scritto così per forza) e manda la status line, cioè il fatto che
        //l'azione è stata ricevuta con successo compresa ed accettata (questo me lo dice il codice 200 che significa "OK" ed è
        //la risposta standard per le richieste HTTP andate a buon fine).

        outputStreamToClient.writeBytes("HTTP/1.1 200 Document Follows\r\n");

        if (fileName.endsWith(".jpg")) //file jpg (ne ho uno in cartella MediaFile)
            outputStreamToClient.writeBytes("Content-Type: image/jpg\r\n");

        if (fileName.endsWith(".gif")) //file gif
            outputStreamToClient.writeBytes("Content-Type: image/gif\r\n");

        if (fileName.endsWith(".txt")) //file di testo (ne ho uno in cartella MediaFile)
            outputStreamToClient.writeBytes("Content-Type: text/txt\r\n");

        if (fileName.endsWith(".mp3")) //file audio mp3 (ne ho uno in cartella MediaFile)
            outputStreamToClient.writeBytes("Content-Type: audio/mpeg\r\n");

        if (fileName.endsWith(".mp4"))//file video mp4
            outputStreamToClient.writeBytes("Content-Type: video/mp4\r\n");

        if (fileName.endsWith(".pdf")) //documenti pdf (ne ho uno in cartella MediaFile)
            outputStreamToClient.writeBytes("Content-Type: doc/pdf\r\n");

    }

    //metodo per fare la tokenizzazione della richiesta del client
    private String tokenization(String requestMessageLine) {
        //tokenizzazione della richiesta del client
        StringTokenizer tokenizer = new StringTokenizer(requestMessageLine);
        //guardo che la richiesta sia di tipo GET il metodo .nextToken() ritorna il token successivo
        // (quindi partendo da zero ritorna la scritta GET)
        String fileName;
        if (tokenizer.nextToken().equals("GET")) {
            //prendo il nome del file chiamando ancora .nextToken() che passa al token successivo alla scritta GET quindi
            // ritorna il nome del file
            fileName = tokenizer.nextToken();
            if (fileName.startsWith("/") == true)
                //questa linea di codice serve per eliminare dal fileName il carattere "/" solo se il nome del file comincia con esso.
                //esempio: /evento.jpg diventa --> evento.jpg
                fileName = fileName.substring(1);

        }
        else {
            return "Bed Request";
        }
        return fileName;
    }

    //metodo main.
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

        while (true) {
            /*avvio un thread pool fatto da WorkerServer che si occuperanno di servire i client connessi
             * ho deciso di usare un thread pool in questo modo posso avere più connessioni da più client al server
             * ogni thread invierà il file richiesto. i file disponibili si trovano nella cartella MediaFile*/
            threadPool.execute(new WorkerServer(listenSocket.accept()));

        }
    }


}
