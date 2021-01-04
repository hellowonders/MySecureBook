package com.qdt.blockchain.fx.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

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
import com.qdt.blockchain.support.utils.ZipUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectMenuController {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProjectMenuController.class);
	private String zipFilePath;
	private String selectedTitle;
	public String getSelectedTitle() {
		return selectedTitle;
	}
	public void setSelectedTitle(String selectedTitle) {
		this.selectedTitle = selectedTitle;
	}
	public String getZipFilePath() {
		return zipFilePath;
	}
	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}

	@FXML
	private Label category;
	public String getCategory() {
		return category.getText();
	}
	public void setCategory(String type) {
		this.category.setText(type);
	}

	@FXML
	private ListView<HBox> listView;

	@FXML
	private JFXButton openProject;

	public void populateList() throws IOException {
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<HBox>() {
			@Override
			public void changed(ObservableValue<? extends HBox> observable, HBox oldValue, HBox newValue) {
				if (newValue.getChildren() != null && !newValue.getChildren().isEmpty()) {
					Label title = (Label) newValue.getChildren().get(0);
					setSelectedTitle(title.getText());
				}
			}
		});
		
		String absoluteDirPath = MasterConfigFileHandler.getInstance().getWokspaceDir() + getCategory();
		Files.createDirectories(Paths.get(absoluteDirPath));
		File[] directories = new File(absoluteDirPath).listFiles(File::isDirectory);
		for (File file : directories) {
			Label label = new Label();
			label.setText(file.getName());
			label.setPrefHeight(10);
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER);
			hbox.setPrefHeight(40);
			hbox.getChildren().add(label);
			HBox.setHgrow(listView, Priority.ALWAYS);

			listView.getItems().add(hbox);
		}
	}

	@FXML
	private void openProject(ActionEvent event) {
		if (getSelectedTitle() != null) {
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();
			Stage primaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WorkArea.fxml"));
			try {
				Parent parent = loader.load();
				WorkAreaController controller = loader.<WorkAreaController>getController();
				controller.setProjectName(getSelectedTitle());
				controller.setCategory(getCategory());
				controller.loadChain();
				MainApp.setScene(primaryStage, parent, 1000, 680);
			} catch (IOException e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}

	@FXML
	private void deleteProject(ActionEvent event) {
		int selectedIndex = listView.getSelectionModel().getSelectedIndex();
		Path directoryPath = Paths.get(MasterConfigFileHandler.getInstance().getWokspaceDir() + getCategory() + "\\" + getSelectedTitle());
		Stage categoryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		JFXDialogLayout layout = new JFXDialogLayout();
		JFXAlert<Void> alert = new JFXAlert<>(categoryStage);

		Label label = new Label("Are you sure to delete " + getSelectedTitle() + "?");

		JFXButton confirmButton = new JFXButton("YES");
		confirmButton.setButtonType(ButtonType.RAISED);
		confirmButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
		confirmButton.setOnAction(confirmEvent -> setDelete(directoryPath, selectedIndex, alert));

		JFXButton cancelButton = new JFXButton("CANCEL");
		cancelButton.setButtonType(ButtonType.RAISED);
		cancelButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
		cancelButton.setCancelButton(true);
		cancelButton.setOnAction(closeEvent -> alert.hideWithAnimation());

		HBox buttonBox = new HBox(10, confirmButton, cancelButton);
		buttonBox.setAlignment(Pos.CENTER);
		VBox popUpBox = new VBox(10, label, buttonBox);
		layout.setBody(popUpBox);
		alert.setOverlayClose(true);
		alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
		alert.setContent(layout);
		alert.initModality(Modality.NONE);
		alert.show();
	}
	
	@FXML
	private void importProject(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		Stage categoryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File file = fileChooser.showOpenDialog(categoryStage);
		if (file != null) {
			setZipFilePath(file.getAbsolutePath());
			String outputDirPath = MasterConfigFileHandler.getInstance().getWokspaceDir() + getCategory() + "\\Test Business\\";
			try {
				ZipUtils.unpack(outputDirPath, getZipFilePath());
				populateList();
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}

	@FXML
	private void exportProject(ActionEvent event) {
		if (selectedTitle != null) {
			String sourceDirPath = MasterConfigFileHandler.getInstance().getWokspaceDir() + getCategory() + "\\" + getSelectedTitle();
			String zipFilePath = MasterConfigFileHandler.getInstance().getWokspaceDir() + "\\Download";
			String zipFile = MasterConfigFileHandler.getInstance().getWokspaceDir() + "\\Download\\" + getSelectedTitle() + ".zip";
			try {
				ZipUtils.pack(sourceDirPath, zipFile);
				Desktop.getDesktop().open(new File(zipFilePath));
			} catch (IOException e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}
	
	private void setDelete(Path directoryPath, int selectedIndex, JFXAlert<Void> alert) {
		alert.close();
		try {
			Files.walk(directoryPath).sorted(Comparator.reverseOrder()).forEach(t -> {
				try {
					Files.delete(t);
				} catch (IOException e) {
					LOGGER.error(ExceptionUtils.getFullStackTrace(e));
				}
			});
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		}
		listView.getItems().remove(selectedIndex);
	}
}
