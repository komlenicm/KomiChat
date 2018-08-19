package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class KomiChatClientLogic implements Runnable {

    Socket socket;
    Scanner inputScanner;
    Scanner sendScanner = new Scanner(System.in);
    PrintWriter printWriterOut;

    public KomiChatClientLogic(Socket X) {
        this.socket = X;
    }

    @Override
    public void run() {
        try{
            try{

                inputScanner = new Scanner(socket.getInputStream());
                printWriterOut = new PrintWriter(socket.getOutputStream());
                printWriterOut.flush();
                checkStream();
            }catch (Exception e){
                System.out.println(e);
            }

        }
        catch(Exception e){

            System.out.println(e);
        }
    }


    public void disconnect() throws IOException{
        printWriterOut.println(KomiChatClientGUI.username + " has disconnected.");
        printWriterOut.flush();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("You've been disconnected!");
        alert.showAndWait();
        System.exit(0);

    }

    public void checkStream(){

        while(true){

            receiveMessages();
        }
    }


    public void receiveMessages(){
        if(inputScanner.hasNext()){

            String MESSAGE = inputScanner.nextLine();

            if(MESSAGE.contains("#?!")){

                String tmp1 = MESSAGE.substring(3);
                tmp1 = tmp1.replace("[", "");
                tmp1 = tmp1.replace("]", "");

                String[] currentUsers = tmp1.split(", ");
                ObservableList<String> currUsers = FXCollections.observableArrayList(currentUsers);
                KomiChatClientGUI.listOnline.setItems(currUsers);
            }
            else{

                KomiChatClientGUI.taConversation.appendText(MESSAGE + "\n");
            }
        }


    }

    public void send(String msg){

        printWriterOut.println(KomiChatClientGUI.username + ": " + msg);
        printWriterOut.flush();
        KomiChatClientGUI.tfMessage.setText("");
    }


}
