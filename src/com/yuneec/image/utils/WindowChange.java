package com.yuneec.image.utils;

import com.yuneec.image.CenterPane;
import com.yuneec.image.Configs;
import com.yuneec.image.Global;
import com.yuneec.image.guide.GuideTemperatureAlgorithm;
import com.yuneec.image.module.point.PointManager;
import com.yuneec.image.module.point.PointTemperature;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WindowChange {

    private static WindowChange instance;
    public boolean maxWindow = false;
    private final double defaultImageZoomRatio = 1;
    private double imageZoomRatio = defaultImageZoomRatio;
    private double maxToMinRatio;

    public static WindowChange I() {
        if (instance == null) {
            instance = new WindowChange();
        }
        return instance;
    }

    public void init() {
        Screen screen = Screen.getPrimary();
        Rectangle2D r1 = screen.getBounds();
        Rectangle2D r2 = screen.getVisualBounds();
        YLog.I("WindowChange " + r1.getHeight() + "," + r1.getWidth());
        YLog.I("WindowChange " + r2.getHeight() + "," + r2.getWidth());
    }

    public void setWindowMax(boolean max) {
        maxWindow = max;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                double width = Global.primaryStage.getWidth();
                double height = Global.primaryStage.getHeight();

                Configs.SceneHeight = (int) height;
                Configs.CenterPanelHeight = (int) (height - Configs.SystemBarHeight - Configs.MenuHeight - Configs.LineHeight);
                Configs.CenterPanelWidth = (int) (width - Configs.LeftPanelWidth - Configs.RightPanelWidth);

                CenterPane.getInstance().centerPane.setPrefWidth(Configs.CenterPanelWidth);
                CenterPane.getInstance().centerSettingPane.setPrefWidth(Configs.CenterPanelWidth);
                CenterPane.getInstance().centerImagePane.setPrefWidth(Configs.CenterPanelWidth);

                double wRatio = Configs.CenterPanelWidth / Configs.DefaultImageWidth;
                double hRatio = Configs.CenterPanelHeight / Configs.DefaultImageHeight;
                if (wRatio < hRatio) {
                    imageZoomRatio = wRatio;
                } else {
                    imageZoomRatio = hRatio;
                }
                imageZoomRatio-=0.1;
//                YLog.I("WindowChange   width:" + width + " , height:" + height + "  ,max :" + max +
//                        " ,wRatio:" + wRatio + " ,hRatio:" + hRatio + " ,imageZoomRatio:" + imageZoomRatio);
                reLoadImage();
            }
        }, 100);
    }

    private void reLoadImage() {
        if (maxWindow) {
            Global.currentOpenImageWidth = Configs.DefaultImageWidth * imageZoomRatio;
            Global.currentOpenImageHeight = Configs.DefaultImageHeight * imageZoomRatio;
            maxToMinRatio = Configs.DefaultImageWidth / Global.currentOpenImageWidth;
            CenterPane.getInstance().imageView.setFitWidth(Configs.DefaultImageWidth * imageZoomRatio);
            CenterPane.getInstance().imageView.setFitHeight(Configs.DefaultImageHeight * imageZoomRatio);
            CenterPane.getInstance().showImagePane.setPrefWidth(Configs.DefaultImageWidth * imageZoomRatio);
            CenterPane.getInstance().showImagePane.setPrefHeight(Configs.DefaultImageHeight * imageZoomRatio);
        } else {
            Global.currentOpenImageWidth = Configs.DefaultImageWidth;
            Global.currentOpenImageHeight = Configs.DefaultImageHeight;
            CenterPane.getInstance().imageView.setFitWidth(Configs.DefaultImageWidth);
            CenterPane.getInstance().imageView.setFitHeight(Configs.DefaultImageHeight);
            CenterPane.getInstance().showImagePane.setPrefWidth(Configs.DefaultImageWidth);
            CenterPane.getInstance().showImagePane.setPrefHeight(Configs.DefaultImageHeight);
        }
        CenterPane.getInstance().getImagePaneOffsetXY();
        reLayoutTemperature();
    }

    private void reLayoutTemperature() {
        if (maxWindow) {
            GuideTemperatureAlgorithm.SupportScale = true;
        }else {
//            YLog.I("maxToMinRatio:" + maxToMinRatio);
            imageZoomRatio = defaultImageZoomRatio;
            GuideTemperatureAlgorithm.SupportScale = false;
        }
        reLayoutPointTemperatureLocation();
    }

    private void reLayoutPointTemperatureLocation() {
        for (int i = 0; i < PointManager.getInstance().pointTemperatureNodeList.size(); i++) {
            PointTemperature pointTemperature = (PointTemperature) PointManager.getInstance().pointTemperatureNodeList.get(i);
            ArrayList pointNodeList = pointTemperature.getPointTemperatureNode();
            Label label = (Label) pointNodeList.get(0);
            Circle circle = (Circle) pointNodeList.get(1);
            Line xLine = (Line) pointNodeList.get(2);
            Line yLine = (Line) pointNodeList.get(3);
            float temperature = (float) pointNodeList.get(4);
            int x = (int) pointNodeList.get(5);
            int y = (int) pointNodeList.get(6);
            boolean maxClickPoint = (boolean) pointNodeList.get(7);
            if (maxClickPoint){
                if (maxWindow){
                    imageZoomRatio = defaultImageZoomRatio;
                }else {
                    imageZoomRatio = maxToMinRatio;
                }
            }
            label.setTranslateX((x*imageZoomRatio+7));
            label.setTranslateY((y*imageZoomRatio-8));
            circle.setCenterX(x*imageZoomRatio);
            circle.setCenterY(y*imageZoomRatio);
            double lineLEN = 7;
            reLayoutLine(x*imageZoomRatio-lineLEN,y*imageZoomRatio,x*imageZoomRatio+lineLEN,y*imageZoomRatio,xLine);
            reLayoutLine(x*imageZoomRatio,y*imageZoomRatio-lineLEN,x*imageZoomRatio,y*imageZoomRatio+lineLEN,yLine);
        }
    }

    private void reLayoutLine(double StartX, double StartY, double EndX, double EndY, Line line) {
        line.setStartX(StartX);
        line.setStartY(StartY);
        line.setEndX(EndX);
        line.setEndY(EndY);
    }


}
