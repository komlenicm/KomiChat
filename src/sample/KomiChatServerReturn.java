package sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class KomiChatServerReturn implements Runnable{

    public Socket socket;
    private Scanner inputScanner;
    private PrintWriter printWriterOut;
    public String message = "";

    public KomiChatServerReturn(Socket socket)
    {
        this.socket = socket;
    }

    public void checkConnection() throws IOException {

        if(!socket.isConnected()) {


            for(int i = 0; i < KomiChatServer.connectionArray.size(); i++) {
                Socket tnmpSocket = KomiChatServer.connectionArray.get(i);
                PrintWriter tmpOut = new PrintWriter(tnmpSocket.getOutputStream());
                tmpOut.println(tnmpSocket.getLocalAddress().getHostName()+" disconnected!");
                tmpOut.flush();

                //Stampanje poruke u konzoli servera (za admine)
                System.out.println(tnmpSocket.getLocalAddress().getHostName()+" disconnected!");
            }

            for(int i = 0; i < KomiChatServer.connectionArray.size(); i++) {
                if(KomiChatServer.connectionArray.get(i) == socket) {
                    KomiChatServer.connectionArray.remove(i);
                }
            }

        }
    }

    @Override
    public void run() {
        try {
            try {
                inputScanner = new Scanner(socket.getInputStream()); //tok dolaznih podataka iz servera
                printWriterOut = new PrintWriter(socket.getOutputStream()); // tok odlaznih podataka iz servera
                while(true) {
                    checkConnection();
                    if(!inputScanner.hasNext()) {
                        return;
                    }

                    message = inputScanner.nextLine(); // uzimamo poruku sa scanner objektom i smestamo je u MESSAGE
                    System.out.println("Client said: " + message);
                    //petlja za prikaz te poruke SVIM korisnicima koji su konektovani
                    for(int i = 0; i < KomiChatServer.connectionArray.size(); i++) {
                        Socket TEMP_SOCK = (Socket) KomiChatServer.connectionArray.get(i);
                        PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
                        TEMP_OUT.println(message);
                        TEMP_OUT.flush();
                        //ispis poruke na konzoli
                        System.out.println("Sent to: " + TEMP_SOCK.getLocalAddress().getHostName());
                    }
                }
            }catch (Exception e){
                System.out.println(e);
            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
