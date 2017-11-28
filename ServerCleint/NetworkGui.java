package ServerCleint;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import Network.Network;
import Network.State;
import Network.Node;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class NetworkGui extends Application implements Observer {
    int SIZE = 20;

    final Paint INFECTED_COLOR = Color.GREEN;
    final Paint RESISTANT_COLOR = Color.BLUE;
    final Paint SUSEPTABLE_COLOR = Color.RED;
    final Paint IMMUNE_COLOR = Color.PURPLE;
    final Paint DEAD_COLOR = Color.BLACK;
    /**
     * Connection to network interface to server
     */
    private NetworkServer serverConn;


    Canvas canvas;
    /**
     * Where the command line parameters will be stored once the application
     * is launched.
     */
    private Map< String, String > params = null;

    private int width;
    private int height;


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
            Network model = new Network();
            serverConn = new NetworkServer(Integer.parseInt(getParamNamed("port")), model);
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

            model.addObserver(this);

        }catch (Exception e){
            e.printStackTrace();
            serverConn.error(e.getMessage());
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

    @Override
    public void update(Observable o, Object arg){

        if (o instanceof Network){
            System.out.println(((Network) o).isActive());
            if (!((Network) o).isActive()){
            }
            this.canvas.setHeight(((Network) o).getHeight());
            this.canvas.setWidth(((Network) o).getWidth());

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight());


            for (Node n: ((Network) o).getNodes().values()){
//                System.out.println(n);
//                System.out.println((n.getX() - SIZE / 2) + "  " +( n.getY() - SIZE / 2 )+ "  " +  SIZE + "  " +  SIZE);
                setFillColor(n.getState(), gc);
                gc.fillOval(n.getX() - SIZE / 2, n.getY() - SIZE / 2, SIZE, SIZE);
            }
            if (((Network) o).isConnections()) {
                gc.setFill(Color.HONEYDEW);
                for (Node n : ((Network) o).getNodes().values()) {
                    for (Node n2 : n.getNodes().values()) {
                        gc.strokeLine(n.getX(), n.getY(), n2.getX(), n2.getY());
                        gc.fillOval(n2.getX() - SIZE / 10 / 2, n2.getY() - SIZE / 10 / 2, SIZE / 10, SIZE / 10);
                    }

                }
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
    public void stop() throws Exception {
        super.stop();
    }
}
