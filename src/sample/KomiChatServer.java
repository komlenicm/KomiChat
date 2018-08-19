package sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class KomiChatServer {
    public static ArrayList<Socket> connectionArray = new ArrayList<Socket>();
    public static ArrayList<String> currentUsers = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        try {
            final int PORT = 444;
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Waiting for clients...");
            Socket socket = null;

            while(true) {
                socket = serverSocket.accept();
                connectionArray.add(socket);

                System.out.println("Client connected from: " + socket.getLocalAddress().getHostName());
                addUsername(socket);

                KomiChatServerReturn chat = new KomiChatServerReturn(socket); //konektovanjem novog korisnika, stvara se nova nit za njega
                Thread X = new Thread(chat);
                X.start();

            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void addUsername(Socket X) throws IOException {
        Scanner inputScanner = new Scanner(X.getInputStream());
        String username = inputScanner.nextLine();
        currentUsers.add(username);

        //prolazi kroz listu
        for(int i = 0; i < KomiChatServer.connectionArray.size(); i++) {
            Socket tmpSocket = KomiChatServer.connectionArray.get(i);
            PrintWriter printWriterOut = new PrintWriter(tmpSocket.getOutputStream());
            printWriterOut.println("#?!" + currentUsers); //prefix komanda za dodavanje u listu trenutnih korisnika u JListu (klijenata)
            printWriterOut.flush(); //koristimo radi prikaza poruka kako bi svaki korisnik video ko je online, ili ko se pridruzio chatu ili ga napustio
        }
    }
}
