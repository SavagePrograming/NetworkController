
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Terminal extends Application {

    private Map< String, String > params = null;

    private TerminalCleint terminalCleint;

    public static void main(String[] args) {
        launch(args);
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
            if (Objects.equals(getParamNamed("width"), "") || Objects.equals(getParamNamed("height"), "")){
                terminalCleint = new TerminalCleint(getParamNamed("host"), Integer.parseInt(getParamNamed("port")));

            }else {
                terminalCleint = new TerminalCleint(getParamNamed("host"), Integer.parseInt(getParamNamed("port")),
                        Integer.parseInt(getParamNamed("width")), Integer.parseInt(getParamNamed("height")));
            }

            System.out.println("start called.");
            System.out.println("start may process the command line.");
            System.out.println("start builds [and shows] the GUI.");

            BorderPane pane = new BorderPane();

            TextField textField = new TextField();
            textField.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            terminalCleint.send(textField.getText());
                        }
                        public void handle(MouseEvent e) {
                            textField.setText("");
                        }
                    });

            pane.setCenter(textField);
            Scene scene = new Scene(pane);

//
//            primaryStage.setWidth(WIDTH);
//            primaryStage.setHeight(HEIGHT);


            primaryStage.setScene(scene);
            primaryStage.setWidth(400);
            primaryStage.setTitle("Controler");
            primaryStage.show();

            if (!getParamNamed("read").equals("")){
                terminalCleint.readFromFile(getParamNamed("read"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            terminalCleint.error(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void stop() throws Exception {
        terminalCleint.error("Quit");
        super.stop();
    }
}
