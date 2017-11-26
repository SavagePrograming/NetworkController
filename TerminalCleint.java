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

public class TerminalCleint extends Application{
    int WIDTH = 400;
    int HEIGHT = 400;
    Socket sock;
    Scanner networkIn;
    private PrintStream networkOut;
    final int TIME = 1000;
//    final int HEIGHT = 160;
//    final int WIDTH = 300;

    private Map< String, String > params = null;

    public static void main(String[] args) {
        launch(args);
    }

    private NetworkClient serverConn;

    public void initalConnect(){
        this.networkOut.println(Protocol.INITIAL_CONNECT + " " + WIDTH + " " + HEIGHT);
    }

    public void initalConnect(int width, int height){
        this.networkOut.println(Protocol.INITIAL_CONNECT + " " + width + " " + height);
    }

    /**
     * Look up a named command line parameter (format "--name=value")
     *
     * @param name the string after the "--"
     * @return the value after the "="
     */
    private String getParamNamed(String name) {
        if (params == null) {
            params = super.getParameters().getNamed();
        }
        if (!params.containsKey(name)) {
            System.out.println("Nope");
            return "";
        } else {
            return params.get(name);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
//            System.out.println(model.);
            this.sock = new Socket(getParamNamed("host"), Integer.parseInt(getParamNamed("port")));
            this.networkIn = new Scanner(sock.getInputStream());
            this.networkOut = new PrintStream(sock.getOutputStream());
            initalConnect(Integer.parseInt(getParamNamed("width")), Integer.parseInt(getParamNamed("height")));


            System.out.println("start called.");
            System.out.println("start may process the command line.");
            System.out.println("start builds [and shows] the GUI.");

            BorderPane pane = new BorderPane();

            TextField textField = new TextField();
            textField.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent e) {
                            networkOut.println(textField.getText());
                        }
                    });

            textField.setOnMouseClicked(
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            textField.setText("");
                        }
                    });

            pane.setCenter(textField);
            Scene scene = new Scene(pane);
//
//            primaryStage.setWidth(WIDTH);
//            primaryStage.setHeight(HEIGHT);


            primaryStage.setScene(scene);
            primaryStage.setTitle("Controler");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            serverConn.error(e.getMessage());
            e.printStackTrace();
        }

    }
}
