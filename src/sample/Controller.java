package sample;

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
import util.TimeLineFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {

    private static final int SECONDSOFHOUR = 3600;
    private final static int FIVEHOURSINSECONDS = 18000;
    private final static int SIXHOURSINSECONDS = 21600;
    private final static int EIGHTHOURSTWELVEMINSECONDS = 28812;
    private final static int EIGHTHOURSFOURTYONEINSECONDS = 28841;
    private final static int TENHOURSINSECONDS = 36000;
    private final static int TWELVEHOURSINSECONDS = 43200;
    
    private IntegerProperty fiveHoursCount = new SimpleIntegerProperty();
    private IntegerProperty sixHoursCount = new SimpleIntegerProperty();
    private IntegerProperty eightHoursTwelveMinCount = new SimpleIntegerProperty();
    private IntegerProperty eightHoursFourtyCount = new SimpleIntegerProperty();
    private IntegerProperty tenHoursCount = new SimpleIntegerProperty();
    private IntegerProperty twelveHoursCount = new SimpleIntegerProperty();

    private Timeline countdownFiveHours, countdownSixHours,
            countdownEightTwelveHours,
            countdownEightFourtyHours,
            countdownTenHours, countdownTwelveHours;

    private Optional<String> startTime = Optional.empty();

    @FXML
    private Label l00Countdown,
            l01Countdown,
            l02Countdown,
            l03Countdown,
            l04Countdown,
            l05Countdown;
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


    private static LocalTime parseStringToTime(Optional<String> input) {
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
    }


    public void startCountdowns() {
        LocalTime parsedStartTime = parseStringToTime(startTime);
        int startTime = parsedStartTime.toSecondOfDay();
        int currentTime = LocalTime.now().toSecondOfDay();
        int deltaFiveHours = delta(FIVEHOURSINSECONDS, startTime, currentTime);
        int deltaSixHours = delta(SIXHOURSINSECONDS, startTime, currentTime);
        fiveHoursCount.setValue(deltaFiveHours);
        sixHoursCount.setValue(deltaSixHours);
        bindPropertyToValue(l00Countdown, fiveHoursCount);
        bindPropertyToValue(l01Countdown, sixHoursCount);


        countdownFiveHours = TimeLineFactory.get(deltaFiveHours, fiveHoursCount);
        countdownSixHours = TimeLineFactory.get(deltaSixHours, sixHoursCount);
        countdownFiveHours.playFromStart();
        countdownSixHours.playFromStart();
    }

    private void bindPropertyToValue(Label label, IntegerProperty hoursCount) {
        label.textProperty().bind(Bindings.concat(hoursCount.divide(SECONDSOFHOUR)).concat(":")
                .concat(formatMinutes(hoursCount)));
    }

    private int delta(int duration, int startTime, int currentTime) {
        return duration - (currentTime - startTime);
    }

    // modulo must be implemented, because the property API does not support it....
    // a % b = a - (b * int(a/b))

    private NumberBinding formatMinutes(IntegerProperty hoursCount) {
        IntegerBinding divide = hoursCount.divide(60);
        return divide.subtract((divide.divide(60)).multiply(60));
    }


    private enum StopWatchStatus {
        STOPPED, RUNNING
    }

}
