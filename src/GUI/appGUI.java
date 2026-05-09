package GUI;

import java.util.concurrent.TimeoutException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class appGUI extends Application {
    private final double SP = 0;
    private double pv = 0;
    private double err = 100;
    private XYChart.Series<Number, Number> errTimSeries;

    private double currTime = 0;

    public void init() {
    }

    public void start(Stage stage) {
        BorderPane maiBorderPane = new BorderPane();

        // Top part of the GUI
        HBox topHBox = new HBox();
        topHBox.setSpacing(10);

        Label statusLabel = new Label("Welcome to the PID controller!");
        Label spLabel = new Label("Setpoint : " + SP);
        Label pvLabel = new Label("Process Value : " + pv);
        Label errLabel = new Label("Error: " + err);
        Label timeLabel = new Label("Elapsed time: " + currTime + "s");

        Timeline tm = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            currTime += 20.0 / 1000.0;
            timeLabel.setText("Elapsed time: " + (int) currTime + "s");
            this.errTimSeries.getData().add(new XYChart.Data<>(currTime, err));

        }));
        tm.setCycleCount(Timeline.INDEFINITE);

        topHBox.getChildren().addAll(statusLabel, spLabel, pvLabel, errLabel, timeLabel);

        statusLabel.setAlignment(Pos.CENTER);
        spLabel.setAlignment(Pos.CENTER);
        pvLabel.setAlignment(Pos.CENTER);
        errLabel.setAlignment(Pos.CENTER);
        timeLabel.setAlignment(Pos.CENTER);

        maiBorderPane.setTop(topHBox);

        // Middle part of the GUI
        VBox graphBox = new VBox();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time [s]");
        yAxis.setLabel("Error");

        LineChart<Number, Number> errChart = new LineChart<>(xAxis, yAxis);
        this.errTimSeries = new XYChart.Series<>();
        errChart.getData().add(errTimSeries);
        graphBox.getChildren().add(errChart);

        maiBorderPane.setCenter(graphBox);

        tm.play();
        Scene mainScene = new Scene(maiBorderPane);
        stage.setScene(mainScene);
        stage.show();
    }

    public void timeInfo() {

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
