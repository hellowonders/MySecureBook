package com.qdt.blockchain.fx.controller;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXDialogLayout;
import com.qdt.blockchain.fx.MainApp;
import com.qdt.blockchain.fx.MasterConfigFileHandler;
import com.qdt.blockchain.fx.progress.RingProgressIndicator;
import com.qdt.blockchain.support.utils.LicenseUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {
	private final static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@FXML
	private RingProgressIndicator ringProgress;

	@FXML
	private Button newProject;

	@FXML
	private Button openProject;
	
	@FXML
	private Button openHelp;

	public void initialize() throws NoSuchAlgorithmException {
		long remDays = LicenseUtil.getLicenseRemainingDays("91280-0A819-C291-34C40");
		ringProgress.setProgress(Double.valueOf(remDays).intValue());
		ringProgress.setTooltip(new Tooltip("Remaining Days"));
	}

	@FXML
	private void createNewProject(ActionEvent event) {
//		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Categories.fxml"));
		try {
			Parent parent = loader.load();
			CategoryController controller = loader.<CategoryController>getController();
			controller.setType("CREATE_NEW");
			MainApp.setScene(primaryStage, parent, 1000, 680);
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	@FXML
	private void openExistingProject(ActionEvent event) {
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Categories.fxml"));
		try {
			Parent parent = loader.load();
			CategoryController controller = loader.<CategoryController>getController();
			controller.setType("OPEN_EXISTING");
			MainApp.setScene(primaryStage, parent, 1000, 680);
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	@FXML
	private void renewLicense(ActionEvent event) {
		Stage categoryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		JFXDialogLayout layout = new JFXDialogLayout();
		JFXAlert<Void> alert = new JFXAlert<>(categoryStage);

		Label label = new Label("Please enter renewal license key");
		TextField textField = new TextField();
		JFXButton confirmButton = new JFXButton("OK");
		confirmButton.setButtonType(ButtonType.RAISED);
		confirmButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
		confirmButton.setOnAction(confirmEvent -> renewLicense(textField.getText()));

		JFXButton cancelButton = new JFXButton("CANCEL");
		cancelButton.setButtonType(ButtonType.RAISED);
		cancelButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
		cancelButton.setCancelButton(true);
		cancelButton.setOnAction(closeEvent -> alert.hideWithAnimation());

		HBox hbox = new HBox(10, confirmButton, cancelButton);

		VBox popUpBox = new VBox(10, label, textField, hbox);
		layout.setBody(popUpBox);
		alert.setOverlayClose(true);
		alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
		alert.setContent(layout);
		alert.initModality(Modality.NONE);
		alert.show();
	}

	@FXML
	public void openProjectHelp(ActionEvent event) {
        WebView browser = new WebView();
        Stage helpStage = new Stage();
        helpStage.getIcons().add(new Image("/images/fingerprint.png"));
        helpStage.setTitle("Help Menu");
        Scene scene = new Scene(browser, browser.getPrefWidth(),  
        		browser.getPrefHeight()); 
        helpStage.setScene(scene);
        URL url = getClass().getResource("/readme.html");
        browser.getEngine().load(url.toExternalForm());
        helpStage.show();
	}
	
	private void renewLicense(String newLicense) {
		try {
			MasterConfigFileHandler instance = MasterConfigFileHandler.getInstance();
			String renewLicenseKey = LicenseUtil.renewLicense(instance.getUserId(), instance.getLicenseKey(), newLicense);
			instance.setLicense(instance.getUserId(), renewLicenseKey);
		} catch (NoSuchAlgorithmException | IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}
