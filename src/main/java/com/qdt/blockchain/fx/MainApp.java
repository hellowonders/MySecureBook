package com.qdt.blockchain.fx;

import java.io.IOException;

import com.jfoenix.controls.JFXDecorator;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;  

public class MainApp extends Application{  
	private int windowWidth = 1000;
	private int windowHeight = 680;
	    
    @Override  
    public void start(Stage primaryStage) throws Exception {  
	
    	FXMLLoader loader;
    	if (MasterConfigFileHandler.getInstance().checkLicense()) {
    		loader = new FXMLLoader(getClass().getResource("/fxml/MainArea.fxml"));
    	} else {
        	loader = new FXMLLoader(getClass().getResource("/fxml/License.fxml"));
        	windowWidth = 350;
        	windowHeight = 200;
    	}
    	Parent parent = loader.load();
    	setScene(primaryStage, parent, windowWidth, windowHeight);  
        
    }
	public static void setScene(Stage stage, Parent parent, int windowWidth, int windowHeight) throws IOException {
   		JFXDecorator decorator = new JFXDecorator(stage, parent);
   		decorator.setCustomMaximize(false);
   		Scene scene = new Scene(decorator, windowWidth, windowHeight);
   		final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(MainApp.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
        		MainApp.class.getResource("/css/jfoenix-design.css").toExternalForm(),
        		MainApp.class.getResource("/css/jfoenix-main.css").toExternalForm(),
        		MainApp.class.getResource("/css/circleprogress.css").toExternalForm());
        stage.setTitle("MySecureBook (Personal Edition)");  
        stage.getIcons().add(new Image("/images/fingerprint.png"));
        stage.setScene(scene);  
        stage.show();
	}  
    public static void main (String[] args)  
    {  
        launch(args);  
    }  
  
}   
