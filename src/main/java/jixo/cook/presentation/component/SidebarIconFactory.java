package jixo.cook.presentation.component;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class SidebarIconFactory {

    /**
     * Wraps an SVGPath in a styled icon box (StackPane).
     * The SVGPath paths are designed for a 24x24 coordinate space.
     * Scaled to 0.72 so they fit comfortably in the 28x28 sidebar-item-icon box.
     */
    private static StackPane wrap(String pathData) {
        SVGPath svg = new SVGPath();
        svg.setContent(pathData);
        svg.getStyleClass().add("sidebar-icon-svg");
        svg.setScaleX(0.72);
        svg.setScaleY(0.72);

        Group group = new Group(svg);
        StackPane box = new StackPane(group);
        box.getStyleClass().add("sidebar-item-icon");
        return box;
    }

    /** Plus-circle — importing / adding a new ingredient from outside */
    public static StackPane importIngredient() {
        return wrap(
            "M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z" +
            " M12 8v8 M8 12h8"
        );
    }

    /** Database cylinder — local ingredient collection */
    public static StackPane ingredients() {
        return wrap(
            "M12 2C6.48 2 2 4.48 2 7s4.48 5 10 5 10-2.24 10-5-4.48-5-10-5z" +
            " M2 7v5c0 2.76 4.48 5 10 5s10-2.24 10-5V7" +
            " M2 12v5c0 2.76 4.48 5 10 5s10-2.24 10-5v-5"
        );
    }

    /** Pencil — write / create a new recipe */
    public static StackPane createRecipe() {
        return wrap(
            "M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"
        );
    }

    /** File with lines — recipe list */
    public static StackPane recipes() {
        return wrap(
            "M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" +
            " M14 2v6h6 M16 13H8 M16 17H8 M10 9H8"
        );
    }
}
