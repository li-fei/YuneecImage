package com.yuneec.image.module.curve;

import com.yuneec.image.CenterPane;
import com.yuneec.image.Configs;
import com.yuneec.image.Global;
import com.yuneec.image.module.Temperature;
import com.yuneec.image.module.box.BoxTemperature;
import com.yuneec.image.module.line.LineTemperManager;
import com.yuneec.image.utils.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class CurveManager {

    public static CurveManager instance;

    public static CurveManager getInstance() {
        if (instance == null) {
            instance = new CurveManager();
        }
        return instance;
    }

    public enum MouseStatus {
        MouseDragged,
        MousePressed,
        MouseReleased,
        MouseClicked
    }

    private int lastx, lasty;
    private int x, y;
    private int startX = 0;
    private int startY = 0;

    public void setMouseMousePressedXY(int x, int y, MouseStatus status) {
        LineTemperManager.getInstance().setMouseMousePressedXY(x,y,status);

        if (CenterPane.getInstance().centerSettingFlag != CenterPane.CenterSettingSelect.CURVE) {
            return;
        }
        if (status == MouseStatus.MousePressed){
            startX = x;
            startY = y;
        }
        if (x >= Global.currentOpenImageWidth){
            x = (int) Global.currentOpenImageWidth;
        }
        if (x <= 0){
            x = 0;
        }
        if (y >= Global.currentOpenImageHeight){
            y = (int) Global.currentOpenImageHeight;
        }
        if (y <= 0){
            y = 0;
        }
        this.x = x;
        this.y = y;
        if (status == MouseStatus.MousePressed){
            if (BackStepManager.getInstance().getCurrentCurveCount() == BackStepManager.MAX_CURVE_COUNT && BackStepManager.openTemperatureLimit){
                BackStepManager.getInstance().backStep(Temperature.TYPE.CURVE);
            }
            this.lastx = x;
            this.lasty = y;
            xyList.clear();
            lineList = new ArrayList();
            lineList.clear();
        }
        Line line = CenterPane.getInstance().drawLine(lastx,lasty,x,y,Configs.temperatureColor);
        CenterPane.getInstance().showImagePane.getChildren().add(line);
        OneLine oneLine = new OneLine(line,lastx,lasty,x,y);
        lineList.add(oneLine);
        this.lastx = x;
        this.lasty = y;

        getCurveAllXY();
        if (status == MouseStatus.MouseReleased){
            startCalculate();
        }
    }

    private void startCalculate() {
        temperatureList.clear();
        getCurveAllTemperature();
        getCurveMaxMinTemperature();
        xyList.clear();
    }

    private ArrayList lineList = new ArrayList();
    private ArrayList<int[]> xyList = new ArrayList<int[]>();
    public ArrayList temperatureList = new ArrayList();
    public ArrayList curveTemperatureList = new ArrayList();

    private void getCurveAllXY() {
        int[] xy = new int[]{x ,y};
        xyList.add(xy);
    }

    private void getCurveAllTemperature() {
        for (int i=0;i<xyList.size();i++){
            int x = xyList.get(i)[0];
            int y = xyList.get(i)[1];
            temperatureList.add(TemperatureAlgorithm.getInstance().getTemperature(x,y));
        }
    }

    float maxTemperature = 0;
    int maxTemperatureIndex = 0;
    float minTemperature = 0;
    int minTemperatureIndex = 0;
    float avgTemperature = 0;
    float allTemperature = 0;
    private void getCurveMaxMinTemperature(){
        maxTemperatureIndex = 0;
        minTemperatureIndex = 0;
        allTemperature = 0;
        maxTemperature = (float)temperatureList.get(0);
        minTemperature = (float)temperatureList.get(0);
        for (int i = 0; i < temperatureList.size(); i++) {
            if ((float)temperatureList.get(i) > maxTemperature) {
                maxTemperature = (float)temperatureList.get(i);
                maxTemperatureIndex = i;
            }
            if ((float)temperatureList.get(i) < minTemperature) {
                minTemperature = (float)temperatureList.get(i);
                minTemperatureIndex = i;
            }
            allTemperature += (float)temperatureList.get(i);
        }
//		YLog.I("CurveTemperatureUtil..." + " maxTemperature:" + maxTemperature  + " maxTemperatureIndex:" + maxTemperatureIndex
//				+ " minTemperature:" + minTemperature + " minTemperatureIndex:" + minTemperatureIndex);

        int[] maxTemperatureXY = xyList.get(maxTemperatureIndex);
        int[] minTemperatureXY = xyList.get(minTemperatureIndex);
        avgTemperature = allTemperature / temperatureList.size();
        int[] avgTemperatureXY = {startX,startY};

        ArrayList curveTemperatureNodeMax = CenterPane.getInstance().addLabelInImage(maxTemperatureXY[0],maxTemperatureXY[1],maxTemperature,Configs.red_color);
        ArrayList curveTemperatureNodeMin = CenterPane.getInstance().addLabelInImage(minTemperatureXY[0],minTemperatureXY[1],minTemperature,Configs.blue2_color);
        ArrayList curveTemperatureNodeAvg = CenterPane.getInstance().addAvgLabelInImage(avgTemperatureXY[0],avgTemperatureXY[1],avgTemperature,Configs.white_color);

        CurveTemperature curveTemperature = new CurveTemperature(lineList,curveTemperatureNodeMax,curveTemperatureNodeMin,curveTemperatureNodeAvg);
        curveTemperature.setMaxWindowDraw(WindowChange.I().maxWindow);
        curveTemperatureList.add(curveTemperature);
        BackStepManager.getInstance().addTemperatureInfo(curveTemperature);
    }

    public void recalculate(){
        allTemperature = 0;
        for (int i = 0; i < curveTemperatureList.size(); i++) {
            CurveTemperature curveTemperature = (CurveTemperature) curveTemperatureList.get(i);
            if (!curveTemperature.getCurveTemperatureNodeMax().isEmpty()){
                Label label = (Label) curveTemperature.getCurveTemperatureNodeMax().get(0);
                int x = (int) curveTemperature.getCurveTemperatureNodeMax().get(5);
                int y = (int) curveTemperature.getCurveTemperatureNodeMax().get(6);
                float newPointTemperature = TemperatureAlgorithm.getInstance().getTemperature(x,y);
                label.setText(Utils.getFormatTemperature(newPointTemperature));
            }
            if (!curveTemperature.getCurveTemperatureNodeMin().isEmpty()){
                Label label = (Label) curveTemperature.getCurveTemperatureNodeMin().get(0);
                int x = (int) curveTemperature.getCurveTemperatureNodeMin().get(5);
                int y = (int) curveTemperature.getCurveTemperatureNodeMin().get(6);
                float newPointTemperature = TemperatureAlgorithm.getInstance().getTemperature(x,y);
                label.setText(Utils.getFormatTemperature(newPointTemperature));
            }
            if (!curveTemperature.getCurveTemperatureNodeAvg().isEmpty()){
                Label label = (Label) curveTemperature.getCurveTemperatureNodeMin().get(0);
                for (int j = 0; j < temperatureList.size(); j++) {
                    allTemperature += (float)temperatureList.get(j);
                }
                float newPointTemperature = allTemperature / temperatureList.size();
                label.setText(Utils.getFormatTemperature(newPointTemperature));
            }
        }
    }

}
