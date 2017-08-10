package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {


    private Optional<String> startTime = Optional.empty();

    private IntegerProperty fiveHoursCount = new SimpleIntegerProperty(0);
    private Timeline countdownTLineFirst;


    @FXML
    private Label l00Countdown;
    @FXML
    private Label lStopuhr;
    @FXML
    private Button testB;
    @FXML
    private Label lKommenZeit;

    private StopwatchWorker stopwatchWorker;
    private StopWatchStatus currentStatus = StopWatchStatus.STOPPED;

    public static LocalTime parseStringToTime(Optional<String> input) {
        try {
            String[] split = input.get().split(":");
            return LocalTime.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), 0);
        } catch (Exception exception) {
            return LocalTime.now();
        }
    }

    @FXML
    private void handleStartStop(ActionEvent event) {

        TextInputDialog input = new TextInputDialog("Set startCountdowns time: h:m");
        input.setTitle("Set startCountdowns time...");

        input.setHeaderText("Set startCountdowns time mr. primus: ");


        if (!startTime.isPresent()) startTime = input.showAndWait();
        if (currentStatus == StopWatchStatus.STOPPED) {
            currentStatus = StopWatchStatus.RUNNING;
            stopwatchWorker = new StopwatchWorker(lStopuhr, startTime);
            //  stopwatchWorker = new StopwatchWorker(label101, startTime);
            Thread t = new Thread(stopwatchWorker);
            lStopuhr.textProperty().bind(stopwatchWorker.messageProperty());

            t.setDaemon(true);
            t.start();
        }


        lKommenZeit.setText(kommenZeit());
        startCountdowns();

    }

    public String kommenZeit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (startTime.isPresent() && startTime.get().matches("[0-9:]*")) {
            return startTime.get();
        } else return formatter.format(LocalDateTime.now());
    }

    @FXML
    private void resetAction(ActionEvent event) {
        lStopuhr.textProperty().unbind();
        lStopuhr.setTextFill(Color.BLACK);
        lStopuhr.setText("00:00");
        if (currentStatus == StopWatchStatus.RUNNING) {
            stopwatchWorker.stop();
            stopwatchWorker = null;
            startTime = Optional.empty();
            currentStatus = StopWatchStatus.STOPPED;
        }
    }

    // ALL!!!! ids in the fxml MUST start with lowercase!!! So refactor it mr. :)

    @FXML
    private void testButton(ActionEvent event) {
        System.out.println("BLABLABLA");
    }

    public void startCountdowns() {
        LocalTime parsedStartTime = parseStringToTime(startTime);
        int startTime = parsedStartTime.toSecondOfDay();
        int currentTime = LocalTime.now().toSecondOfDay();
        int delta = 18000 - (currentTime - startTime);
        fiveHoursCount.setValue(delta);
        l00Countdown.textProperty().bind(Bindings.concat(fiveHoursCount.divide(3600))
                .concat(":").concat(getMinutesDueModuloFromSeconds(fiveHoursCount)));
  //      l00Countdown.textProperty().bind(fiveHoursCount.asString());

        if (countdownTLineFirst != null) {
            countdownTLineFirst.stop();
        }
        countdownTLineFirst = new Timeline();
        countdownTLineFirst.getKeyFrames().add(
                new KeyFrame(javafx.util.Duration.seconds(delta + 1),
                        new KeyValue(fiveHoursCount, 0)));
        countdownTLineFirst.playFromStart();
    }

    private IntegerProperty getMinutesDueModuloFromSeconds(IntegerProperty fiveHoursCount) {
        int seconds = fiveHoursCount.intValue();
        int i = ((seconds / 60) % 60);
        fiveHoursCount.setValue(i);
        return fiveHoursCount;
    }


    private enum StopWatchStatus {
        STOPPED, RUNNING
    }
}
