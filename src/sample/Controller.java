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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final double ESTIMATEDMONTHSECONDS = 554400;
    private static final int SECONDSOFHOUR = 3600;
    private final static int FIVEHOURSINSECONDS = 18000;
    private final static int SIXHOURSINSECONDS = 21600;
    private final static int EIGHTHOURSTWELVEMINSECONDS = 29520;
    private final static int EIGHTHOURSFOURTYONEINSECONDS = 31260;
    private final static int TENHOURSINSECONDS = 36000;
    private final static int TWELVEHOURSINSECONDS = 43200;
    private final static int ESTIMATEDWEEKSECONDS = 138600;
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
            l05Countdown,
            lWoche00Count,
            lMonat00Count;
    @FXML
    private ProgressBar pB10Countdown,
            pB11Countdown,
            pB12Countdown,
            pB13Countdown,
            pB14Countdown,
            pB15Countdown,
            pB10WocheCountdown,
            pB11MonatCountdown;
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

    // TODO: refactor to timeline!!!!
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

        lKommenZeit.setText(startingTime());
        lGehenZeit.setText(estimatedEndTime());

        startCountdowns();

    }

    void initClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            lUhrzeit.setText(format(hour) + ":" + format(minute) + ":" + format(second));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private String startingTime() {
        if (startTime.isPresent() && startTime.get().matches("[0-9:]*")) {
            return startTime.get();
        } else return TIME_FORMATTER.format(LocalDateTime.now());
    }

    private String estimatedEndTime() {
        if (startTime.isPresent() && startTime.get().matches("[0-9:]*")) {
            return TIME_FORMATTER.format(parseStringToTime(startTime).plusHours(10).plusMinutes(30));
        } else return TIME_FORMATTER.format(LocalDateTime.now().plusHours(10).plusMinutes(30));
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


    private void startCountdowns() {


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
            pB10Countdown.setProgress(calculateProgress(fiveHoursCount.get(), FIVEHOURSINSECONDS));
            setLabelText(l00Countdown, fiveHoursCount.get(), true);
        });
        sixHoursCount.addListener(observable -> {
            pB11Countdown.setProgress(calculateProgress(sixHoursCount.get(), SIXHOURSINSECONDS));
            setLabelText(l01Countdown, sixHoursCount.get(), true);
        });
        eightHoursTwelveMinCount.addListener(observable -> {
            pB12Countdown.setProgress(calculateProgress(eightHoursTwelveMinCount.get(), EIGHTHOURSTWELVEMINSECONDS));
            setLabelText(l02Countdown, eightHoursTwelveMinCount.get(), true);
        });
        eightHoursFourtyCount.addListener(observable -> {
            pB13Countdown.setProgress(calculateProgress(eightHoursFourtyCount.get(), EIGHTHOURSFOURTYONEINSECONDS));
            setLabelText(l03Countdown, eightHoursFourtyCount.get(), true);
        });
        tenHoursCount.addListener(observable -> {
            pB14Countdown.setProgress(calculateProgress(tenHoursCount.get(), TENHOURSINSECONDS));
            setLabelText(l04Countdown, tenHoursCount.get(), true);
        });
        twelveHoursCount.addListener(observable -> {
            pB15Countdown.setProgress(calculateProgress(twelveHoursCount.get(), TWELVEHOURSINSECONDS));
            setLabelText(l05Countdown, twelveHoursCount.get(), true);
        });
        startTimeLines();
    }

    private double calculateProgress(int actual, int constant) {
        return 1.0 - actual / (constant * 1.0);
    }

    private void setLabelText(Label label, int seconds, boolean isProgressBar) {
        if (seconds <= 0) {
            label.setText("00:00");
            return;
        }
        int minutes = (seconds / 60) % 60;
        label.setText(format(formatHours(seconds, isProgressBar)) + ":" + format(minutes));
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

    private String format(int input) {
        return String.format("%02d", input);
    }


    private int formatHours(int hours, boolean isProgressBar) {
        if (isProgressBar) {
            if (hours % 3600 == 0) return hours / SECONDSOFHOUR - 1;
            return hours / SECONDSOFHOUR;
        } else return hours / SECONDSOFHOUR;
    }

    void initializeTableView() {
        tableDate.setCellValueFactory(new PropertyValueFactory<>("datum"));
        tableKommen.setCellValueFactory(new PropertyValueFactory<>("kommen"));
        tableGehen.setCellValueFactory(new PropertyValueFactory<>("gehen"));
        tableZeit.setCellValueFactory(new PropertyValueFactory<>("zeit"));

        table.getItems().setAll(DataBase.readData());
        setLabelText(lWoche00Count, getWeekHoursInSeconds(), false);
        pB10WocheCountdown.setProgress((getWeekHoursInSeconds() * 1.0) / ESTIMATEDWEEKSECONDS);
        setLabelText(lMonat00Count, getMonthHoursInSeconds(), false);
        pB11MonatCountdown.setProgress((getMonthHoursInSeconds() * 1.0) / ESTIMATEDMONTHSECONDS);
    }

    void saveData() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DataBase.saveOnClose(formatter.format(LocalTime.now()),
                lKommenZeit.getText(),
                lStopuhr.getText(),
                format.format(LocalDate.now()));
    }

    // TODO: refactor me
    private int getWeekHoursInSeconds() {
        final int[] weekMinutes = {0};
        List<TimeRecord> timeRecords = getRecordsOfActualWeek(DataBase.readData());
        timeRecords.forEach(timeRecord -> {
            int minutes = formatTimeToMinutes(timeRecord.getZeit());
            weekMinutes[0] += minutes;
        });
        return weekMinutes[0] * 60;
    }

    // TODO: refactor me
    private int getMonthHoursInSeconds() {
        final int[] monthMinutes = {0};
        List<TimeRecord> timeRecords = getRecordsOfActualMonth(DataBase.readData());
        timeRecords.forEach(timeRecord -> {
            int minutes = formatTimeToMinutes(timeRecord.getZeit());
            monthMinutes[0] += minutes;
        });
        return monthMinutes[0] * 60;
    }

    private List<TimeRecord> getRecordsOfActualWeek(List<TimeRecord> timeRecords) {
        Collections.reverse(timeRecords);
        List<TimeRecord> acc = new ArrayList<>();
        final int[] lastMonday = {99};
        final int[] actMonth = {99};
        timeRecords.forEach(timeRecord -> {
            LocalDate date = getLocalDate(timeRecord);
            int day = date.getDayOfMonth();
            if (actMonth[0] == 99) actMonth[0] = date.getMonthValue();
            if (lastMonday[0] == 99) lastMonday[0] = date.with(DayOfWeek.MONDAY).getDayOfMonth();
            if (lastMonday[0] <= day && date.getMonthValue() == actMonth[0]) {
                acc.add(timeRecord);
            } else return;

        });
        return acc;

    }


    private List<TimeRecord> getRecordsOfActualMonth(List<TimeRecord> timeRecords) {
        Collections.reverse(timeRecords);
        List<TimeRecord> acc = new ArrayList<>();
        final int[] actMonth = {99};
        timeRecords.forEach(timeRecord -> {
            LocalDate date = getLocalDate(timeRecord);
            int month = date.getMonthValue();
            if (actMonth[0] == 99) actMonth[0] = month;
            if (month == actMonth[0]) {
                acc.add(timeRecord);
            } else return;

        });
        return acc;

    }

    private LocalDate getLocalDate(TimeRecord timeRecord) {
        String x = timeRecord.getDatum();
        String[] splittedDate = x.split("-");
        return LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(splittedDate[1]), Integer.valueOf(splittedDate[0]));
    }

    private int formatTimeToMinutes(String zeit) {
        String[] split = zeit.split(":");
        return Integer.valueOf(split[0]) * 60 + Integer.valueOf(split[1]);
    }

    private enum StopWatchStatus {
        STOPPED, RUNNING
    }


}
