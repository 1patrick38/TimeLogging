package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {
    @FXML
    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/sample/test.fxml").toURL();
//        Parent root = FXMLLoader.load(url);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = (Parent)loader.load();
        Controller controller = (Controller)loader.getController();
        primaryStage.setTitle("TimeLogging");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnHiding(event -> controller.foo());
    }


    public static void main(String[] args) {
        launch(args);
    }

}
