package ui.organization;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;

/**
 * Created by Lance on 5/3/2016.
 */
public class ScreenMessage extends GridPane {
    private Label message;

    public ScreenMessage() {
        VBox messageContainer = new VBox();
        Label dismissMessage = new Label("CLICK TO DISMISS");

        message = new Label("");

        message.getStyleClass().add("screen-label-textB");
        dismissMessage.getStyleClass().add("screen-label-textI");

        messageContainer.setAlignment(Pos.CENTER);

        messageContainer.getChildren().addAll(message, dismissMessage);

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
        message.setText(addLineBreak(getMessage, 25));
    }

    private static String addLineBreak(String getString, int breakPoint) {
        return addLineBreak(getString, breakPoint, new ArrayList<>());
    }

    private static String addLineBreak(String getString, int breakPoint, ArrayList<String> strings) {
        if (getString.length() < breakPoint) {
            String outString = "";
            for (String s : strings) outString += s.trim() + "\n";
            return outString + getString.trim();
        }
        int findBreakPoint = breakPoint;
        for (int i = 0; i < breakPoint; i++) {
            if (getString.charAt(i) == ' ') findBreakPoint = i;
        }
        String subString = getString.substring(0, findBreakPoint);
        if (findBreakPoint == breakPoint) subString += "-";

        strings.add(subString);

        return addLineBreak(getString.substring(findBreakPoint, getString.length()), breakPoint, strings);
    }
}
