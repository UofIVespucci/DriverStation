package ui.organization;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class VBoxDivider extends AnchorPane{
    private ImageView barView;
    private Image barImg;

    public VBoxDivider(){
        barImg = new Image("ui/toolbox/bar.png");
        barView = new ImageView();

        barView.setImage(barImg);
        getChildren().add(barView);
        barView.fitHeightProperty().bind(this.heightProperty());
    }
}
