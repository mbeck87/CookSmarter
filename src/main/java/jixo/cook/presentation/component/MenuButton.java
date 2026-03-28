package jixo.cook.presentation.component;

import javafx.scene.layout.HBox;

public class MenuButton {

    private HBox box;
    private boolean selected = false;

    public MenuButton() {}

    public MenuButton(HBox box) {
        this.box = box;
    }

    // Hover is handled entirely by CSS :hover — nothing to do here
    public void highlight() {}
    public void unhighlight() {}

    public void setSelected(Boolean selection) {
        selected = selection;
        if (selected) {
            if (!box.getStyleClass().contains("selected")) {
                box.getStyleClass().add("selected");
            }
        } else {
            box.getStyleClass().remove("selected");
        }
    }

    public HBox getBox() { return box; }
    public Boolean getSelected() { return selected; }
}
