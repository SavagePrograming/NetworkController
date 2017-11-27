package ServerCleint;

import javafx.application.Application;
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

import java.util.Map;

public class ControlerGui extends Application {
    final int TIME = 1000;
//    final int HEIGHT = 160;
//    final int WIDTH = 300;

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
            serverConn = new NetworkClient(getParamNamed("host"), Integer.parseInt(getParamNamed("port")), 500, 500);
//            System.out.println(model.);


            System.out.println("start called.");
            System.out.println("start may process the command line.");
            System.out.println("start builds [and shows] the GUI.");

            BorderPane pane = new BorderPane();

            VBox box = new VBox();

            box.getChildren().add(makePlayControls());

            box.getChildren().add(makeInfectionControls());

            box.getChildren().add(makeConnectControls());

            box.getChildren().add(makeCreationControls());

            box.getChildren().add(makeMoveControls());

            pane.setCenter(box);
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

    public HBox makePlayControls() {
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


    public HBox makeConnectControls(){
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

    public HBox makeInfectionControls(){
        HBox pane = new HBox();
        TextField Name = new TextField("Name");
        ChoiceBox<State> Effect = new ChoiceBox<>();
        Button change = new Button("Effect");

        change.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.change(Effect.getValue(), Name.getText());
                    }
                });

        pane.getChildren().add(Name);
        pane.getChildren().add(Effect);
        pane.getChildren().add(change);

        return pane;
    }

    public HBox makeCreationControls(){
        HBox pane = new HBox();
        TextField Name = new TextField("Name");
        Button create = new Button("Create");
        Button delete = new Button("delete");

        create.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.create(Name.getText());
                    }
                });

        delete.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.delete(Name.getText());
                    }
                });

        pane.getChildren().add(Name);
        pane.getChildren().add(create);
        pane.getChildren().add(delete);

        return pane;
    }

    public HBox makeMoveControls(){
        HBox pane = new HBox();
        TextField Name1 = new TextField("Name");
        TextField indexX = new TextField("X");
        TextField indexY = new TextField("Y");
        Button Move = new Button("Move");

         Move.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        serverConn.move(Name1.getText(), Integer.parseInt(indexX.getText()), Integer.parseInt(indexY.getText()));
                    }
                });

        pane.getChildren().add(Name1);
        pane.getChildren().add(indexX);
        pane.getChildren().add(indexY);
        pane.getChildren().add(Move);

        return pane;


    }

}
