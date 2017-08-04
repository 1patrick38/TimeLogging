package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.Optional;

public class Controller {


    private Optional<String> startTime = Optional.empty();

    private int countdownFiveHours = 18000;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(countdownFiveHours);
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

    @FXML
    private void handleStartStop(ActionEvent event) {
        startCountdowns();

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

        }


    //Setzt das Label für die Kommenzeit.
    public String kommenZeit(){

        char[] charArray = startTime.toString().toCharArray();

        StringBuilder sb = new StringBuilder();
        sb.append(charArray[9]).append(charArray[10]).append(charArray[11]).append(charArray[12]).append(charArray[13]);

        return sb.toString();
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

    // ALL!!!! ids in the fxml MUST start with lowercase!!! So refactor it mr. :)

    public void startCountdowns() {

        l00Countdown.textProperty().bind(timeSeconds.asString());

        if (countdownTLineFirst != null) {
            countdownTLineFirst.stop();
        }
        countdownTLineFirst = new Timeline();
        countdownTLineFirst.getKeyFrames().add(
                new KeyFrame(javafx.util.Duration.seconds(countdownFiveHours + 1),
                        new KeyValue(timeSeconds, 0)));
        countdownTLineFirst.playFromStart();
    }


    private enum StopWatchStatus {
        STOPPED, RUNNING
    }
}
