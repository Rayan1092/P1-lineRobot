package GUI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class appGUI extends Application {
    private final double SP = 0;
    private double pv = 0;
    private double err = 10;
    private double corr = 10;
    private XYChart.Series<Number, Number> errTimSeries;
    private XYChart.Series<Number, Number> pvTimSeries;
    private XYChart.Series<Number, Number> corrTimSeries;

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
            this.errTimSeries.getData().add(new XYChart.Data<>(currTime, this.err));
            this.pvTimSeries.getData().add(new XYChart.Data<>(currTime, this.pv));
            this.corrTimSeries.getData().add(new XYChart.Data<>(currTime, this.corr));

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

        NumberAxis xAxiserr = new NumberAxis();
        NumberAxis yAxiserr = new NumberAxis();
        xAxiserr.setLabel("Time [s]");
        yAxiserr.setLabel("Error");

        NumberAxis xAxispv = new NumberAxis();
        NumberAxis yAxispv = new NumberAxis();
        xAxispv.setLabel("Time [s]");
        yAxispv.setLabel("Process Variable");

        NumberAxis xAxisCorr = new NumberAxis();
        NumberAxis yAxisCorr = new NumberAxis();
        xAxisCorr.setLabel("Time [s]");
        yAxisCorr.setLabel("Correction");

        // For the axis numbers and tick count
        xAxiserr.setAutoRanging(false);
        xAxiserr.setUpperBound(60);
        xAxiserr.setTickUnit(10);
        yAxiserr.setAutoRanging(false);
        yAxiserr.setUpperBound(30);
        yAxiserr.setTickUnit(10);

        xAxispv.setAutoRanging(false);
        xAxispv.setUpperBound(60);
        xAxispv.setTickUnit(10);
        yAxispv.setAutoRanging(false);
        yAxispv.setUpperBound(600);
        yAxispv.setTickUnit(30);

        xAxisCorr.setAutoRanging(false);
        xAxisCorr.setUpperBound(60);
        xAxisCorr.setTickUnit(10);
        yAxisCorr.setAutoRanging(false);
        yAxisCorr.setUpperBound(50);
        yAxisCorr.setTickUnit(30);





        LineChart<Number, Number> errChart = new LineChart<>(xAxiserr, yAxiserr);
        this.errTimSeries = new XYChart.Series<>();
        errChart.getData().add(this.errTimSeries);
        graphBox.getChildren().add(errChart);


        LineChart<Number, Number>  pvChart = new LineChart<>(xAxispv, yAxispv);
        this.pvTimSeries = new XYChart.Series<>();
        pvChart.getData().add(this.pvTimSeries);
        graphBox.getChildren().add(pvChart);

        LineChart<Number, Number>  corrChart = new LineChart<>(xAxisCorr, yAxisCorr);
        this.corrTimSeries = new XYChart.Series<>();
        corrChart.getData().add(this.corrTimSeries);
        graphBox.getChildren().add(corrChart);


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
