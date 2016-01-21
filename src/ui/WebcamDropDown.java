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
//    private ObservableList<String> wcNames;
    private Map<String, Webcam> wcMap;

    public WebcamDropDown()
    {
        super();
//        wcNames = FXCollections.observableArrayList(new ArrayList<String>());
        wcMap = new HashMap<>();


        getWebcams();
//        if (wcNames!=null) setItems(wcNames);
//        if (wcNames!=null) getItems().addAll(wcNames);
//        setItems(wcNames);
//        setItems();

//        getItems().addListener(new ListChangeListener<String>() {
//            @Override
//            public void onChanged(Change<? extends String> c) {
//                if (wcMap.get(getSelectionModel().getSelectedItem().toString()) != null)
//                    Main.guiManager.setWebcam(wcMap.get(getSelectionModel().getSelectedItem().toString()));
//            }
//        });
    }

    public void getWebcams()
    {
//        List<String> l = new ArrayList<>();
//        ObservableList<String> wcNamesInit = FXCollections.observableArrayList(l);
//        Map<String, Webcam> wcMapInit = new HashMap<>();

        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
//            wcNamesInit.add(wc.getName());
            if (wc.getName()!=null) getItems().add(wc.getName());
            wcMap.put(wc.getName(), wc);
//            wcMapInit.put(wc.getName(), wc);
        }
        setVisibleRowCount(20);

//        wcNames = wcNamesInit;
//        wcMap = wcMapInit;
    }

    private Webcam getWebcamByName(String n)
    {
        return wcMap.get(n);
    }

    public Webcam getSelected(){
        return getWebcamByName(getSelectionModel().getSelectedItem().toString());
    }
}
