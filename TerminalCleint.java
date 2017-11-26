import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;

public class TerminalCleint{
    final int WIDTH = 400;
    final int HEIGHT = 400;
    Socket sock;
    Scanner networkIn;
    private PrintStream networkOut;
    final int TIME = 1000;



    public TerminalCleint(String host, int port, int height, int width){
        try {
            this.sock = new Socket(host, port);
            this.networkIn = new Scanner(sock.getInputStream());
            this.networkOut = new PrintStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initalConnect(width, height);
    }

    public TerminalCleint(String host, int port){
        try {
            this.sock = new Socket(host, port);
            this.networkIn = new Scanner(sock.getInputStream());
            this.networkOut = new PrintStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initalConnect();
    }

    public void initalConnect(){
        this.networkOut.println(Protocol.INITIAL_CONNECT + " " + WIDTH + " " + HEIGHT);
    }

    public void initalConnect(int width, int height){
        this.networkOut.println(Protocol.INITIAL_CONNECT + " " + width + " " + height);
    }

    public void send(String input){
        networkOut.println(input);
    }

    public void error(String message){
        this.networkOut.println(Protocol.ERROR + " " + message);
    }

}
