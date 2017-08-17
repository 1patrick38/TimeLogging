package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import util.DataBase;
import util.TimeLineFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Optional;

public class Controller {

    private static final int SECONDSOFHOUR = 3600;
    private final static int FIVEHOURSINSECONDS = 18000;
    private final static int SIXHOURSINSECONDS = 21600;
    private final static int EIGHTHOURSTWELVEMINSECONDS = 29520;
    private final static int EIGHTHOURSFOURTYONEINSECONDS = 31260;
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
            countdownTenHours,
            countdownTwelveHours;

    private Optional<String> startTime = Optional.empty();

    @FXML
    private Label l00Countdown,
            l01Countdown,
            l02Countdown,
            l03Countdown,
            l04Countdown,
            l05Countdown;
    @FXML
    private ProgressBar pB10Countdown,
            pB11Countdown,
            pB12Countdown,
            pB13Countdown,
            pB14Countdown,
            pB15Countdown;
    @FXML
    private Label lStopuhr;
    @FXML
    private Button testB;
    @FXML
    private Label lKommenZeit;
    @FXML
    private Label lGehenZeit;
    @FXML
    private TableView<TimeRecord> table;
    @FXML
    private TableColumn<TimeRecord, String> tableDate;
    @FXML
    private TableColumn<TimeRecord, String> tableKommen;
    @FXML
    private TableColumn<TimeRecord, String> tableGehen;
    @FXML
    private TableColumn<TimeRecord, String> tableZeit;

    private StopwatchWorker stopwatchWorker;
    private StopWatchStatus currentStatus = StopWatchStatus.STOPPED;

    @FXML
    private Label lUhrzeit;


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

