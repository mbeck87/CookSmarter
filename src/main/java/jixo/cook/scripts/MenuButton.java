package jixo.cook.scripts;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

public class MenuButton {

    private HBox box;
    private Boolean selected = false;
    private Boolean locker = false;
    private int maxSteps = 20;
    private Timeline timeline = new Timeline();

    // constructor
    public MenuButton() {
    }

    public MenuButton(HBox box) {
        this.box = box;
    }

    // mouse entered animation
    public void highlight() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }
        timeline.getKeyFrames().clear();
        for (int step = 0; step <= maxSteps; step++) {
            double level = (double) step / maxSteps;
            KeyFrame frame = new KeyFrame(Duration.millis(step * 25), e -> {
                box.setBackground(new Background(new BackgroundFill(
                        new LinearGradient(
                                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                                new Stop(0, Color.CORNFLOWERBLUE),
                                new Stop(level, Color.TRANSPARENT)
                        ),
                        CornerRadii.EMPTY, Insets.EMPTY
                )));
            });

            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }

    // mouse exited animation
    public void unhighlight() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }
        timeline.getKeyFrames().clear();
        for (int step = maxSteps; step >= 0; step--) {
            double level = (double) step / maxSteps;
            KeyFrame frame = new KeyFrame(Duration.millis((maxSteps - step) * 25), e -> {
                box.setBackground(new Background(new BackgroundFill(
                        new LinearGradient(
                                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                                new Stop(0, Color.CORNFLOWERBLUE),
                                new Stop(level, Color.TRANSPARENT)
                        ),
                        CornerRadii.EMPTY, Insets.EMPTY
                )));
            });
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }

    // bei buttonclick
    public void setSelected(Boolean selection) {
        // soll selektiert werden
        if(selection == true) {
            selected = true;
            if(timeline.getStatus() == Animation.Status.RUNNING && !locker) {
                timeline.stop();
            }
            box.setBackground(new Background(new BackgroundFill(
                    new LinearGradient(
                            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.DARKCYAN),
                            new Stop(1, Color.TRANSPARENT)
                    ),
                    CornerRadii.EMPTY, Insets.EMPTY
            )));
        // wenn selektiert ist und deselektiert werden soll
        } else if(selected == true) {
            timeline.getKeyFrames().clear();
            selected = false;
            for (int step = maxSteps; step >= 0; step--) {
                double level = (double) step / maxSteps;
                KeyFrame frame = new KeyFrame(Duration.millis((maxSteps - step) * 25), e -> {
                    box.setBackground(new Background(new BackgroundFill(
                            new LinearGradient(
                                    0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                                    new Stop(0, Color.DARKCYAN),
                                    new Stop(level, Color.TRANSPARENT)
                            ),
                            CornerRadii.EMPTY, Insets.EMPTY
                    )));
                });
                timeline.getKeyFrames().add(frame);
            }
            // locker um animationsabbruch zu verhindern
            locker = true;
            timeline.setOnFinished(event -> {
                locker = false;
            });
            timeline.play();
        }
    }

    // getter
    public HBox getBox() {
        return box;
    }

    public Boolean getSelected() {
        return selected;
    }
}
