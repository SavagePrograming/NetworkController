import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class NetworkGui extends Application implements Observer {
    int SIZE = 50;
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
            System.out.println("Nope");
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



    @Override
    public void update(Observable o, Object arg) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        if (o instanceof  Network){
            gc.setFill(Color.RED);
            for (Node n: ((Network) o).getNodes().values()){
                System.out.println(n);
                System.out.println((n.getX() - SIZE / 2) + "  " +( n.getY() - SIZE / 2 )+ "  " +  SIZE + "  " +  SIZE);
                gc.fillOval(n.getX() - SIZE / 2, n.getY() - SIZE / 2, SIZE, SIZE);
//                for (Node n2: n.getNodes().values()){
//                    gc.strokeLine(n.getX(), n.getY(), n2.getX(), n2.getY());
//                }
            }
        }

    }
}
