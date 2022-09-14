

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Esegui come: Server <porta>");
            System.exit(1);
        }
        // Leggo il numero di porta da riga di comando.
        int porta = Integer.parseInt(args[0]);
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //faccio il bind (lego) il serverScoket all'indirizzo locale e configuro il socket per ascoltare le connessioni
            serverSocketChannel.socket().bind(new InetSocketAddress(porta));
            //configuro il channel in modalità non bloccante
            serverSocketChannel.configureBlocking(false);
            //apro il selettore
            Selector selector = Selector.open();
            //registro il channel (serverSocketChannel) con il selettore aperto sopra per accettare connessioni
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server is running on port 5454" );
            //alloco il buffer
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            //selector.select() ritorna il numero di chiavi i cui set sono stati aggiornati
            //quindi seleziona il set di chiavi dove i corrispondenti canali sono pronti per operazioni di I/O
            //se è minore o uguale a zero esco dal while
            while (selector.select() > 0 ){
                //creo un set di chiavi di questo selettore ("selector") per poterci iterare sopra con un foreach
                Set<SelectionKey> chiavi = selector.selectedKeys();
                //itero sul set
                for (SelectionKey key : chiavi  ) {
                    //se il canale di questa chiave è pronto ad accettare una nuova connessione allora =>
                    if(key.isAcceptable()){
                        // => accetto la connessione
                        SocketChannel newClientConnected = serverSocketChannel.accept();
                        //configuro il canale (socket) come non bloccante
                        newClientConnected.configureBlocking(false);
                        //lo registro in lettura
                        newClientConnected.register(selector, SelectionKey.OP_READ);
                        System.out.println("il nuovo client connesso è: " + newClientConnected.getRemoteAddress().toString());
                    }
                    //se il canale di questa chiave è in lettura allora =>
                    if(key.isReadable()){
                        //creo il canale per cui questa chiave è stata creata con il metodo key.channel
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        //salvo il messaggio ricevuto dal client in una stringa "richiestaDiEcho"
                        String richiestaDiEcho = messaggioRicevutoDalClient(socketChannel, buffer);
                        //se la richiesta è uguale a "-Stop! o -stop!" chiudo il socket e stampo quale client si è disconnesso
                        if(richiestaDiEcho.equalsIgnoreCase("-Stop!")){
                            System.out.println("client disconnesso: " + socketChannel.getRemoteAddress().toString() );
                            socketChannel.close();
                            continue;
                        }
                        //stampo lato server il messaggio inviato dal client
                        System.out.println(richiestaDiEcho);
                        //creo il messaggio che il server manda in risposta al client
                        String echoFromServer = "Echoed from Server: " + richiestaDiEcho;
                        //pulisco il buffer
                        buffer.clear();
                        //metto nel buffer i byte del messaggio da mandare al client
                        buffer.put(echoFromServer.getBytes(StandardCharsets.UTF_8));
                        //mi preparo per la scrittura sul socket
                        buffer.flip();
                        //finché ho byte nel buffer
                        while (buffer.hasRemaining()){
                            //scrivo sul socket
                            socketChannel.write(buffer);
                        }

                    }
                    //rimuovo la chiave dal set
                    chiavi.remove(key);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //METODO PER ELABORARE IL MESSAGGIO RICEVUTO DAL CLIENT
    public static String messaggioRicevutoDalClient (SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        int numeroDiByteNelBuffer = socketChannel.read(buffer); //prendo il numero di byte del messaggio del client presenti nel buffer
        if(numeroDiByteNelBuffer <= 0 ){ //se il numero di byte è <= 0 allora c'è un errore quindi esco
            System.err.println("Errore durante la lettura");
            socketChannel.close();
            System.exit(1);
        }
        //creo la stringa che contiene il messaggio mandato dal client
        String messaggioRicevutoDalClient = new String(buffer.array(), 0, numeroDiByteNelBuffer);
        //ritorno tale stringa
        return messaggioRicevutoDalClient;
    }
}
