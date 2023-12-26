package me.croshaw.carservicesimulation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import me.croshaw.carservicesimulation.simulation.SimulationController;
import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class CreateController implements Initializable {
    @FXML
    private ComboBox<String> durationSimUNIT;
    @FXML
    private ComboBox<String> maxServiceDurUNIT;
    @FXML
    private ComboBox<String> maxWaitDurUNIT;
    @FXML
    private ComboBox<String> fromUnit;
    @FXML
    private ComboBox<String> toUnit;
    @FXML
    private TextField seed;
    @FXML
    private TextField durationSimulation;
    @FXML
    private TextField maxDurationService;
    @FXML
    private TextField maxDurationWaiting;
    @FXML
    private TextField requestDistributionFrom;
    @FXML
    private TextField requestDistributionTo;
    @FXML
    private TextField minSalary;
    @FXML
    private TextField profitShare;
    @FXML
    private Button createButton;
    private MainController parent;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        durationSimUNIT.getItems().addAll("Дни", "Месяца");
        durationSimUNIT.setValue("Дни");
        maxServiceDurUNIT.getItems().addAll("Часы", "Дни", "Месяца");
        maxServiceDurUNIT.setValue("Часы");
        maxWaitDurUNIT.getItems().addAll("Часы", "Дни", "Месяца");
        maxWaitDurUNIT.setValue("Часы");
        fromUnit.getItems().addAll("Секунды", "Минуты", "Часы");
        fromUnit.setValue("Секунды");
        toUnit.getItems().addAll("Секунды", "Минуты", "Часы");
        toUnit.setValue("Секунды");

        makeTextFieldForOnlyNumber(durationSimulation);
        makeTextFieldForOnlyNumber(maxDurationService);
        makeTextFieldForOnlyNumber(maxDurationWaiting);
        makeTextFieldForOnlyNumber(requestDistributionFrom);
        makeTextFieldForOnlyNumber(requestDistributionTo);
        makeTextFieldForOnlyNumber(minSalary);
        makeTextFieldForOnlyNumber(profitShare);
    }
    public Duration getDuration(long value, String str) {
        return switch (str) {
            case ("Минуты") -> Duration.ofMinutes(value);
            case ("Часы") -> Duration.ofHours(value);
            case ("Дни") -> Duration.ofDays(value);
            case ("Месяца") -> Duration.ofDays(value * 30);
            default -> Duration.ofSeconds(value);
        };
    }
    @FXML
    protected void onDefSettingsButtonClick() {
        seed.setText("0");
        durationSimulation.setText("7");
        durationSimUNIT.setValue("Дни");
        maxDurationService.setText("7");
        maxServiceDurUNIT.setValue("Дни");
        maxDurationWaiting.setText("7");
        maxWaitDurUNIT.setValue("Дни");
        requestDistributionFrom.setText("15");
        fromUnit.setValue("Минуты");
        requestDistributionTo.setText("1");
        toUnit.setValue("Часы");
        minSalary.setText("1500");
        profitShare.setText("35");
    }
    @FXML
    protected void onCreateButtonClick() throws IOException {
        if(isTextFieldsEmpty(seed, durationSimulation, maxDurationService, maxDurationWaiting
                , requestDistributionFrom, requestDistributionTo, minSalary, profitShare)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Заполните поля", ButtonType.OK);
            alert.show();
            return;
        }
        var seed = Integer.parseInt(this.seed.getText());
        var durSim = getDuration(Long.parseLong(durationSimulation.getText()), durationSimUNIT.getValue());
        var maxServ = getDuration(Long.parseLong(maxDurationService.getText()), maxServiceDurUNIT.getValue());
        var maxWait = getDuration(Long.parseLong(maxDurationWaiting.getText()), maxWaitDurUNIT.getValue());
        var from = getDuration(Long.parseLong(requestDistributionFrom.getText()), fromUnit.getValue());
        var to = getDuration(Long.parseLong(requestDistributionTo.getText()), toUnit.getValue());
        double minSal = Double.parseDouble(minSalary.getText());
        float proShar = Math.max(0, Math.min(100, Integer.parseInt(profitShare.getText()))) / 100.f;
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Parent root = loader.load();
        parent.createSimulationCallback(seed, durSim
                , 3600, new ValueRange<>(from.toSeconds(), to.toSeconds()), maxServ, maxWait, minSal, proShar);
        ((Stage)createButton.getScene().getWindow()).close();
    }
    @FXML
    protected void onCancelButtonClick() {
        ((Stage)createButton.getScene().getWindow()).close();
    }
    private boolean isTextFieldsEmpty(TextField ... textFields) {
        for(TextField t : textFields)
            if(t.getText().isEmpty() || t.getText().isBlank())
                return true;
        return false;
    }
    private void makeTextFieldForOnlyNumber(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setParent(MainController mainController) {
        this.parent = mainController;
    }
}
