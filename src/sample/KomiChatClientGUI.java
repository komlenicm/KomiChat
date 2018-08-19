package sample;

import javafx.scene.paint.Color;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class KomiChatClientGUI extends Application {

    public static KomiChatClientLogic chatClient;
    public static String username = "Anonymous";
    public static Stage mainWindow = new Stage();
    private static Button btnAbout = new Button("ABOUT");
    private static Button btnConnect = new Button("CONNECT");
    private static Button btnDisconnect = new Button("DISCONNECT");
    private static Button btnHelp = new Button("HELP");
    private static Button btnSend = new Button("SEND MESSAGE");
    private static Label lbMessage = new Label("Message: ");
    public static TextField tfMessage = new TextField();
    private static Label lbConversation = new Label("Conversation");
    public static TextArea taConversation = new TextArea();
    private static ScrollPane spConversation = new ScrollPane();
    private static Label lbOnline = new Label("Currently Online");
    public static ListView<String> listOnline = new ListView<String>();
    private static ScrollPane spOnline = new ScrollPane();
    private static Label lbLoggedInAs = new Label("Logged In As");
    private static Label lbLoggedInAsBox = new Label();

    public static Stage loginWindow = new Stage();
    public static TextField tfUsernameBox = new TextField();
    private static Button btnEnter = new Button("ENTER");
    private static Label lbEnterUsername = new Label("Enter username: ");
    private static Stage stageLogIn = new Stage();
    private static GridPane gp = new GridPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Insets ins = new Insets(10, 10, 10, 10);
        BorderPane border = new BorderPane();
        HBox hboxT = new HBox(15);
        HBox hboxB = new HBox(15);
        hboxT.setPadding(ins);
        btnDisconnect.setDisable(true);
        lbLoggedInAsBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null)));
        hboxT.getChildren().addAll(btnConnect,btnDisconnect,btnAbout,btnHelp, lbLoggedInAs,lbLoggedInAsBox);
        border.setTop(hboxT);

        hboxB.setPadding(ins);
        tfMessage.setPrefWidth(290);
        hboxB.getChildren().addAll(lbMessage,tfMessage,btnSend);
        border.setBottom(hboxB);

        VBox vboxR = new VBox(15);
        vboxR.setPadding(ins);
        listOnline.setPrefSize(100, 200);
        spOnline.setPrefSize(100, 200);
        spOnline.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spOnline.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        spOnline.setContent(listOnline);
        vboxR.getChildren().addAll(lbOnline, spOnline);
        border.setRight(vboxR);

        VBox vboxC = new VBox(15);
        vboxC.setPadding(ins);

        taConversation.setPrefColumnCount(20);
        taConversation.setPrefRowCount(5);
        taConversation.setEditable(false);
        taConversation.wrapTextProperty();
        taConversation.setPrefHeight(300);
        taConversation.setPrefWidth(345);

        spConversation.setPrefHeight(300);
        spConversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spConversation.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        spConversation.setContent(taConversation);
        vboxC.getChildren().addAll(lbConversation,spConversation);
        border.setCenter(vboxC);


        btnConnect.setOnAction((ActionEvent event) -> {

            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.TOP_CENTER);
            vbox.setPadding(new Insets(10, 10, 10, 10));

            vbox.getChildren().addAll(lbEnterUsername,tfUsernameBox,btnEnter);

            loginWindow.setTitle("Enter your name");
            loginWindow.setScene(new Scene(vbox, 300, 100));
            loginWindow.show();


        });

        btnEnter.setOnAction((ActionEvent event) -> {
            btnActionEnter(primaryStage);
        });


        btnSend.setOnAction((ActionEvent event) -> {

            btnActionSend();

        });


        btnSend.setOnKeyPressed(
                event -> {
                    if(event.getCode() == KeyCode.ENTER){

                        btnActionSend();
                    }

                }

        );

        tfMessage.setOnKeyPressed(event -> enterKeyPressedToSendMessage(event.getCode()));
        tfUsernameBox.setOnKeyPressed(event -> enterKeyPressedToConfirmUsername(event.getCode(), primaryStage));


        btnDisconnect.setOnAction((ActionEvent event) -> {

            btnActionDisconnect();
        });


        btnAbout.setOnAction((ActionEvent event) -> {

            btnActionHelp();
        });

        btnHelp.setOnAction((ActionEvent event) -> {

            btnActionHelp();
        });


        Scene scene = new Scene(border, 500, 320);

        primaryStage.setTitle(username + "'s Chat Box");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static void connect(){

        try{
            final int PORT = 444;
            final String HOST = "localhost";
            Socket socket = new Socket(HOST, PORT);
            System.out.println("You connected to: " + HOST);

            chatClient = new KomiChatClientLogic(socket);

            //dodajemo na listu Online korisnika
            PrintWriter printWriterOut = new PrintWriter(socket.getOutputStream());
            printWriterOut.println(username);
            printWriterOut.flush();

            Thread X = new Thread(chatClient);
            X.start();
        }
        catch(Exception e){
            System.out.print(e);

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Server not responding.");

            alert.showAndWait();
            System.exit(0);
        }
    }

    public static void btnActionEnter(Stage primaryStage){
        if(!tfUsernameBox.getText().equals("")){
            username = tfUsernameBox.getText().trim();
            lbLoggedInAsBox.setText(username);
            KomiChatServer.currentUsers.add(username);
            btnSend.setDisable(false);
            btnDisconnect.setDisable(false);
            btnConnect.setDisable(true);
            connect();
            primaryStage.setTitle(username + "'s Chat Box");
            loginWindow.hide();
        }
        else{

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a name!");
            alert.showAndWait();
        }
    }

    public static void btnActionSend(){

        if(!tfMessage.getText().equals("")){

            chatClient.send(tfMessage.getText());
            tfMessage.requestFocus();

        }
    }

    public static void btnActionDisconnect(){
        try{

            chatClient.disconnect();

        }
        catch(Exception e){

            e.printStackTrace();
        }
    }

    public static void btnActionHelp(){

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("This is student project.");

        alert.showAndWait();
    }

    public void enterKeyPressedToSendMessage(KeyCode keyCode){
        if(keyCode == KeyCode.ENTER){
            btnActionSend();
        }
    }
    public void enterKeyPressedToConfirmUsername(KeyCode keyCode,Stage stage){
        if(keyCode == KeyCode.ENTER){
            btnActionEnter(stage);
        }
    }

}
