package com.yuneec.image;


import java.io.File;

import com.yuneec.image.Configs;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class ScaleImage extends Application {
    Point2D dragDistance = null;
    public void start(Stage primaryStage) {

        String file = Global.currentOpenImagePath;
        ImageView imageView = new ImageView();
        System.out.println(file);
        Image image = new Image("file:" + file);
        imageView.setImage(image);

        double windowWidth = 640;
        double windowHeight = 512;
        final double w = image.getWidth();
        final double h = image.getHeight();
        System.out.println("image w:" + w + ",h:"+h);
        final double max = Math.max(w, h);
        final int width = (int) (400 * w / max);
        final int heigth = (int) (400 * h / max);
        imageView.setFitHeight(h);
        imageView.setFitWidth(w);

        Pane imagePane = new Pane();

//        init4Pane();

//        imageView.setX((pane.getWidth() - imageView.getFitWidth()) / 2);
//        imageView.setY((pane.getHeight() - imageView.getFitHeight()) / 2);

//        pane.setBackground(new Background(new BackgroundFill(Color.web(Configs.grey_color), null, null)));
        StackPane stackPane = new StackPane(imagePane);
        stackPane.setBackground(new Background(new BackgroundFill(Color.web(Configs.backgroundColor), null, null)));
        Scene scene = new Scene(stackPane, windowWidth, windowHeight);
        imagePane.getChildren().add(imageView);
//        pane.getChildren().add(topPane);
        imagePane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                imageView.setY((newValue.doubleValue() - imageView.getFitHeight()) / 2);
            }
        });

        imagePane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                imageView.setX((newValue.doubleValue() - imageView.getFitWidth()) / 2);
            }
        });

        final double scale = 10;
        stackPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double rate = 0;
            if (event.getDeltaY() > 0) {
                rate = 0.05;
            } else {
                rate = -0.05;
            }
            newWidth = (int) (imageView.getFitWidth() + w * rate);
            newHeight = (int) (imageView.getFitHeight() + h * rate);
            if (newWidth <= width || newWidth > scale * width) {
                return;
            }

            Point2D eventPoint = new Point2D(event.getSceneX(), event.getSceneY());
            Point2D imagePoint = imagePane.localToScene(new Point2D(imageView.getX(), imageView.getY()));
            Rectangle2D imageRect = new Rectangle2D(imagePoint.getX(), imagePoint.getY(), imageView.getFitWidth(), imageView.getFitHeight());
            Point2D ratePoint;
            Point2D eventPointDistance;
            if (newWidth > scale / 4 * width && imageRect.contains(eventPoint)) {
                ratePoint = eventPoint.subtract(imagePoint);
                ratePoint = new Point2D(ratePoint.getX() / imageView.getFitWidth(), ratePoint.getY() / imageView.getFitHeight());
                eventPointDistance = imagePane.sceneToLocal(eventPoint);
            } else {
                ratePoint = new Point2D(0.5, 0.5);
                eventPointDistance = new Point2D(imagePane.getWidth() / 2, imagePane.getHeight() / 2);
            }

            offsetX = eventPointDistance.getX() - newWidth * ratePoint.getX();
            offsetY = eventPointDistance.getY() - newHeight * ratePoint.getY();
            imageView.setX(offsetX);
            imageView.setY(offsetY);
            imageView.setFitWidth(newWidth);
            imageView.setFitHeight(newHeight);
            String s = "eventPointDistance.getX():" + eventPointDistance.getX() + " eventPointDistance.getY():" + eventPointDistance.getY()
                    + " offsetX:" + offsetX + " offsetY:" + offsetY;
//            System.out.println("ScaleImage MouseEvent:" + s);
        });

        draggable(stackPane);

        imagePane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                int x = (int) (e.getX() - offsetX);
                int y = (int) (e.getY() - offsetY);
                String s = "x = " + x + " y = " + y + " offsetX:" + offsetX + " offsetY:" + offsetY
                        + " newWidth:" + newWidth + " newHeight:" + newHeight;
                System.out.println("ScaleImage MouseEvent:" + s);
            }
        });

        scene.setFill(Paint.valueOf(Configs.backgroundColor));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    double offsetX,offsetY;
    int newWidth,newHeight;

    Pane topPane;
    private void init4Pane() {
        topPane = new Pane();
        topPane.setBackground(new Background(new BackgroundFill(Color.web(Configs.backgroundColor), null, null)));
        topPane.setPrefHeight(50);
        topPane.setPrefWidth(640);
    }

    private static class Position {
        double x;
        double y;
    }

    private void draggable(Node node) {
        final Position pos = new Position();
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> node.setCursor(Cursor.HAND));
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> node.setCursor(Cursor.DEFAULT));
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            node.setCursor(Cursor.MOVE);
            pos.x = event.getX();
            pos.y = event.getY();

        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> node.setCursor(Cursor.DEFAULT));
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            double distanceX = event.getX() - pos.x;
            double distanceY = event.getY() - pos.y;
            double x = node.getLayoutX() + distanceX;
            double y = node.getLayoutY() + distanceY;
            node.relocate(x, y);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
