package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {


    private Optional<String> startTime = Optional.empty();

    private IntegerProperty fiveHoursCount = new SimpleIntegerProperty(0);
    private Timeline countdownFiveHours;
    private final static int FIVEHOURSINSECONDS =18000;


    @FXML
    private Label l00Countdown;
    @FXML
    private Label lStopuhr;
    @FXML
    private Button testB;
    @FXML
    private Label lKommenZeit;
    @FXML
    private Label lGehenZeit;

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
        lGehenZeit.setText(gehenZeit());
        startCountdowns();

    }

    public String kommenZeit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (startTime.isPresent() && startTime.get().matches("[0-9:]*")) {
            return startTime.get();
        } else return formatter.format(LocalDateTime.now());
    }

    public String gehenZeit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (startTime.isPresent() && startTime.get().matches("[0-9:]*")) {

            System.out.println(startTime);
            String[] split = startTime.get().split(":");
            // startTime = Optional.of(LocalTime.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), 0).toString());

            LocalTime maximalWorking = LocalTime.now().with(LocalTime.of(Integer.valueOf(split[0]), Integer.valueOf(split[1])));
            maximalWorking.plusHours(10).plusMinutes(30);

            System.out.println(maximalWorking);

            return formatter.format(maximalWorking.plusHours(10).plusMinutes(30));
        } else return formatter.format(LocalDateTime.now().plusHours(10).plusMinutes(30));
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

    @FXML
    private void testButton(ActionEvent event) {
        System.out.println("BLABLABLA");
    }


    // modulo must be implemented, because the property API does not support it....
    // a % b = a - (b * int(a/b))
    public void startCountdowns() {
        LocalTime parsedStartTime = parseStringToTime(startTime);
        int startTime = parsedStartTime.toSecondOfDay();
        int currentTime = LocalTime.now().toSecondOfDay();
        int deltaFiveHours = deltaFiveHours(startTime, currentTime);
        fiveHoursCount.setValue(deltaFiveHours);
        l00Countdown.textProperty().bind(Bindings.concat(fiveHoursCount.divide(3600)).concat(":")
                .concat(formatMinutes(fiveHoursCount)));

        if (countdownFiveHours != null) {
            countdownFiveHours.stop();
        }
        countdownFiveHours = new Timeline();
        countdownFiveHours.getKeyFrames().add(
                new KeyFrame(javafx.util.Duration.seconds(deltaFiveHours + 1),
                        new KeyValue(fiveHoursCount, 0)));
        countdownFiveHours.playFromStart();
    }

    private int deltaFiveHours(int startTime, int currentTime) {
        return FIVEHOURSINSECONDS - (currentTime - startTime);
    }

    private NumberBinding formatMinutes(IntegerProperty fiveHoursCount) {
        IntegerBinding divide = fiveHoursCount.divide(60);
        return divide.subtract((divide.divide(60)).multiply(60));
    }

    private enum StopWatchStatus {
        STOPPED, RUNNING
    }


}
