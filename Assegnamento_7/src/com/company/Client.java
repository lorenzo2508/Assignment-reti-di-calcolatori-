


import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/* IMPLEMENTARE TOKENIZZAZIONE DEL MESSAGGIO DI RITORNO DAL SERVER, CON UNA COPIA DELLA STRINGA DI RITONRNO PERCHE TOKENIZZARE DISTUGGE LA STRINGA*/


public class Client implements Runnable{
    //VARIABILI D'ISTANZA
    protected DatagramSocket socket; //SOCKET SU CUI COMUNICANO CLIENT E SERVER
    private InetAddress indirizzo ; //INDIRIZZO
    private byte [] buffer = new byte[1024];//BUFFER PER MANDARE I DATI
    long returnTime = 0; //tempo di ritorno del pacchetto
    private final Timer timer = new Timer(returnTime);
    private int contatorePacchettiRicevuti = 10;
    private final ArrayList<Long> vettoreDiRTT = new ArrayList<>(); //vettore che tiene gli RTT
    private static int porta;
    private int packetNumber;
    private String ricevuto;



    //COSTUTTORE
    public Client(String serverName, int porta){
        try {
            indirizzo = InetAddress.getByName(serverName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //METODO RUN DEL CLIENT

    @Override
    public void run() {

        //vettore che uso per controllare se il pacchetto che mi rimanda indietro il server è un pacchetto vecchio
        //che quindi va scartato
        boolean [] vettorePacchetti = new boolean[10];
        for (int j = 0; j < 10; j++){
            vettorePacchetti[j] = false;
        }
        // Creo i 10 paccheti da mandare al server, e ciò che il server mi ritorna. (in quanto sendMessage sia invia il messaggio, sia attende
        //la risposta del server per mandare il pacchetto successivo


        for(int i = 0; i < 10; i++) {

            try {
                String message = sendMessage(i, vettorePacchetti);
                System.out.println( message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        timer.setStopTimer(true); //per boccare il timer una volta finito di mandare i pacchetti

        /*STATISTICHE ============================================================================*/
        int percentualePacchettiPersi = 100 - ((contatorePacchettiRicevuti * 100)/10); //calcolo percentuale pacchetti persi
        System.out.println("===============================================================================================================");
        System.out.println("--------PING Statistics---------");
        //stampo i pacchetti trasmessi (10), i pacchetti ricevuti e la percentuale dei pacchetti persi
        System.out.println("10 packets transmitted, " + contatorePacchettiRicevuti + " packets received," + percentualePacchettiPersi+ "% packet loss");
        vettoreDiRTT.sort(null); //arrayList che contiene i vari RTT, lo ordino in con ordinamento naturale dal più piccolo al più grande RTT
        long somma = 0;
        for (Long RTT: vettoreDiRTT) {
            somma = RTT + somma; //sommo tutti gli RTT
        }
        double media = (double) somma / vettoreDiRTT.size(); //faccio la media degli RTT
        //stampo il minimo RTT, la media degli RTT, e il massimo RTT
        System.out.println("round-trip (ms) min/avg/max = " + vettoreDiRTT.get(0)+ "/" + media +"/" + vettoreDiRTT.get(vettoreDiRTT.size()-1));
        System.out.println("");
        System.out.println("");
        socket.close();

    }


//INVIO DEL MESSAGGIO AL SERVER / ASPETTO DI RICEVERE INDIETRO IL PACCHETTO DAL SERVER PRIMA DI MANDARNE UN ALTRO
    public String sendMessage (int i, boolean[] vettoreDiPacchetti) throws IOException {
        //INVIO I MESSAGGI AL SERVER
        DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, indirizzo, 4445);

        String messaggio = generateMessage(i); //creo il messaggio formattato come richiesto
        buffer = messaggio.getBytes(StandardCharsets.UTF_8);//trasformo il messaggio in una sequenza di byte che poi manderò al server
        pacchetto = new DatagramPacket(buffer, buffer.length, indirizzo, porta);//creo il pacchetto
        long timeToAdd = System.currentTimeMillis(); //prendo il tempo attuale (l'stante a cui invio il messaggio)
        socket.send(pacchetto); //mando il pacchetto al server

        //ASPETTO PER 2 SECONDI CON METODO .setSoTimeout(2000); CHE IL SERVER MI RIMANDI INDIETRO IL PACCHETTO CHE HO
        // INVIATO PRIMA DI MANDARNE UN ALTRO.
        //
        //SE IL PACCHETTO NON TORNA RITORNO UN MESSAGGIO DI DEFAULT CHE E' "messaggio + " RTT: " + "*" "

        Thread t = new Thread(timer);//thread che scandisce il tempo di quanto ci mette il messaggio a tornare dal server
        t.start(); //parte il cronometro
        //stringa che ritorno se non ricevo dal server il pacchetto indietro (causa time out o pacchetto perso)
        String messaggioServerNonRicevuto = messaggio + " RTT: " + "*";
        try {
            boolean newPacket = false; //per far terminare il ciclo while quando mi arriva un nuovo pacchetto.

            /*Questo ciclo mi serve perché in caso arrivassero pacchetti con una latenza maggiore di 2 secondi otterrei due PING per lo stesso
            * pacchetto, allora il codice sottostante è stato implementato nel seguente modo:
            * usando un array di booleani, inizialmente inizializzato tutto a false, tengo traccia dei pacchetti arrivati e di quelli a cui è scaduto
            * il timeout mettendo a true l'indice dell'array corrispondente al pacchetto. Ossia, il messaggio mandato dal Server sarà di tipo
            * "PING <numero del pacchetto> <contenuto del pacchetto>" facendo il parsing di questa stringa prendo il numero del pacchetto e vado a
            * controllare se nell'array di booleani, alla posizione <numero del pacchetto>, risulta esserci il valore false. Se c'è il valore false
            * allora il pacchetto non l'ho mai ricevuto prima quindi non va scartato; conseguentemente stampo il PING.
            * Se il valore nell'array di booleani, alla posizione <numero del pacchetto>, risulta essere true allora è un pacchetto la cui latenza ha
            * superato i 2 secondi, oppure un pacchetto che avevo già ricevuto in precedenza e dunque, va scartato il pacchetto.
            * Il while mi serve perché in caso arrivi un pacchetto con timeOut scaduto, devo scartare tale pacchetto e rimettermi in ascolto per il
            * pacchetto che realmente stavo aspettando.  */
            while (!newPacket) {


                socket.setSoTimeout(2000);//uso questo metodo per dire quanto sto bloccato sulla receive.
                // setta un time out di 2 secondi sulla receive, se il timeOut finisce lancia l'eccezione
                //SocketTimeoutException che catturo sotto nel blocco catch


                pacchetto = new DatagramPacket(buffer, buffer.length); //nuovo pacchetto che è quello che mi torna dal server
                socket.receive(pacchetto);//aspetto che il server mi mandi il pacchetto e poi lo prendo dal socket e lo metto in "pacchetto"



                t.interrupt(); //interrompo il timer per vedere quanto tempo ci ha messo il server per mandarmi il pacchetto indietro
                //sottraggo al tempo impiegato dal server per mandarmi il pacchetto indietro, l'istante iniziale in cui il client ha mandato il
                //messaggio al server in questo modo calcolo il Round Trip time "RTT"
                returnTime = timer.getReturnTime() + timeToAdd; //RTT, istante di tempo in cui ho inviato il messaggio (timeToAdd) + timer.getReturnTime()
                // quanto ci mette il server a rimandare il pacchetto (timer.getReturnTime())

                ricevuto = new String(pacchetto.getData(), 0, pacchetto.getLength()); //pacchetto ricevuto dal server
                String tmp = ricevuto; //copio il pacchetto ricevuto in tmp per fare il parsing e tirare fuori il numero di pacchetto
                packetNumber = tockenizzatore(tmp); //numero di pacchetto: "PING <numero>" il valore di packetNumber è <numero>
                //pacchetto gia ricevuto
                if (vettoreDiPacchetti[packetNumber]) { //vettore che uso per controllare se il pacchetto che mi rimanda
                                                        // indietro il server è un pacchetto vecchio
                                                        //che quindi va scartato
                    continue;


                }
                else {
                    //pacchetto nuovo
                    vettoreDiPacchetti[packetNumber] = true;
                    newPacket = true;
                }



            }

            vettoreDiRTT.add(returnTime);
            return ricevuto + " RTT:" + returnTime + " ms"; //ritorno il pacchetto che mi ha rimandato indietro il server
            // (che è lo stesso che ho mandato)


        }

        //pacchetto mandato in ritardo

        catch (Exception e) { //se catturo l'eccezione vuol dire che il messaggio non è arrivato per tempo (2 secondi), scatta timeOut
                //eccezione catturata allora stampo messaggio non ricevuto perché scaduto timeOut d'ascolto sul socket
                //da parte del client
                messaggioServerNonRicevuto = messaggio + " RTT: " + "*";
                contatorePacchettiRicevuti --;
                vettoreDiPacchetti[i] = true;
                //System.out.println("pacchetto numero: " + i +" "+ vettoreDiPacchetti[i]);
                return messaggioServerNonRicevuto;

        }



    }



    /*METODO PER FARE LA TOKENIZZAZIONE DEL PACCHETTO CHE RICEVO DAL SERVER PER ESTRAPOLARE IL NUMERO DI PACCHETTO*/

    private int tockenizzatore (String tmp){
        StringTokenizer defaultTokenizer = new StringTokenizer(tmp);
        String numeroDiPacchetto = null;
        while (defaultTokenizer.hasMoreTokens())
        {

            numeroDiPacchetto = defaultTokenizer.nextToken();
            if(numeroDiPacchetto.equals("0")) break;
            if(numeroDiPacchetto.equals("1")) break;
            if(numeroDiPacchetto.equals("2")) break;
            if(numeroDiPacchetto.equals("3")) break;
            if(numeroDiPacchetto.equals("4")) break;
            if(numeroDiPacchetto.equals("5")) break;
            if(numeroDiPacchetto.equals("6")) break;
            if(numeroDiPacchetto.equals("7")) break;
            if(numeroDiPacchetto.equals("8")) break;
            if(numeroDiPacchetto.equals("9")) break;

        }

        return Integer.parseInt(numeroDiPacchetto); //RITORNO IL NUMERO DI PACCHETTO


    }

    //METODO PER GENERARE IL MESSAGGIO CON IL TIMESTAMP E CON IL NUMERO DI SEQUENZA DELLA RICHIESTA (RICHIESTA NUMERO: 1, 2 ECC...)
    public String generateMessage(Integer numeroDiSequenza){
        Long timestamp = System.currentTimeMillis();
        String messaggio = "PING: " + numeroDiSequenza.toString() + " " +  timestamp.toString();
        return messaggio;

    }
    //METODO PER CHIUDERE IL SOCKET
    public void close (){
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Esegui come: Client <indirizzo> <porta>");
            System.exit(1);
        }
        String nome = args[0];
        porta = Integer.parseInt(args[1]);

        //inizializzo il client
        Client client = new Client(nome ,porta);
        Thread threadClient = new Thread(client);

        threadClient.start();

        //join del client e chiusura del socket lato client
        try {
            threadClient.join();
            client.close();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


    }



}




