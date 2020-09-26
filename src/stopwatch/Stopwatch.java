/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stopwatch;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;


/**
 *
 * @author Carson Rottinghaus, cqrqfd, 16265777
 */
class Timer {
    private List<Pair<Instant, Action>> events = new ArrayList<>();
    private int targetMilli;

    public Timer() {
        start();
    }

    public Timer(int seconds) {
        start();
        targetMilli = seconds * 1000;
    }

    public void start() {
        events.add(new Pair(Instant.now(), Action.START));
    }

    public void stop() {
        events.add(new Pair(Instant.now(), Action.STOP));
    }
    
    public boolean isStopped(){
        return events.get(events.size()-1).getValue() == Action.STOP;
    }
    
    public int getTargetMilli(){
        return targetMilli;
    }

    public int getTotalStartTime() {
        int totalMillis = 0;
        for (int i = 0; i < events.size() - 1; i++) {
            Pair<Instant, Action> lastEvent = events.get(i);
            Pair<Instant, Action> currentEvent = events.get(i + 1);
            if (lastEvent.getValue().equals(Action.START)) {
                totalMillis += currentEvent.getKey().toEpochMilli() - lastEvent.getKey().toEpochMilli();
            }
        }
        if(!this.isStopped()){
            totalMillis += calculateEventDifference(Instant.now(),events.get(events.size()-1).getKey());
        }
        return totalMillis;
    }

    public long getTotalTime() {
        return calculateEventDifference(events.get(0).getKey(), Instant.now());
    }

    private static long calculateEventDifference(Instant thing, Instant thang) {
        return Math.abs(thing.toEpochMilli() - thang.toEpochMilli());
    }
}

enum Action {
    START, STOP;
}

public class Stopwatch extends Application {
    
    public String convertTime(long epochMilli){

        long sec = (epochMilli / 1000) % 60;
        long min = (epochMilli / 60000) % 60;
        long dec = epochMilli % 1000;
        
        return(min+":"+sec+"."+dec);
    }
    
    @Override
    public void start(Stage primaryStage) {
        int allowedTime;
        TextInputDialog td = new TextInputDialog(""); 
        td.setHeaderText("Set up the start time: ");
        td.setContentText("Please set up the start time in seconds (Integer)");
        td.showAndWait();
        while(true)
            try{
                allowedTime = Integer.parseInt(td.getEditor().getText());
                if(allowedTime <= 0){
                    throw new IllegalArgumentException();
                }
                break;
            }
            catch(IllegalArgumentException e){
                td.setHeaderText("Set up the start time: ");
                td.setContentText("Please set up the start time (Integer)");
                td.showAndWait();
            }
        
        Timer timer = new Timer(allowedTime);
        Label timeDisplay = new Label("00:00.00");
        Label timerText = new Label("Timer");
        Label timerLabel = new Label("00.00");
        Label recordText1 = new Label("Rec");
        Label recordText2 = new Label("Rec");
        Label recordText3 = new Label("Rec");
        Label recordLabel = new Label("00.00");
        Label recordLabel2 = new Label("00.00");
        Label recordLabel3 = new Label("00.00");
        Button stop = new Button("Stop");
        Button record = new Button("Record");
        ImageView cFace = new ImageView();
        ImageView cHand = new ImageView();
        Image clockFace = new Image(Stopwatch.class.getResourceAsStream("clockface.png"));
        Image clockHand = new Image(Stopwatch.class.getResourceAsStream("hand.png"));
        cFace.setImage(clockFace);
        cHand.setImage(clockHand);
        HBox buttonBox = new HBox(stop, record);
        HBox timerBox = new HBox(timerText,timerLabel);
        HBox recordBox1 = new HBox(recordText1, recordLabel);
        HBox recordBox2 = new HBox(recordText2, recordLabel2);
        HBox recordBox3 = new HBox(recordText3, recordLabel3);
        
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        Scene scene = new Scene(grid, 600, 400);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setHgap(4);
        grid.setVgap(4);
        buttonBox.setSpacing(10);
        timerBox.setSpacing(10);
        recordBox1.setSpacing(5);
        recordBox2.setSpacing(5);
        recordBox3.setSpacing(5);
        grid.add(cFace,0,0);
        grid.add(cHand,0,0);
        grid.add(timeDisplay,1,1);
        timeDisplay.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
        grid.add(timerBox,1,2);
        grid.add(recordBox1,1,3);
        grid.add(recordBox2,1,4);
        grid.add(recordBox3,1,5);
        grid.add(buttonBox,1,6);
        primaryStage.setTitle("StopWatch");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(timer.isStopped()){
                    timer.start();
                    stop.setText("Stop");
                    record.setText("Record");
                }
                else{
                    timer.stop();
                    stop.setText("Start");
                    record.setText("Reset");
                }
            }
        });
        List records = new ArrayList<>();
        record.setOnAction(new EventHandler<ActionEvent>() {
            int recordCount = 0;
            @Override
            public void handle(ActionEvent event) {
                if(timer.isStopped()){
                    System.out.println("RESETTING");
                    timeDisplay.setText("00:00.00");
                    timerLabel.setText("00.00");
                    recordLabel.setText("00.00");
                    recordLabel2.setText("00.00");
                    recordLabel3.setText("00.00");
                    recordText1.setText("Rec");
                    recordText2.setText("Rec");
                    recordText3.setText("Rec");
                    
                }
                else{
                    if(records.isEmpty()){
                    records.add(timer.getTotalTime());
                    recordLabel.setText(convertTime(timer.getTotalTime()));
                    recordText1.setText("Rec "+records.size());
                    recordCount++;
                }
                else{
                    records.add(timer.getTotalTime());
                    long first = (long)records.get(records.size()-2);
                    long second = (long)records.get(records.size()-1);
                    long difference = second - first;
                    switch(recordCount){
                        case 0:
                            recordLabel.setText(convertTime(difference));
                            recordText1.setText("Rec "+records.size());
                            break;
                        case 1:
                            recordLabel2.setText(convertTime(difference));
                            recordText2.setText("Rec "+records.size());
                            break;
                        case 2:
                            recordLabel3.setText(convertTime(difference));
                            recordText3.setText("Rec "+records.size());
                            break;
                    }
                    recordCount++;
                    if(recordCount > 2){
                        recordCount = 0;
                    }
                }
            }

        }
    });
        
        AnimationTimer tracker;
        tracker = new AnimationTimer(){
            @Override
            public void handle(long now) {
                long startTime = timer.getTotalStartTime();
                long timeLeft = timer.getTargetMilli() - startTime;
                timeDisplay.setText(convertTime(startTime));
                timerLabel.setText(convertTime(timeLeft));
                
            }
        };
        tracker.start();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
