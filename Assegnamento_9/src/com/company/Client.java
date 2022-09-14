

import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Esegui come: Client <hostname> <porta>");
            System.exit(1);
        }
        // Leggo il numero di porta da riga di comando.
        String hostName = args[0];
        int porta = Integer.parseInt(args[1]);

        System.out.println("Inserisci il messaggio che vuoi inviare al server. \n");
        System.out.println("[Per fermare l'esecuzione inserisci: '-Stop!'] ");
        //creo il socketChannel da cui il client manderà messaggi verso il server con un try with resurces
        try(SocketChannel socket = SocketChannel.open(new InetSocketAddress(hostName, 5454))) {
            //oggetto della classe Scanner per leggere l'input da tastiera
            Scanner scanner = new Scanner(System.in);
            // configuro il socketChannel bloccante
            socket.configureBlocking(true);
            //alloco il buffer con 1024 byte
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            //messaggio da inviare
            String messaggioDaInviareAlServer;
            while (true) {
                //prendo l'input da tastiera
                messaggioDaInviareAlServer = scanner.nextLine();
                //Se la condizione dell'if si verifica, vuol dire che voglio smettere di mandare messaggi al server, chiudere la connessione
                //e fermare il processo client
                if (messaggioDaInviareAlServer.equalsIgnoreCase("-Stop!")){
                    mandaMessaggioAlServer(socket, messaggioDaInviareAlServer, buffer);
                    return;

                }

                //se il messaggio che mando è vuoto, faccio terminare il processo client perché non sono ammessi messaggi vuoti
                if(messaggioDaInviareAlServer.isEmpty()){
                    System.err.println("Il messaggio non può essere vuoto");
                    System.exit(1);
                }
                //mando il messaggio al server
                mandaMessaggioAlServer(socket, messaggioDaInviareAlServer, buffer);

                //preparo il buffer per la scrittura della risposta

                buffer.clear(); //pulisco il buffer

                //leggo il messaggio dal canale e lo scrivo sul buffer
                socket.read(buffer);
                //preparo il buffer per la lettura
                buffer.flip();
                //lunghezza della risposta
                int lunghezzaRisposta = buffer.getInt();
                //array di byte che conterrà i byte della risposta (presenti sul buffer)
                byte [] byteDellaRisposta = new byte[lunghezzaRisposta];
                //metto i byte dal buffer nell'array "byteDellaRisposta"
                buffer.get(byteDellaRisposta);
                //metto i byte che stanno nel buffer nella stringa "ritornoDalServer"
                String ritornoDalServer = new String(byteDellaRisposta );
                //Stampo la stringa
                System.out.println(ritornoDalServer);

            }

        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Server out of connection");
        }



    }
    //metodo statico che mi serve per mandare i messaggi al server
    public static void mandaMessaggioAlServer(SocketChannel socketChannel, String messaggio, ByteBuffer buffer) throws IOException {
        buffer.clear();
        byte[] message = messaggio.getBytes();
        //invio la lunghezza del messaggio al server
        buffer.putInt(message.length);
        //metto i byte del messaggio sul buffer
        buffer.put(message);
        //preparo il buffer per la lettura, dunque mi preparo per la scrittura sul canale
        buffer.flip();
        //finché ho byte nel buffer gli scrivo sul socket
        while (buffer.hasRemaining()){
            //leggo dal buffer e scrivo sul canale
            socketChannel.write(buffer);
        }
        buffer.clear();
    }
}
