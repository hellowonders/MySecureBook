package com.qdt.blockchain.fx.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXDialogLayout;
import com.qdt.blockchain.fx.InternalConfigFileHandler;
import com.qdt.blockchain.fx.MainApp;
import com.qdt.blockchain.fx.MasterConfigFileHandler;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoryController {
	private final static Logger LOGGER = LoggerFactory.getLogger(CategoryController.class.getName());
	
	@FXML
	private String type;
	public String getType() {return type;}
	public void setType(String type) {
		this.type = type;
	}

	@FXML
	private void handleCategoryClick(ActionEvent event) {
		Node node = (Node) event.getSource() ;
	    String data = (String) node.getUserData();
	    if (getType().equalsIgnoreCase("CREATE_NEW"))
	    	getProjectName(event, data);
	    else
	    	changeScene(null, data, event, null);
	}

	private void getProjectName(ActionEvent event, String data) {
		Stage categoryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    JFXDialogLayout layout = new JFXDialogLayout();
	    JFXAlert<Void> alert = new JFXAlert<>(categoryStage);
	    
	    Label label=new Label("Please enter project name");
	    TextField textField = new TextField();
        textField.setPromptText("Enter text");

        JFXButton okButton = new JFXButton("CREATE");
        okButton.setButtonType(ButtonType.RAISED);
        okButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
        okButton.disableProperty().bind(Bindings.isEmpty(textField.textProperty()));
        okButton.setOnAction(addEvent -> {
			changeScene(textField.getText(), data, event, label);
    	});
        
        JFXButton cancelButton = new JFXButton("CANCEL");
        cancelButton.setButtonType(ButtonType.RAISED);
        cancelButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
    	cancelButton.setCancelButton(true);
    	cancelButton.setOnAction(closeEvent -> alert.hideWithAnimation());
    	
    	HBox hbox = new HBox(10, okButton, cancelButton);
    	hbox.setAlignment(Pos.CENTER);
        VBox popUpBox = new VBox(10, label, textField);
        popUpBox.setAlignment(Pos.CENTER_LEFT);
        popUpBox.setPadding(new Insets(24));
	    popUpBox.getChildren().add(hbox);
        layout.setBody(popUpBox);
        
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
	    alert.show();
	}

	private void changeScene(String projectName, String data, ActionEvent event, Label label) {
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Stage primaryStage = new Stage();
		FXMLLoader loader = null;
		Parent parent = null;
		try {
			String absolutePath = MasterConfigFileHandler.getInstance().getWokspaceDir() + data + "\\" + projectName;
			if (getType().equalsIgnoreCase("CREATE_NEW")) {
				if (Files.exists(Paths.get(absolutePath))) {
					label.setText("Project exists, enter new name.");

				} else {
					currentStage.close();
					InternalConfigFileHandler.createConfigFile(data + "\\" + projectName);
					loader = new FXMLLoader(getClass().getResource("/fxml/WorkArea.fxml"));
					parent = loader.load();
					WorkAreaController controller = loader.<WorkAreaController>getController();
					controller.setCategory(data);
					controller.setProjectName(projectName);
					controller.setNewProject(true);
					controller.loadChain();
					MainApp.setScene(primaryStage, parent, 1000, 680);
				}
			} else if (getType().equalsIgnoreCase("OPEN_EXISTING")) {
				loader = new FXMLLoader(getClass().getResource("/fxml/ProjectMenu.fxml"));
				parent = loader.load();
				ProjectMenuController controller = loader.<ProjectMenuController>getController();
				controller.setCategory(data);
				controller.populateList();
				currentStage.close();
				MainApp.setScene(primaryStage, parent, 1000, 680);
			}
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}
