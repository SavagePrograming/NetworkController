package JustCleint;

import Network.Network;
import Network.Protocol;
import Network.State;
import Network.Node;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ScreenGui extends Application implements Observer {
    final int SIZE = 15;
    final int WIDTH = 500;
    final int HEIGHT = 500;
    final int MEMORYLIMIT = -1;

//    private Stage main;
    final Paint INFECTED_COLOR = Color.GREEN;
    final Paint RESISTANT_COLOR = Color.BLUE;
    final Paint SUSEPTABLE_COLOR = Color.RED;
    final Paint IMMUNE_COLOR = Color.PURPLE;
    final Paint DEAD_COLOR = Color.BLACK;
    /**
     * Connection to network interface to server
     */
    private Controller controller;


    Canvas canvas;
    /**
     * Where the command line parameters will be stored once the application
     * is launched.
     */
    private Map< String, String > params = null;

    private int width;
    private int height;

    private ArrayList<String> history;
    int historyIndex;


    /**
     * Look up a named command line parameter (format "--name=value")
     * @param name the string after the "--"
     * @return the value after the "="
     */
    private String getParamNamed( String name ) {
        if ( params == null ) {
            params = super.getParameters().getNamed();
        }
        if ( !params.containsKey( name ) ) {
//            System.out.println("Nope");
            return "";
        }
        else {
            return params.get( name );
        }
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try{
            params = super.getParameters().getNamed();
            Network model = new Network();
            model.addObserver(this);
            if (getParamNamed("width") != "" && getParamNamed("height") != "" ){
                controller = new Controller(Integer.parseInt(getParamNamed("width")),
                        Integer.parseInt(getParamNamed("height")), model, params);
            }else{
                controller = new Controller(WIDTH,HEIGHT, model, params);
            }


            height =  model.getHeight();
            width = model.getWidth();
//            System.out.println(model.);



            BorderPane pane = new BorderPane();
            canvas = new Canvas(height, width);
            pane.setCenter(canvas);

            Scene scene = new Scene( pane );

            primaryStage.setWidth(width);
            primaryStage.setHeight(height);



            primaryStage.setScene(scene);
            primaryStage.setTitle( "Network Viewer" );
            primaryStage.show();

            Stage secondStage = new Stage();
            this.createSecondStage(secondStage);
            secondStage.show();


        }catch (Exception e){
            e.printStackTrace();
            controller.error(e.getMessage());
            e.printStackTrace();
        }

    }


    public void setFillColor(State state, GraphicsContext gc){
        switch (state){
            case Susceptible:
                gc.setFill(SUSEPTABLE_COLOR);
                break;
            case Infected:
                gc.setFill(INFECTED_COLOR);
                break;
            case Dead:
                gc.setFill(DEAD_COLOR);
                break;
            case Immune:
                gc.setFill(IMMUNE_COLOR);
                break;
            case Resistant:
                gc.setFill(RESISTANT_COLOR);
                break;
        }
    }

    public void createSecondStage(Stage primaryStage) {

        try {
            history = new ArrayList<>();
            historyIndex = 0;
            BorderPane pane = new BorderPane();

            TextField textField = new TextField();
            textField.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            if(controller.command(textField.getText())){
                                addToHistory(textField.getText());
                            }
                            textField.setText("");
                            historyIndex = -1;
                        }
                    });

            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().equals(KeyCode.UP)){

                        historyIndex ++;
                        textField.setText(getHistory());


                    }else if (event.getCode().equals(KeyCode.DOWN)){

                        historyIndex --;
                        textField.setText(getHistory());


                    }
//                    else if (event.getCode().equals(KeyCode.S)){
//                        System.out.println("Clicked");
//                        if (textField.getText().equals("")){
//
//                            System.out.println("Set");
//                            textField.setText("usceptible");
//                        }
//                    }
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
                controller.readFromFile(getParamNamed("read"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            controller.error(e.getMessage());
            e.printStackTrace();
        }

    }
    public String getHistory(){
        if (historyIndex < 0) {
            historyIndex = -1;
            return "";
        }else if (historyIndex >= history.size()){
            historyIndex = history.size() - 1;
            if (historyIndex >= 0) return history.get(historyIndex);
            else  return "";

        }else if (MEMORYLIMIT >= 0 && historyIndex >= MEMORYLIMIT) {
            historyIndex = MEMORYLIMIT - 1;
            if (historyIndex >= 0) return history.get(historyIndex);
            else return "";
        }else{
            return  history.get(historyIndex);
        }
    }


    public void addToHistory(String command){
        if (history.contains(command)){
            history.remove(command);
            history.add(0, command);
        }else{
            history.add(0, command);
        }
        while (MEMORYLIMIT >= 0 && MEMORYLIMIT < history.size()){
            history.remove(MEMORYLIMIT);
        }
    }

    public void updateWhenReady(Observable o, Object arg){
//        System.out.println("Updated");

        if (o instanceof  Network){
            if (!((Network) o).isActive()){
                try {
                    this.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.canvas.setHeight(((Network) o).getHeight());
            this.canvas.setWidth(((Network) o).getWidth());

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight());

            if (((Network) o).isConnections()) {
                gc.setFill(Color.HONEYDEW);
                for (Node n : ((Network) o).getNodes().values()) {
                    for (Node n2 : n.getNodes().values()) {
                        gc.strokeLine(n.getX(), n.getY(), n2.getX(), n2.getY());
                        gc.fillOval(n2.getX() - SIZE / 10 / 2, n2.getY() - SIZE / 10 / 2, SIZE / 10, SIZE / 10);
                    }

                }
            }


            for (Node n: ((Network) o).getNodes().values()){
//                System.out.println(n);
//                System.out.println((n.getX() - SIZE / 2) + "  " +( n.getY() - SIZE / 2 )+ "  " +  SIZE + "  " +  SIZE);
                setFillColor(n.getState(), gc);
                gc.fillOval(n.getX() - SIZE / 2, n.getY() - SIZE / 2, SIZE, SIZE);
            }
            if (((Network) o).isText()) {
                gc.setFill(Color.BLUE);
                for (Node n : ((Network) o).getNodes().values()) {
//                System.out.println(n);
//                System.out.println((n.getX() - SIZE / 2) + "  " +( n.getY() - SIZE / 2 )+ "  " +  SIZE + "  " +  SIZE);
                    gc.fillText(n.getName(), n.getX() - SIZE / 2, n.getY() - SIZE / 2 + SIZE / 4);
                }
            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateWhenReady(o,arg);
            }
        });
    }

}
