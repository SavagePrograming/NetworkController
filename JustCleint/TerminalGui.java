package JustCleint;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;

public class TerminalGui extends Application {

    private Map< String, String > params = null;

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    public void setController(Controller controller) {
        this.controller = controller;
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

    public void start(){
        launch();
    }


    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane pane = new BorderPane();

            TextField textField = new TextField();
            textField.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            controller.command(textField.getText());
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
                controller.readFromFile(getParamNamed("read"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            controller.error(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void stop() throws Exception {
        controller.error("Quit");
        super.stop();
    }
}
