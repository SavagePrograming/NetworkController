
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

public class Terminal extends Application {

        private Map< String, String > params = null;

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
        public void start(Stage primaryStage) {
            try {
                Network model = new Network();
                serverConn = new NetworkClient(getParamNamed("host"), Integer.parseInt(getParamNamed("port")), 500, 500);
    //            System.out.println(model.);

    }
