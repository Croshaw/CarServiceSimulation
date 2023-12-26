package me.croshaw.carservicesimulation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.croshaw.carservicesimulation.simulation.SimulationController;
import me.croshaw.carservicesimulation.simulation.core.util.DurationHelper;
import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private FlowPane simulationPanel;
    @FXML
    protected void onStartNewSimulationButtonClick() throws Exception {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("create-new-simulation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Создать");
        stage.setScene(scene);
        stage.show();
        CreateController createController = fxmlLoader.getController();
        createController.setParent(this);
    }
    public void loadSimulation(SimulationController simulationController) {
        Slider speedSlider = new Slider();
        speedSlider.setMax(1);
        speedSlider.setMin(0.0001);
        speedSlider.setValue(simulationController.getSpeed());
        Label speedInfo = new Label("Скорость");
        Label speedMonitor = new Label();
        speedMonitor.setText(speedSlider.valueProperty().getValue().toString());

        VBox speedBox = new VBox(speedInfo, speedSlider, speedMonitor);
        speedBox.setAlignment(Pos.CENTER);
        Slider stepSlider = new Slider();
        stepSlider.setMax(3600);
        stepSlider.setMin(1);
        stepSlider.setValue(simulationController.getSecondsStep());
        Label stepInfo = new Label("Шаг");
        Label stepMonitor = new Label();
        stepMonitor.setText(DurationHelper.toString(Duration.ofSeconds(stepSlider.valueProperty().getValue().longValue())));
        VBox stepBox = new VBox(stepInfo, stepSlider, stepMonitor);
        stepBox.setAlignment(Pos.CENTER);
        Canvas newCanvas = new Canvas(simulationPanel.getPrefWidth(), simulationPanel.getPrefHeight());
        simulationController.pause();
        simulationController.setCanvas(newCanvas);
        speedSlider.valueProperty().addListener((observableValue, number, t1) -> {
            speedMonitor.setText(t1.toString());
            simulationController.setSpeed(t1.doubleValue());
        });
        stepSlider.valueProperty().addListener((observableValue, number, t1) -> {
            stepMonitor.setText(DurationHelper.toString(Duration.ofSeconds(t1.longValue())));
            simulationController.setStep(t1.longValue());
        });
        Button startButton = new Button("Запуск");
        Button stopButton = new Button("Завершить");
        Button pauseButton = new Button("Пауза");
        Button saveButton = new Button("Сохранить");

        saveButton.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(simulationPanel.getScene().getWindow());
            if(file != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()));
                    writer.write(SimulationController.serialize(simulationController));
                    writer.close();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Что-то пошло не так!");
                    alert.setTitle("Ошибка");
                }
            }
        });

        saveButton.visibleProperty().setValue(false);
        HBox hBox = new HBox(speedBox, pauseButton, startButton, stopButton, stepBox, saveButton);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(newCanvas, hBox);
        simulationPanel.getChildren().add(vBox);
        VBox.setVgrow(newCanvas, Priority.ALWAYS);
        startButton.setOnMouseClicked(mouseEvent -> {
            stopButton.visibleProperty().setValue(true);
            pauseButton.visibleProperty().setValue(true);
            startButton.visibleProperty().setValue(false);
            saveButton.visibleProperty().setValue(true);
            simulationController.setStep(stepSlider.valueProperty().getValue().longValue());
            simulationController.setSpeed(speedSlider.valueProperty().doubleValue());
            simulationController.start();
        });
        pauseButton.setOnMouseClicked(mouseEvent -> {
            if(pauseButton.getText().equals("Пауза")) {
                pauseButton.setText("Возобновить");
                simulationController.pause();
            } else {
                pauseButton.setText("Пауза");
                simulationController.resume();
            }
        });
        stopButton.setOnMouseClicked(mouseEvent -> {
            stopButton.visibleProperty().setValue(false);
            pauseButton.visibleProperty().setValue(false);
            startButton.visibleProperty().setValue(true);
            simulationPanel.getChildren().remove(vBox);
        });

        pauseButton.visibleProperty().setValue(true);
        stopButton.visibleProperty().setValue(true);
        startButton.visibleProperty().setValue(false);
        saveButton.visibleProperty().setValue(true);
        simulationController.setStep(stepSlider.valueProperty().getValue().longValue());
        simulationController.setSpeed(speedSlider.valueProperty().doubleValue());
        simulationController.start();
        simulationController.resume();
    }
    public void createNewSimulation(LocalDateTime time, int seed, Duration durationOfSimulation, long secondsStep, ValueRange<Long> requestDistributionInterval
            , Duration maxServicingDuration, Duration maxWaitingDuration, double minSalary, float profitShare) {
        Slider speedSlider = new Slider();
        speedSlider.setMax(1);
        speedSlider.setMin(0.0001);
        speedSlider.setValue(0.01);
        Label speedInfo = new Label("Скорость");
        Label speedMonitor = new Label();
        speedMonitor.setText(speedSlider.valueProperty().getValue().toString());

        VBox speedBox = new VBox(speedInfo, speedSlider, speedMonitor);
        speedBox.setAlignment(Pos.CENTER);
        Slider stepSlider = new Slider();
        stepSlider.setMax(3600);
        stepSlider.setMin(1);
        stepSlider.setValue(600);
        Label stepInfo = new Label("Шаг");
        Label stepMonitor = new Label();
        stepMonitor.setText(DurationHelper.toString(Duration.ofSeconds(stepSlider.valueProperty().getValue().longValue())));
        VBox stepBox = new VBox(stepInfo, stepSlider, stepMonitor);
        stepBox.setAlignment(Pos.CENTER);
        Canvas newCanvas = new Canvas(simulationPanel.getPrefWidth(), simulationPanel.getPrefHeight());
        SimulationController simulationController = new SimulationController(LocalDateTime.now(), seed, durationOfSimulation
                , 3600, requestDistributionInterval, maxServicingDuration, maxWaitingDuration, minSalary, profitShare, newCanvas);
        speedSlider.valueProperty().addListener((observableValue, number, t1) -> {
            speedMonitor.setText(t1.toString());
            simulationController.setSpeed(t1.doubleValue());
        });
        stepSlider.valueProperty().addListener((observableValue, number, t1) -> {
            stepMonitor.setText(DurationHelper.toString(Duration.ofSeconds(t1.longValue())));
            simulationController.setStep(t1.longValue());
        });
        Button startButton = new Button("Запуск");
        Button stopButton = new Button("Завершить");
        Button pauseButton = new Button("Пауза");
        Button saveButton = new Button("Сохранить");

        saveButton.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(simulationPanel.getScene().getWindow());
            if(file != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()));
                    writer.write(SimulationController.serialize(simulationController));
                    writer.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Что-то пошло не так!");
                    alert.setTitle("Ошибка");
                }
            }
        });

        saveButton.visibleProperty().setValue(false);
        HBox hBox = new HBox(speedBox, pauseButton, startButton, stopButton, stepBox, saveButton);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(newCanvas, hBox);
        simulationPanel.getChildren().add(vBox);
        VBox.setVgrow(newCanvas, Priority.ALWAYS);
        startButton.setOnMouseClicked(mouseEvent -> {
            stopButton.visibleProperty().setValue(true);
            pauseButton.visibleProperty().setValue(true);
            startButton.visibleProperty().setValue(false);
            saveButton.visibleProperty().setValue(true);
            simulationController.setStep(stepSlider.valueProperty().getValue().longValue());
            simulationController.setSpeed(speedSlider.valueProperty().doubleValue());
            simulationController.start();
        });
        pauseButton.setOnMouseClicked(mouseEvent -> {
            if(pauseButton.getText().equals("Пауза")) {
                pauseButton.setText("Возобновить");
                simulationController.pause();
            } else {
                pauseButton.setText("Пауза");
                simulationController.resume();
            }
        });
        stopButton.setOnMouseClicked(mouseEvent -> {
            stopButton.visibleProperty().setValue(false);
            pauseButton.visibleProperty().setValue(false);
            startButton.visibleProperty().setValue(true);
            simulationPanel.getChildren().remove(vBox);
        });

        pauseButton.visibleProperty().setValue(false);
        stopButton.visibleProperty().setValue(false);
    }
    @FXML
    protected void onLoadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(simulationPanel.getScene().getWindow());
        if(file != null) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
                loadSimulation(SimulationController.deserialize(json));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Что-то пошло не так!");
                alert.setTitle("Ошибка");
            }
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        simulationPanel.setAlignment(Pos.TOP_CENTER);
        simulationPanel.setHgap(10);
        simulationPanel.setVgap(10);
        simulationPanel.setOrientation(Orientation.HORIZONTAL);
    }
    public void createSimulationCallback(int seed, Duration durationOfSimulation, long secondsStep, ValueRange<Long> requestDistributionInterval
            , Duration maxServicingDuration, Duration maxWaitingDuration, double minSalary, float profitShare) {
        createNewSimulation(LocalDateTime.now(), seed, durationOfSimulation
                , 3600, requestDistributionInterval, maxServicingDuration, maxWaitingDuration, minSalary, profitShare);
    }
}