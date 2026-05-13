package GUI;

import Networking.picoPacket;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class appGUI extends Application implements Observer<appModel, String> {
    private appModel model;
    private Label mssgLabel;
    private final double SP = 0;
    private double pv = 0;
    private double err = 10;
    private double corr = 10;
    private XYChart.Series<Number, Number> errTimSeries;
    private XYChart.Series<Number, Number> pvTimSeries;
    private XYChart.Series<Number, Number> corrTimSeries;
    private Slider KpSlider;
    private Slider KiSlider;
    private Slider KdSlider;
    private NumberAxis xAxiserr = new NumberAxis();
    private NumberAxis xAxispv = new NumberAxis();
    private NumberAxis xAxisCorr = new NumberAxis();
    private double currTime = 0;
    private int upperBound = 60;

    public void init() {
        this.model = new appModel();
        this.model.addObserver(this);

    }

    public void start(Stage stage) {
        BorderPane mainBorderPane = new BorderPane();

        // Top part of the GUI
        HBox topHBox = new HBox();
        topHBox.setSpacing(40);
        // topHBox.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("Welcome to the PID controller!");
        Label spLabel = new Label("Setpoint : " + SP);
        Label pvLabel = new Label("Process Value : " + this.pv);
        Label errLabel = new Label("Error: " + this.err);
        Label timeLabel = new Label("Elapsed time: " + currTime + "s");


        Timeline tm = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            currTime += 20.0 / 1000.0;
            if ((int) currTime >= this.upperBound)  {
                this.upperBound += 60;
                
                this.xAxiserr.setUpperBound(this.upperBound);
                this.xAxiserr.setLowerBound(this.upperBound - 60);

                this.xAxispv.setUpperBound(this.upperBound);
                this.xAxispv.setLowerBound(this.upperBound - 60);

                this.xAxisCorr.setUpperBound(this.upperBound);
                this.xAxisCorr.setLowerBound(this.upperBound - 60);

            }
            picoPacket packet = this.model.getLatestPacket();

            if (packet != null) {
                this.pv = packet.pv();
                this.err = packet.err();
                this.corr = packet.corr();


                pvLabel.setText("Process Value : " + this.pv);
                errLabel.setText("Error: " + this.err);
                timeLabel.setText("Elapsed time: " + (int) currTime + "s");

                this.errTimSeries.getData().add(new XYChart.Data<>(currTime, this.err));
                if (this.errTimSeries.getData().size() > 200) this.errTimSeries.getData().remove(0);

                this.pvTimSeries.getData().add(new XYChart.Data<>(currTime, this.pv));
                if (this.pvTimSeries.getData().size() > 200) this.pvTimSeries.getData().remove(0);

                this.corrTimSeries.getData().add(new XYChart.Data<>(currTime, this.corr));
                if (this.corrTimSeries.getData().size() > 200) this.corrTimSeries.getData().remove(0);
            }

        }));
        tm.setCycleCount(Timeline.INDEFINITE);

        topHBox.getChildren().addAll(statusLabel, spLabel, pvLabel, errLabel, timeLabel);

        statusLabel.setAlignment(Pos.CENTER);
        spLabel.setAlignment(Pos.CENTER);
        pvLabel.setAlignment(Pos.CENTER);
        errLabel.setAlignment(Pos.CENTER);
        timeLabel.setAlignment(Pos.CENTER);

        mainBorderPane.setTop(topHBox);

        // Middle part of the GUI
        VBox graphBox = new VBox();

        NumberAxis yAxiserr = new NumberAxis();
        this.xAxiserr.setLabel("Time [s]");
        yAxiserr.setLabel("Error");

        NumberAxis yAxispv = new NumberAxis();
        this.xAxispv.setLabel("Time [s]");
        yAxispv.setLabel("Process Var");

        NumberAxis yAxisCorr = new NumberAxis();
        this.xAxisCorr.setLabel("Time [s]");
        yAxisCorr.setLabel("Correction");

        // For the axis numbers and tick count
        this.xAxiserr.setAutoRanging(false);
        this.xAxiserr.setUpperBound(60);
        this.xAxiserr.setTickUnit(10);
        yAxiserr.setAutoRanging(false);
        yAxiserr.setUpperBound(30);
        yAxiserr.setTickUnit(10);

        this.xAxispv.setAutoRanging(false);
        this.xAxispv.setUpperBound(60);
        this.xAxispv.setTickUnit(10);
        yAxispv.setAutoRanging(false);
        yAxispv.setUpperBound(50);
        yAxispv.setTickUnit(30);

        this.xAxisCorr.setAutoRanging(false);
        this.xAxisCorr.setUpperBound(60);
        this.xAxisCorr.setTickUnit(10);
        yAxisCorr.setAutoRanging(false);
        yAxisCorr.setUpperBound(50);
        yAxisCorr.setTickUnit(30);

        LineChart<Number, Number> errChart = new LineChart<>(xAxiserr, yAxiserr);
        this.errTimSeries = new XYChart.Series<>();
        errChart.getData().add(this.errTimSeries);
        graphBox.getChildren().add(errChart);
        errChart.setPrefHeight(50);

        LineChart<Number, Number> pvChart = new LineChart<>(xAxispv, yAxispv);
        this.pvTimSeries = new XYChart.Series<>();
        pvChart.getData().add(this.pvTimSeries);
        graphBox.getChildren().add(pvChart);
        pvChart.setPrefHeight(50);

        LineChart<Number, Number> corrChart = new LineChart<>(xAxisCorr, yAxisCorr);
        this.corrTimSeries = new XYChart.Series<>();
        corrChart.getData().add(this.corrTimSeries);
        graphBox.getChildren().add(corrChart);
        corrChart.setPrefHeight(50);

        mainBorderPane.setCenter(graphBox);

        // The left side (sliders)
        VBox mainSliderVBox = new VBox();

        VBox KpSliderVbox = new VBox();
        this.KpSlider = new Slider(0, 10, 5);
        KpSlider.setShowTickLabels(true);
        Label KpSliderLabel = new Label("Kp                                       " + KpSlider.getValue());
        KpSliderLabel.setTextFill(Color.BLUE);
        KpSliderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        KpSliderVbox.getChildren().addAll(KpSliderLabel, KpSlider);

        VBox KiSliderVbox = new VBox();
        this.KiSlider = new Slider(0.00, 10.0, 5.0);
        KiSlider.setShowTickLabels(true);
        Label KiSliderLabel = new Label("Ki                                       " + KiSlider.getValue());
        KiSliderLabel.setTextFill(Color.BLUE);
        KiSliderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        KiSliderVbox.getChildren().addAll(KiSliderLabel, KiSlider);

        VBox KdSliderVbox = new VBox();
        this.KdSlider = new Slider(0, 10, 5);
        KdSlider.setShowTickLabels(true);
        Label KdSliderLabel = new Label("Kd                                       " + KdSlider.getValue());
        KdSliderLabel.setTextFill(Color.BLUE);
        KdSliderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        KdSliderVbox.getChildren().addAll(KdSliderLabel, KdSlider);

        Button rstButton = new Button("Reset");
        rstButton.setAlignment(Pos.CENTER);

        mainSliderVBox.setAlignment(Pos.CENTER);
        mainSliderVBox.setSpacing(50);
        mainSliderVBox.getChildren().addAll(KpSliderVbox, KiSliderVbox, KdSliderVbox, rstButton);

        mainBorderPane.setLeft(mainSliderVBox);

        StackPane botStackPane = new StackPane();
        this.mssgLabel = new Label("All status update's will be dispalyed here!");
        mssgLabel.setTextFill(Color.BLUE);
        mssgLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        mssgLabel.setAlignment(Pos.CENTER);
        botStackPane.getChildren().add(mssgLabel);
        botStackPane.setAlignment(Pos.CENTER);

        mainBorderPane.setBottom(botStackPane);

        // Controller section
        rstButton.setOnAction(e -> {
            this.model.reset();
        });

        this.KpSlider.setOnMouseReleased(e -> {
            this.model.send("Kp", this.KpSlider.getValue());
        });

        this.KiSlider.setOnMouseReleased(e -> {
            this.model.send("Ki",this.KiSlider.getValue());
        });

        this.KdSlider.setOnMouseReleased(e -> {
            this.model.send("Kd", this.KdSlider.getValue());
        });

        tm.play();
        Scene mainScene = new Scene(mainBorderPane);
        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void update(appModel subject, String data) {
        this.mssgLabel.setText(data);
        this.KpSlider.setValue(subject.getKpVal());
        this.KiSlider.setValue(subject.getKiVal());
        this.KdSlider.setValue(subject.getKdVal());
    }
}
