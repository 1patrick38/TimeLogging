package util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.util.Duration;

public class TimeLineFactory {

    // auxiliary class
    private TimeLineFactory() {
    }

    public static Timeline get(int duration, IntegerProperty label) {
        try{
            Timeline t = new Timeline();
            t.getKeyFrames().add(new KeyFrame(Duration.seconds(duration + 1)
                    , new KeyValue(label, 0)));
            return t;
        }catch (Exception exception) {
            return new Timeline(new KeyFrame(Duration.millis(1),new KeyValue(label,0)));
        }
    }
}
