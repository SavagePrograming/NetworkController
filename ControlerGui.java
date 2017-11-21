import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class ControlerGui extends Application {
    final int TIME = 1000;
    final int HEIGHT = 160;
    final int WIDTH = 300;

    private Map< String, String > params = null;

    public static void main(String[] args) {
        launch(args);
    }

    private NetworkClient serverConn;

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
            Network model = new Network();
            serverConn = new NetworkClient(getParamNamed("host"), Integer.parseInt(getParamNamed("port")));
//            System.out.println(model.);


            System.out.println("start called.");
            System.out.println("start may process the command line.");
            System.out.println("start builds [and shows] the GUI.");

            BorderPane pane = new BorderPane();

            pane.setTop(makeTopControls());

            pane.setBottom(makeBottomControls());

            pane.setCenter(makeCenterControls());

            Scene scene = new Scene(pane);

            primaryStage.setWidth(WIDTH);
            primaryStage.setHeight(HEIGHT);


            primaryStage.setScene(scene);
            primaryStage.setTitle("Controler");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            serverConn.error(e.getMessage());
            e.printStackTrace();
        }

    }

    public HBox makeTopControls() {
        HBox pane = new HBox();
        Button step = new Button("Step");
        Button play = new Button("Play");
        Button stop = new Button("Stop");
        Button startStop = new Button("On/Off");
        step.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.step();
                    }
                });

        play.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.start(TIME);
                    }
                });

        startStop.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.onOff(TIME);
                    }
                });

        stop.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.pause();
                    }
                });


        pane.getChildren().add(step);
        pane.getChildren().add(play);
        pane.getChildren().add(startStop);
        pane.getChildren().add(stop);

        return pane;
    }


    public HBox makeCenterControls(){
        HBox pane = new HBox();
        TextField Name1 = new TextField("Name 1");
        TextField Name2 = new TextField("Name 2");
        Button connect = new Button("Connect");

        connect.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.connect(Name1.getText(), Name2.getText());
                    }
                });

        pane.getChildren().add(Name1);
        pane.getChildren().add(Name2);
        pane.getChildren().add(connect);

        return pane;


    }
    public HBox makeBottomControls(){
        HBox pane = new HBox();
        TextField Name = new TextField("Name");
        ChoiceBox<State> Effect = new ChoiceBox<>();
        Button connect = new Button("Connect");

        connect.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.change(Effect.getValue(), Name.getText());
                    }
                });

        pane.getChildren().add(Name);
        pane.getChildren().add(Effect);
        pane.getChildren().add(connect);

        return pane;
    }



}
