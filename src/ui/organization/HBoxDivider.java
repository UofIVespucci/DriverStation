package ui.organization;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HBoxDivider extends AnchorPane{
    private ImageView barView;
    private Image barImg;

    public HBoxDivider(){
        barImg = new Image("ui/toolbox/hbar.png");
        barView = new ImageView();

        barView.setImage(barImg);
        getChildren().add(barView);
        barView.fitWidthProperty().bind(this.widthProperty());
    }
}
