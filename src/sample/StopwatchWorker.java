package sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

public class StopwatchWorker extends Task<Void> {

    private BooleanProperty stop = new SimpleBooleanProperty(false);
    private LocalTime startDateTime;
    private LocalTime stopDateTime;
    private Label label;
    private Label label101;
    private Label label102;
    private Optional<String> startTime;

    StopwatchWorker(Label label, Optional<String> startTime) {
        this.label = label;
        this.startTime = startTime;
        parseStartTime(startTime);
    }

    private void parseStartTime(Optional<String> startTime) {
        try {
            String[] split = startTime.get().split(":");
            startDateTime = LocalTime.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), 0);
        } catch (Exception exception) {
            startDateTime = LocalTime.now();
        }

    }


    @Override
    protected Void call() throws Exception {

        while (!stop.getValue()) {
            stopDateTime = LocalTime.now();
            Duration d = Duration.between(startDateTime, stopDateTime);

            long hours = Math.max(0, d.toHours());
            long minutes = Math.max(0, d.toMinutes() - 60 * d.toHours());
            long seconds = Math.max(0, d.getSeconds() - 60 * d.toMinutes());


            if (d.toMinutes() < 462) {
                label.setTextFill(Color.DARKORANGE);
            } else if (d.toMinutes() >= 462 && d.toMinutes() < 600) {
                label.setTextFill(Color.GREEN);
            } else if (d.toMinutes() >= 600 && d.toMinutes() < 720) {
                label.setTextFill(Color.RED);

            } else if (d.toMinutes() >= 720) {
                label.setTextFill(Color.DARKRED);

            }

            updateMessage(String.format("%02d", hours) + ":" + String.format("%02d", minutes));

            Thread.sleep(3);
        }
        return null;
    }


    public void setStop(Boolean stop) {
        label.setText("00:00");
        this.stop.setValue(stop);
    }


    public void stop() {
        setStop(true);
    }

    public void setStartDateTime(LocalTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
