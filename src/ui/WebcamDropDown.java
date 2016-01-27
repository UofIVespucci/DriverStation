package ui;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.control.ComboBox;

import java.util.HashMap;
import java.util.Map;

public class WebcamDropDown extends ComboBox{
    private Map<String, Webcam> wcMap;

    public WebcamDropDown()
    {
        super();
        wcMap = new HashMap<>();
        getWebcams();
    }

    public void getWebcams()
    {
        wcMap.clear();
        getItems().clear();

        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
            if (wc.getName()!=null) getItems().add(wc.getName());
            wcMap.put(wc.getName(), wc);
        }
    }

    private Webcam getWebcamByName(String n)
    {
        return wcMap.get(n);
    }

    public Webcam getSelected(){
        return getWebcamByName(getSelectionModel().getSelectedItem().toString());
    }
}
