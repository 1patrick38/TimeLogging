package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

import java.util.Optional;

public class Controller {
    private enum StopWatchStatus {
        STOPPED, RUNNING
    }

    Optional<String> startTime = null;

    @FXML
    private Label label;

    @FXML
    private Label label101;
    @FXML
    private Label label102;

    private StopwatchWorker stopwatchWorker;
    private StopWatchStatus currentStatus = StopWatchStatus.STOPPED;


    @FXML
    private Button testB;


    @FXML
    private void handleStartStop(ActionEvent event) {

        TextInputDialog input = new TextInputDialog("Set start time: h:m");
        input.setTitle("Set start time...");
        input.setHeaderText("Set start time mr. primus: ");
        if (startTime == null) startTime = input.showAndWait();
        if (currentStatus == StopWatchStatus.STOPPED) {
            currentStatus = StopWatchStatus.RUNNING;
            stopwatchWorker = new StopwatchWorker(label, startTime);
            //  stopwatchWorker = new StopwatchWorker(label101, startTime);
            Thread t = new Thread(stopwatchWorker);
            label.textProperty().bind(stopwatchWorker.messageProperty());
            label101.textProperty().bind(stopwatchWorker.messageProperty());
            label102.textProperty().bind(stopwatchWorker.messageProperty());

            t.setDaemon(true);
            t.start();
        }

    }


    @FXML
    private void resetAction(ActionEvent event) {
        label.textProperty().unbind();
        label.setTextFill(Color.BLACK);
        label.setText("00:00");
        label101.setText("00:00");
        label102.setText("00:00");
        if (currentStatus == StopWatchStatus.RUNNING) {
            stopwatchWorker.stop();
            stopwatchWorker = null;
            startTime = null;
            currentStatus = StopWatchStatus.STOPPED;
        }
    }

    @FXML
    private void testButton(ActionEvent event) {
        System.out.println("BLABLABLA");
    }
}


