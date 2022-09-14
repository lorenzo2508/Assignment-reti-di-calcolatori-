
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class WorkerServer implements Runnable {
    private long latenzaMassima; //numero generato in modo random che mi dice quanta latenza introdurre intervallo (0 ; 1000) milliSecondi
    //  private long perditaDiPacchetti; //numero generato in modo random che mi dice quali pacchetti non accettare intervallo (0 ; 10) pacchetti
    protected DatagramSocket socket; //SOCKET

    private DatagramPacket pacchetto;
    private Random rand;



    public WorkerServer(DatagramSocket socket, DatagramPacket pacchetto, long seed) {
        this.socket = socket; //il socket su cui client e server comunicano
        this.pacchetto= pacchetto; //prendo il pacchetto che mi passa il server
        this.rand = new Random(seed); //in questo modo, inizializzando il generatore random nel costruttore ogni thread del pool avrà
                                      //un suo generatore casuale indipendente con un proprio seme, anch'esso indipendente


    }

    @Override
    public void run() {
        latenzaMassima = 5000;
        /*====================================== CREO IL PACCHETTO DI RISPOSTA AL CLIENT ===========================================*/

        InetAddress indirizzo = pacchetto.getAddress(); //prendo l'ip del client
        int porta = pacchetto.getPort(); //prendo numero di porta che il client ha usato per mandare questo pacchetto
        String contenutoPacchetto = new String(pacchetto.getData()); //prendo il contenuto del pacchetto mandato dal client
        String risposta = creaRisposta(contenutoPacchetto); //stringa che contiene la risposta da mandare
        byte [] buffer = new byte[16];
        buffer = risposta.getBytes(StandardCharsets.UTF_8); //metto la risposta nel buffer, prendendo i byte
        DatagramPacket pacchettoDiRisposta = new DatagramPacket(buffer, buffer.length, indirizzo, porta); //creo il pacchetto di risposta da mandare al
                                                                                                         //al client
        //QUESTE 3 RIGHE DI CODICE CI SONO PERCHE' L'INDIRIZZO IP DEL CLIENT COMINCIAVA CON IL CARATTERE "/"
        String address = indirizzo.toString();
        if(address.startsWith("/")){
            address = address.substring(1);
        }

        //=========================IMPLEMENTO LA PROBABILITA' DI SCARTO DEL 25%============================

        int probabilita = rand.nextInt(100)+1; //Varibile in cui salvo un numero random tra zero e 100 e se il numero è <= 25 scarto il pacchetto
                                                    // per implementare la probabilità del 25%
        if(probabilita <= 25){ //se la probabilità è minore del 25% scarto il pacchetto
            System.out.println("Scarto il pacchetto sottostante a questa stampa");
            //stampo lato server la formattazione richiesta per far capire che non è inviata
            System.out.println( address + ":" + porta + "> " + contenutoPacchetto + " ACTION: not sent, PACKET LOST");
            return;
        }

        //=========================IMPLEMENTO LA SIMULAZIONE DELLA LATENZA DI UN INVIO DI PACCHETTI IN RETE============================

        long latenza = (long) rand.nextInt((int) latenzaMassima)+1; //genero casualmente la latenza da mettere nella sleep, il massimo generabile è 5000ms
        try {
            Thread.sleep(latenza); //dormo per "latenza" ms
        } catch (InterruptedException e) {
            //se il worker viene interrotto durante la sleep, segnalo l'interruzione e termino il thread
            System.err.println("Interruzione durante la sleep");
            e.printStackTrace();
            return;
        }

        //=======================================INVIO IL PACCHETTO DI RISPOSTA AL CLIENT=============================================

        try {
            //stampo lato server la formattazione richiesta per far capire il pacchetto è stato rimandato indietro con una certa latenza
            System.out.println(address + ":" + porta + "> " + contenutoPacchetto + " ACTION: delayed " + latenza + " ms");
            socket.send(pacchettoDiRisposta); //mando il pacchetto al client
        } catch (IOException e) {
            System.err.println("errore durante l'invio del pacchetto");
            e.printStackTrace();
        }


    }

    //METODO CHE MI SERVE PER CREARE LA RISPOSTA DA MANDARE AL CLIENT.

    private String creaRisposta(String contenutoPacchetto){
        return  contenutoPacchetto;
    }


}

