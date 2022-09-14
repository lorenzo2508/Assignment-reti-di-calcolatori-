

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static DatagramSocket socket = null; //Socket
    private static int bufferDimension = 32; //dimensione del buffer
    protected static int porta;

    public static int getPorta() {
        return porta;
    }

    public static void main(String[] args) {
        System.out.println("inserisci la porta: ");
        Scanner scanner = new Scanner(System.in);
        porta = scanner.nextInt();
        System.out.println("inserisci il seed:");
        long seed = scanner.nextLong();
        Random random = new Random(seed);
        ExecutorService threadPoll = Executors.newCachedThreadPool();
        try{
            socket = new DatagramSocket(porta);
            System.out.println("Server is running on port: "+ porta);
            while (true){
                 byte [] buffer = new byte[bufferDimension];
                DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacchetto);
                long seedForThreads = random.nextLong();
                threadPoll.execute(new WorkerServer(socket, pacchetto, seedForThreads));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
                System.err.println("errore in chiusura del socket");
            }
            threadPoll.shutdown();
            if(!threadPoll.isTerminated())
                threadPoll.shutdownNow();

        }
    }


}



