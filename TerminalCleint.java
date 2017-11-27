import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.animation.ScaleTransition;
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
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class TerminalCleint{
    final String READ = "Read";
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
        if (validate(input)){
            networkOut.println(input);
        }else if (input.split(" ")[0].equals(READ)){
            String path = "";
            boolean start = true;
            for (String i: input.split(" ")){
                if (start) {
                    start = false;
                }else{
                    path += i + " ";
                }

            }
            readFromFile(path.substring(0,path.length() - 1));
        }else{
            System.out.println("Invalid Command:'" + input + "'");
        }
    }

    public void error(String message){
        this.networkOut.println(Protocol.ERROR + " " + message);
    }

    boolean validate(String input){
        String command = input.split(" ")[0];
        return command.equals(Protocol.CHANGE ) || command.equals(Protocol.MOVE) || command.equals(Protocol.CLEAR) ||
                command.equals(Protocol.INITIAL_CONNECT) || command.equals(Protocol.CONNECT) ||
                command.equals(Protocol.ERROR ) || command.equals(Protocol.CREATE) || command.equals(Protocol.ZOOM) ||
                command.equals(Protocol.EXIT) || command.equals(Protocol.DELETE) || command.equals(Protocol.STEP)||
                command.equals(Protocol.START) || command.equals(Protocol.STOP) || command.equals(Protocol.ONOFF) ||
                command.equals(Protocol.TEXT) || command.equals(Protocol.CONNECTIONS) ||
                command.equals(Protocol.INFECTED) || command.equals(Protocol.DEAD) || command.equals(Protocol.IMMUNE) ||
                command.equals(Protocol.SUSCEPTIBLE) || command.equals(Protocol.RESISTANCE);


    }

    public void readFromFile(String fileName){
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            ArrayList<String> commands = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null){
                if (!(line.contains(READ) && line.contains(fileName))) commands.add(line);
            }
            for (String command:commands){
                this.send(command);
            }
        } catch (IOException e) {
            System.out.println("File is in Valid: " + fileName);

        }
    }
}
