package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controllers.LoginViewController;
import sample.controllers.PanelController;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ReflectPermission;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
//        showDBView();
        showLoginView();
//        testSecruty();

    }

    private void testSecruty() {
        ReflectPermission reflect = new ReflectPermission("*","accessDeclaredMembers");
        System.setSecurityManager(new SecurityManager());
        SecurityManager securityManager = System.getSecurityManager();
        securityManager.checkPermission(reflect);
        Field[] fields = Main.class.getDeclaredFields();
        try {
            fields[0].set(this,null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void showLoginView() throws IOException {
//        AnchorPane pane = FXMLLoader.load(getClass().getResource("fxml/LoginView.fxml"));
////        primaryStage.initStyle(StageStyle.TRANSPARENT);
//        primaryStage.setScene(new Scene(pane));
//        primaryStage.setTitle("Login");
//        primaryStage.show();
        FXMLLoader loader= new FXMLLoader(getClass().getResource("fxml/LoginView.fxml"));
        loader.load();
        LoginViewController loginViewController = (LoginViewController)loader.getController();
        loginViewController.init();
        Parent root = loader.getRoot();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void showDBView() throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("fxml/sample.fxml"));
        loader.load();
        PanelController panelContoller = (PanelController)loader.getController();
        panelContoller.init();
        Parent root = loader.getRoot();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
