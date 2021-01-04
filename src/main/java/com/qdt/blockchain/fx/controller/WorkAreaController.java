package com.qdt.blockchain.fx.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXDialogLayout;
import com.qdt.blockchain.block.Block;
import com.qdt.blockchain.block.BlockHandler;
import com.qdt.blockchain.chain.Chain;
import com.qdt.blockchain.fx.InternalConfigFileHandler;
import com.qdt.blockchain.fx.MasterConfigFileHandler;
import com.qdt.blockchain.support.utils.EncryptDecryptUtil;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WorkAreaController {
	private final static Logger LOGGER = LoggerFactory.getLogger(WorkAreaController.class);
	
	private static final String GLYPH_STYLE="-fx-fill: linear-gradient(#ffffff, #d2d2d2); -fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.8) , 4 , 0.0 , 1 , 1 )";
	private String projectName;
	private String projectPath;
	private String absolutePath; 
	private String lastHash;
	private String fileName;
	private String category;
	private Integer selectedIndex;
	private InputStream is;
	private List<Block> blockChain;
	private InternalConfigFileHandler configHandler;
	@FXML
	private boolean newProject;
	
	@FXML
	private BorderPane borderpane;
	@FXML
	private TextField title;
	@FXML
	private TextArea description;
	@FXML
	private Label date;
	@FXML
	private Label attachment;
	@FXML
	private JFXButton downloadButton;
	
	public void initialize() {}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public boolean isNewProject() {
		return newProject;
	}
	public void setNewProject(boolean flag) {
		this.newProject = flag;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getSelectedIndex() {
		return selectedIndex;
	}
	public void setSelectedIndex(Integer selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public void loadChain() {
		Chain chain = new Chain();
		blockChain = chain.getBlockChain();
		title.setDisable(true);
		description.setDisable(true);

		ListView<HBox> listView = new ListView<>();
		listView.setOrientation(Orientation.HORIZONTAL);
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<HBox>() {

			@Override
			public void changed(ObservableValue<? extends HBox> observable, HBox oldValue, HBox newValue) {
				if (null != oldValue) {
					StackPane roundWrap = (StackPane) oldValue.getChildren().get(0);
					StackPane round = (StackPane) roundWrap.getChildren().get(1);
					FontAwesomeIcon icon = (FontAwesomeIcon) round.getChildren().get(0);
					icon.setGlyphStyle(GLYPH_STYLE);
				}
				StackPane roundWrap = (StackPane) newValue.getChildren().get(0);
				StackPane round = (StackPane) roundWrap.getChildren().get(1);
				FontAwesomeIcon icon = (FontAwesomeIcon) round.getChildren().get(0);
				icon.setGlyphStyle("-fx-fill: GREEN");
				int selectedIndex = listView.getSelectionModel().getSelectedIndex();
				setSelectedIndex(selectedIndex);
				populateDescription(blockChain, selectedIndex);
			}

			
		});
		
		projectPath = getCategory() + "\\" + getProjectName() + "\\"; 
		absolutePath = MasterConfigFileHandler.getInstance().getWokspaceDir() + projectPath;
		if(isNewProject()) {
			InternalConfigFileHandler.createConfigFile(projectPath);
		}
		configHandler = new InternalConfigFileHandler(projectPath);
		String firstHash = configHandler.getFirstHash();
		lastHash = configHandler.getLastHash();
		boolean isValidChain = true;
		if (firstHash == null)
			setNewProject(true);
		int i = 1;
		while(firstHash != null && !firstHash.isEmpty()) {
			Block block = BlockHandler.readBlock(absolutePath, firstHash);
			if (block == null) {
				isValidChain = false;
				break;
			}
			blockChain.add(block);
			firstHash = block.getNextHash();
			FontAwesomeIcon icon = new FontAwesomeIcon();
			icon.setIcon(FontAwesomeIconName.CIRCLE_ALT);
			icon.setSize("8em");
			icon.setTextAlignment(TextAlignment.CENTER);
			icon.setGlyphStyle(GLYPH_STYLE);

			Label label2 = new Label();
			label2.setText(String.valueOf(i));
			label2.setStyle("-fx-text-fill: WHITE; -fx-font-size: 24px;");

			StackPane round = new StackPane();
			round.setAlignment(Pos.CENTER);
			round.getChildren().addAll(icon, label2);
			
			StackPane roundWrap = new StackPane();
			roundWrap.setAlignment(Pos.CENTER);
			roundWrap.setPrefWidth(100);
			SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");  
			Date dateVal = new Date(block.getTimeStamp());  
			String dateText = formatter.format(dateVal);  
			
			Label dateLbl = new Label(dateText);dateLbl.setStyle("-fx-text-fill: WHITE");dateLbl.setPadding(new Insets(0, 0, 120, 0));
			Label titleLbl = new Label(block.getTitle());titleLbl.setStyle("-fx-text-fill: WHITE;");titleLbl.setPadding(new Insets(50, 0, 0, 0));
			titleLbl.setWrapText(true);
			titleLbl.setAlignment(Pos.CENTER);
			titleLbl.setMaxWidth(roundWrap.getPrefWidth());
			titleLbl.heightProperty().addListener((obs, oldVal, newVal) -> {
			    titleLbl.setTranslateY(newVal.doubleValue() * 0.5);
			});
			roundWrap.getChildren().addAll(dateLbl, round, titleLbl);
			
			FontAwesomeIcon arrow = new FontAwesomeIcon();
			arrow.setIcon(FontAwesomeIconName.LONG_ARROW_RIGHT);
			arrow.setSize("2em");
			arrow.setGlyphStyle("-fx-fill: WHITE");
			
			HBox nodeContainer = new HBox();
			nodeContainer.setAlignment(Pos.CENTER);
			nodeContainer.getChildren().addAll(roundWrap, arrow);

			listView.getItems().add(nodeContainer);
			i++;
		}
		final Separator sepVertical = new Separator();
		sepVertical.setValignment(VPos.BOTTOM);
		
		if(!isValidChain && !chain.isChainValid()) {
			Label label = new Label("Blockchain Corrupted");
			label.setStyle("-fx-text-fill: WHITE; -fx-font-size: 48px;");
			HBox outerContainer = new HBox(label);
			outerContainer.setAlignment(Pos.CENTER);
			VBox centerContainer = new VBox(outerContainer, sepVertical);
			VBox.setVgrow(outerContainer, Priority.ALWAYS);
			centerContainer.setAlignment(Pos.CENTER);
			borderpane.setCenter(centerContainer);
		} else {
			if (isNewProject()) {
				Label label = new Label("Click \"Create New\" Node");
				label.setStyle("-fx-text-fill: WHITE; -fx-font-size: 48px;");
				HBox outerContainer = new HBox(label);
				outerContainer.setAlignment(Pos.CENTER);
				VBox centerContainer = new VBox(outerContainer, sepVertical);
				VBox.setVgrow(outerContainer, Priority.ALWAYS);
				centerContainer.setAlignment(Pos.CENTER);
				borderpane.setCenter(centerContainer);
			} else {
				HBox outerContainer = new HBox(listView);
				outerContainer.setAlignment(Pos.CENTER);
				HBox.setHgrow(listView, Priority.ALWAYS);
				listView.scrollTo(listView.getItems().size());

				VBox centerContainer = new VBox(outerContainer, sepVertical);
				centerContainer.setAlignment(Pos.CENTER);
				borderpane.setCenter(centerContainer);
			}
		}
	}
	
	@FXML
	private void createNewNode(ActionEvent event) {
		Stage categoryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    JFXDialogLayout layout = new JFXDialogLayout();
	    JFXAlert<Void> alert = new JFXAlert<>(categoryStage);
	    
	    Label labelTitle=new Label("Title*");
	    TextField textFieldTitle = new TextField();
	    textFieldTitle.setPromptText("Enter title text for node");
        
        Label labelDesc=new Label("Description");
	    TextArea textAreaDesc = new TextArea();
	    textAreaDesc.setPrefHeight(100);
	    textAreaDesc.setWrapText(true);
	    textAreaDesc.setPromptText("Enter description for node");
	    
	    Label labelFileChoose=new Label("Attachment");
	    Label fileNameLabel = new Label();
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Select File to Attach");

	    JFXButton fileChooseButton = new JFXButton("Select File"); 
	    fileChooseButton.setButtonType(ButtonType.RAISED);
	    fileChooseButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
	    
        EventHandler<ActionEvent> fileEvent =  
        new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                File file = fileChooser.showOpenDialog(categoryStage); 
                fileChooser(fileNameLabel, file); 
            }
        }; 
        fileChooseButton.setOnAction(fileEvent); 
        
        HBox fileChooseBox = new HBox(labelFileChoose, fileChooseButton);
        fileChooseBox.setSpacing(20);
        
        JFXButton createButton = new JFXButton("CREATE");
        createButton.setButtonType(ButtonType.RAISED);
        createButton.disableProperty().bind(Bindings.isEmpty(textFieldTitle.textProperty()));
        createButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
        createButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alert.close();
				Block newBlock = new Block(lastHash);
				newBlock.setTitle(textFieldTitle.getText());
				newBlock.setDescription(textAreaDesc.getText());
				newBlock.setFileName(getFileName());
				try {
					if (null != is) {
						newBlock.setData(EncryptDecryptUtil.applyAES256Encryption(IOUtils.toByteArray(is), "AN3RNBCMHY6JQ84U"));
					}
					BlockHandler.writeBlock(absolutePath, newBlock);
					if (isNewProject()) {
						configHandler.setProps(newBlock.getHash(), newBlock.getHash());
						setNewProject(false);
					} else {
						configHandler.setProps(null, newBlock.getHash());
						BlockHandler.updateBlockNextHash(absolutePath, newBlock.getPreviousHash(), newBlock.getHash());
					}
					loadChain();
				} catch (IOException | NoSuchAlgorithmException e) {
					LOGGER.error(ExceptionUtils.getFullStackTrace(e));
				}
				BlockHandler.writeBlock(projectPath, newBlock);
				setFileName(null);
			}
		});
        
        JFXButton cancelButton = new JFXButton("CANCEL");
        cancelButton.setButtonType(ButtonType.RAISED);
        cancelButton.setStyle("-fx-background-color:WHITE;-fx-font-size:14px;");
    	cancelButton.setCancelButton(true);
    	cancelButton.setOnAction(closeEvent -> alert.hideWithAnimation());
        
    	HBox hbox = new HBox(10, createButton, cancelButton);
    	hbox.setAlignment(Pos.CENTER);
        VBox popUpBox = new VBox(10, labelTitle, textFieldTitle, labelDesc, textAreaDesc, fileChooseBox, fileNameLabel);
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
	
	@FXML
	private void downloadAttachment(ActionEvent event) {
		Integer selectedIndex = getSelectedIndex();
		if (null != selectedIndex) {
			Block block = blockChain.get(selectedIndex);
			if (null != block.getData() && null != block.getFileName()) {
				String downloadPath = MasterConfigFileHandler.getInstance().getWokspaceDir() + "\\Download\\";
				BlockHandler.downloadFile(downloadPath, block);
				try {
					Desktop.getDesktop().open(new File(downloadPath));
				} catch (IOException e) {
					LOGGER.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		}
		selectedIndex = null;
	}
	
	private void populateDescription(List<Block> blockChain, int selectedIndex) {
		Block block = blockChain.get(selectedIndex);
		if (block != null) {
			SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");  
			Date dateVal = new Date(block.getTimeStamp());  
			String dateText = formatter.format(dateVal);  	
			
			title.setDisable(false);
			description.setDisable(false);
			
			title.setText(block.getTitle());
			description.setText(block.getDescription());
			date.setText(dateText);
			attachment.setText(block.getFileName());
		}
	}
	
	private void fileChooser(Label fileNameLabel, File file) {
		if (file != null) { 
			String absolutePath = file.getAbsolutePath();
			try {
				is = new FileInputStream(absolutePath);
			} catch (FileNotFoundException e) {
				LOGGER.error(ExceptionUtils.getFullStackTrace(e));
			}
			setFileName(file.getName());
			fileNameLabel.setText(absolutePath); 
        }
	} 
}
