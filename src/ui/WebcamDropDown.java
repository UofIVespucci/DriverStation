package ui;

import com.Main;
import com.github.sarxos.webcam.Webcam;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebcamDropDown extends ComboBox{
    private ObservableList<String> wcNames;
    private Map<String, Webcam> wcMap;

    public WebcamDropDown()
    {
        getWebcams();
        setItems(wcNames);

        wcNames.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                if (wcMap.get(getSelectionModel().getSelectedItem().toString())!=null)
                    Main.guiManager.setWebcam(wcMap.get(getSelectionModel().getSelectedItem().toString()));
            }
        });
    }

    public void getWebcams()
    {
        List<String> l = new ArrayList<>();
        ObservableList<String> wcNamesInit = FXCollections.observableList(l);
        Map<String, Webcam> wcMapInit = new HashMap<>();

        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
            wcNamesInit.add(wc.getName());
            wcMapInit.put(wc.getName(),wc);
        }
    }

    private Webcam getWebcamByName(String n)
    {
        return wcMap.get(n);
    }
}