    public void initClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            lUhrzeit.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public String kommenZeit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (startTime.isPresent() && startTime.get().matches("[0-9:]*")) {
            return startTime.get();
        } else return formatter.format(LocalDateTime.now());
    }

    // hol dir die Zeit direct von der parseStringToTime methode. Der if zweig hier darf nur eine Zeile lang sein ;)
    // In dieser Klasse wird DateTimeFormatter zweimal genau gleich instanziert --> hier kann man refactoren
    // die Prüfung im if von kommenZeit ist 1:1 wie die von gehenZeit --> indiz dass man hier was zusammenfassen kann
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
        int deltaEightTwelveHours = delta(EIGHTHOURSTWELVEMINSECONDS, startTime, currentTime);
        int deltaEightFourtyHours = delta(EIGHTHOURSFOURTYONEINSECONDS, startTime, currentTime);
        int deltaTenHours = delta(TENHOURSINSECONDS, startTime, currentTime);
        int deltaTwelveHours = delta(TWELVEHOURSINSECONDS, startTime, currentTime);

        setPropertyInitialValues(deltaFiveHours,
                deltaSixHours,
                deltaEightTwelveHours,
                deltaEightFourtyHours,
                deltaTenHours,
                deltaTwelveHours);

        createTimeLines(deltaFiveHours,
                deltaSixHours,
                deltaEightTwelveHours,
                deltaEightFourtyHours,
                deltaTenHours,
                deltaTwelveHours);

        fiveHoursCount.addListener(observable -> {
            pB10Countdown.setProgress((1.0 - fiveHoursCount.get() / (FIVEHOURSINSECONDS * 1.0)));
            setLabelText(l00Countdown, fiveHoursCount.get());
        });
        sixHoursCount.addListener(observable -> {
            pB11Countdown.setProgress((1.0 - sixHoursCount.get() / (SIXHOURSINSECONDS * 1.0)));
            setLabelText(l01Countdown, sixHoursCount.get());
        });
        eightHoursTwelveMinCount.addListener(observable -> {
            pB12Countdown.setProgress((1.0 - eightHoursTwelveMinCount.get() / (EIGHTHOURSTWELVEMINSECONDS * 1.0)));
            setLabelText(l02Countdown, eightHoursTwelveMinCount.get());
        });
        eightHoursFourtyCount.addListener(observable -> {
            pB13Countdown.setProgress((1.0 - eightHoursFourtyCount.get() / (EIGHTHOURSFOURTYONEINSECONDS * 1.0)));
            setLabelText(l03Countdown, eightHoursFourtyCount.get());
        });
        tenHoursCount.addListener(observable -> {
            pB14Countdown.setProgress((1.0 - tenHoursCount.get() / (TENHOURSINSECONDS * 1.0)));
            setLabelText(l04Countdown, tenHoursCount.get());
        });
        twelveHoursCount.addListener(observable -> {
            pB15Countdown.setProgress((1.0 - twelveHoursCount.get() / (TWELVEHOURSINSECONDS * 1.0)));
            setLabelText(l05Countdown, twelveHoursCount.get());
        });
        startTimeLines();
    }

    private void setLabelText(Label label, int hours) {
        label.setText(String.format("%02d", formatHours(hours)) + ":" + String.format("%02d", (hours / 60) % 60));
    }

    private void createTimeLines(int deltaFiveHours, int deltaSixHours, int deltaEightTwelveHours, int deltaEightFourtyHours, int deltaTenHours, int deltaTwelveHours) {
        countdownFiveHours = TimeLineFactory.get(deltaFiveHours, fiveHoursCount);
        countdownSixHours = TimeLineFactory.get(deltaSixHours, sixHoursCount);
        countdownEightTwelveHours = TimeLineFactory.get(deltaEightTwelveHours, eightHoursTwelveMinCount);
        countdownEightFourtyHours = TimeLineFactory.get(deltaEightFourtyHours, eightHoursFourtyCount);
        countdownTenHours = TimeLineFactory.get(deltaTenHours, tenHoursCount);
        countdownTwelveHours = TimeLineFactory.get(deltaTwelveHours, twelveHoursCount);
    }

    private void startTimeLines() {
        countdownFiveHours.playFromStart();
        countdownSixHours.playFromStart();
        countdownEightTwelveHours.playFromStart();
        countdownEightFourtyHours.playFromStart();
        countdownTenHours.playFromStart();
        countdownTwelveHours.playFromStart();
    }

    private void setPropertyInitialValues(int deltaFiveHours, int deltaSixHours, int deltaEightTwelveHours, int deltaEightFourtyHours, int deltaTenHours, int deltaTwelveHours) {
        fiveHoursCount.setValue(deltaFiveHours);
        sixHoursCount.setValue(deltaSixHours);
        eightHoursTwelveMinCount.setValue(deltaEightTwelveHours);
        eightHoursFourtyCount.setValue(deltaEightFourtyHours);
        tenHoursCount.setValue(deltaTenHours);
        twelveHoursCount.setValue(deltaTwelveHours);
    }


    private int delta(int duration, int startTime, int currentTime) {
        return duration - (currentTime - startTime);
    }


    private int formatHours(int hours) {
        if (hours % 3600 == 0) return hours / SECONDSOFHOUR - 1;
        return hours / SECONDSOFHOUR;
    }

    public void initializeTableView() {
        tableDate.setCellValueFactory(new PropertyValueFactory<TimeRecord, String>("datum"));
        tableKommen.setCellValueFactory(new PropertyValueFactory<TimeRecord, String>("kommen"));
        tableGehen.setCellValueFactory(new PropertyValueFactory<TimeRecord, String>("gehen"));
        tableZeit.setCellValueFactory(new PropertyValueFactory<TimeRecord, String>("zeit"));

        table.getItems().setAll(DataBase.readData());
    }

    public void saveData() {
        DataBase.saveOnClose(lGehenZeit.getText(),
                lKommenZeit.getText(),
                lStopuhr.getText(),
                LocalDate.now().getDayOfWeek().toString());
    }


    private enum StopWatchStatus {
        STOPPED, RUNNING
    }

}
