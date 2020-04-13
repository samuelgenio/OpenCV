package br.com.samuka.opencv.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.opencv.core.Core;

import br.com.samuka.opencv.controller.MainController;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

    public static Parent root;
    public static FXMLLoader loader;
    public static MainController controller;

    @Override
    public void start(Stage primaryStage) {
        try {

            loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

            root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/assets/css/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            primaryStage.show();

            controller = (MainController) loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*System.out.printf("java.library.path: %s%n",
        System.getProperty("java.library.path"));
        System.loadLibrary("opencv_java342");*/
        launch(args);
    }
}
