package ipinfo;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("ipinfo.fxml"));
        root.styleProperty().bind(Bindings.format("-fx-font-size: 10pt; -fx-font-family: Arial,Helvetica,sans-serif;"));
        primaryStage.setTitle("IP info getter");
        primaryStage.setScene(new Scene(root, 650, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
