package ui.toolbox;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Created by Lance on 5/4/2016.
 */
public class SliderImageView extends AnchorPane{
    private ImageView leftImage;
    private ImageView rightImage;

    public SliderImageView(Image getLeftImg, Image getRightImg, double border) {
        leftImage = new ImageView(getLeftImg);
        rightImage = new ImageView(getRightImg);

        getChildren().addAll(leftImage, rightImage);

        AnchorPane.setLeftAnchor(leftImage, border);
        AnchorPane.setRightAnchor(rightImage, border);
    }
}
