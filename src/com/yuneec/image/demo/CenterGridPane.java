package com.yuneec.image.demo;

import com.yuneec.image.CenterPane;
import com.yuneec.image.Configs;
import com.yuneec.image.Global;
import com.yuneec.image.ScaleImage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CenterGridPane extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	GridPane gridpane;
	String fileName = "image/Yuneec05.jpg";

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Pane root = new Pane();
		FlowPane centerPane = new FlowPane();
		centerPane.setPrefWidth(Configs.CenterPanelWidth);
		centerPane.setPrefHeight(Configs.SceneHeight);
		centerPane.setTranslateX(Configs.LeftPanelWidth);
		centerPane.setBackground(new Background(new BackgroundFill(Color.web(Configs.backgroundColor), null, null)));
		root.getChildren().add(centerPane);

		gridpane = new GridPane();
//		gridpane.setBackground(new Background(new BackgroundFill(Color.web(Configs.blue_color), null, null)));
		gridpane.setAlignment(Pos.CENTER);
		gridpane.setPrefWidth(Configs.CenterPanelWidth);
		gridpane.setPrefHeight(Configs.SceneHeight);
		gridpane.setHgap(15);
		gridpane.setVgap(15);
//		gridpane.setPadding(new Insets(25, 25, 25, 25));

		for (int i=0;i<4;i++){
			for (int j=0;j<3;j++){
				Pane item = new Pane();
				Button button = creatSettingButton(fileName,"");
				item.setPrefWidth(320/2);
				item.setPrefHeight(256/2);
//				item.setBackground(new Background(new BackgroundFill(Color.web(Configs.grey_color), null, null)));
				item.getChildren().add(button);
				gridpane.add(item,i,j);
			}
		}

		centerPane.getChildren().add(gridpane);

		Scene scene = new Scene(root, Configs.SceneWidth, Configs.SceneHeight);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	Background centerSettingButtonClickBackground = new Background(new BackgroundFill(Paint.valueOf(Configs.backgroundColor), new CornerRadii(5), new Insets(1)));
	Background centerSettingButtonUnclickBackground = new Background(new BackgroundFill(Paint.valueOf(Configs.lightGray_color), new CornerRadii(5), new Insets(1)));
	Border ouClickborder = new Border(new BorderStroke(Paint.valueOf(Configs.blue_color),BorderStrokeStyle.SOLID,new CornerRadii(5),new BorderWidths(1.5)));
	Border clickborder = new Border(new BorderStroke(Paint.valueOf(Configs.red_color),BorderStrokeStyle.SOLID,new CornerRadii(5),new BorderWidths(1.5)));
	ArrayList allImageButton = new ArrayList();
	public Button creatSettingButton(String imagePath,String text) {
		Button button = new Button();
		button.setText(text);
		button.setTextFill(Paint.valueOf(Configs.grey_color));
		if(imagePath != null){
			ImageView imageView = new ImageView(new Image(imagePath));
			imageView.setFitWidth(320/2 - 20);
			imageView.setFitHeight(256/2 - 10);
			button.setGraphic(imageView);
			button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				setAllBackground(button);
				if (event.getClickCount() == 2) {
					Global.currentOpenImagePath = "F:\\intellijSpace\\YuneecImage\\src\\image\\Yuneec05.jpg";
					new ScaleImage().start(new Stage());
				}
			});
		}
		button.setBackground(centerSettingButtonUnclickBackground);
		button.setBorder(ouClickborder);
		allImageButton.add(button);
		return button;
	}

	private void setAllBackground(Button button){
		for (int i=0;i<allImageButton.size();i++){
			Button b = (Button) allImageButton.get(i);
			b.setBackground(centerSettingButtonUnclickBackground);
			b.setBorder(ouClickborder);
		}
		button.setBackground(centerSettingButtonClickBackground);
		button.setBorder(clickborder);
	}
	
	

}
