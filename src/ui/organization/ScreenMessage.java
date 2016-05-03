package ui.organization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

/**
 * Created by Lance on 5/3/2016.
 */
public class ScreenMessage extends GridPane {
    private VBox messageContainer;
    private Label message;
    private Label dismissMessage;

    public ScreenMessage() {
        messageContainer = new VBox();
        message = new Label("");
        dismissMessage = new Label("CLICK TO DISMISS");

        message.getStyleClass().add("screen-label-textB");
        dismissMessage.getStyleClass().add("screen-label-textI");

        messageContainer.setAlignment(Pos.CENTER);

        messageContainer.getChildren().add(message);
        messageContainer.getChildren().add(dismissMessage);

        messageContainer.getStyleClass().add("screen-label");
        getStyleClass().add("screen-message");
        setAlignment(Pos.CENTER);
        getChildren().addAll(messageContainer);

        managedProperty().bind(visibleProperty());

        setVisible(false);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dismiss();
            }
        });
    }

    public void dismiss() {
        setVisible(false);
    }

    public void show() {
        setVisible(true);
    }

    public void setMessage(String getMessage) {
        message.setText(getMessage);
    }
}
