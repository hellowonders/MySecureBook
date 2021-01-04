package com.qdt.blockchain.fx.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qdt.blockchain.fx.MainApp;
import com.qdt.blockchain.fx.MasterConfigFileHandler;
import com.qdt.blockchain.support.utils.LicenseUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/** Controls the login screen */
public class LicenseController {
	private final static Logger LOGGER = LoggerFactory.getLogger(LicenseController.class);
	
	@FXML
	private Label alertLabel;
	@FXML
	private TextField user;
	@FXML
	private TextField license;
	@FXML
	private Button licenseValidateButton;

	@FXML
	private void licenseVerify(ActionEvent event) {
		try {
			if (user.getText().isEmpty()) {
				alertLabel.setText("Please enter registered email id");
				return;
			}
			if (license.getText().isEmpty()) {
				alertLabel.setText("Please enter license key");
				return;
			}
			if(!LicenseUtil.validateSerial(user.getText(), license.getText())) {
				alertLabel.setText("Provided license is invalid");
				return;
			}

			MasterConfigFileHandler.getInstance().setLicense(user.getText(), license.getText());
			Stage licenseStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			licenseStage.close();
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainArea.fxml"));
			Parent parent = loader.load();
			MainApp.setScene(primaryStage, parent, 1000, 680);
		} catch (NoSuchAlgorithmException | IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		} catch (RuntimeException e) {
			alertLabel.setText("Provided license can not be validated");
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}