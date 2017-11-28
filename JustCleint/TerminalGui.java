//package JustCleint;
//
//import javafx.application.Application;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.Scene;
//import javafx.scene.control.TextField;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.BorderPane;
//import javafx.stage.Stage;
//
//import java.util.Map;
//import java.util.Objects;
//
//public class TerminalGui extends Application {
//
//    private Map< String, String > params = null;
//
//    private Controller controller;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    public void setController(Controller controller) {
//        this.controller = controller;
//    }
//
//    public void setParams(Map<String, String> params) {
//        this.params = params;
//    }
//
//    /**
//     * Look up a named command line parameter (format "--name=value")
//     *
//     * @param name the string after the "--"
//     * @return the value after the "="
//     */
//    private String getParamNamed(String name) {
//        if (params == null) {
//            System.out.println("Nope");
//            return "";
//        }
//        if (!params.containsKey(name)) {
//            System.out.println("Nope");
//            return "";
//        } else {
//            return params.get(name);
//        }
//    }
//
//
//
//
//    @Override
//    public void stop() throws Exception {
//        controller.error("Quit");
//        super.stop();
//    }
//}
