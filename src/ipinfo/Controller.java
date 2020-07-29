package ipinfo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;

public class Controller implements Initializable {

    private final String api = "http://ip-api.com/json/";
    // api_request + ip + ?fields= + params

    @FXML
    private VBox checkerBox;
    @FXML
    private VBox lblBox;

    public TextField filenameField;
    public TextField ipTextBox;
    public Label localip;
    public Label publicip;

    private void clearLabels() {
        for(Node node: lblBox.getChildren()) {
            ((Label) node).setText("");
        }
    }

    private void alert(String from) {
        Stage alertWindow = new Stage();
        VBox grid = new VBox(25);
        Label alertTxt = new Label();
        Button close = new Button("OK");
        close.setOnAction(event -> alertWindow.close());
        alertWindow.setOnCloseRequest(event -> alertWindow.close());

        switch (from) {
            case "fetch" -> alertTxt.setText("Invalid fetch parameters!");
            case "save" -> alertTxt.setText("Invalid save operation!");
        }

        alertWindow.initModality(Modality.APPLICATION_MODAL);
        alertWindow.setTitle("Error");
        grid.getChildren().addAll(alertTxt, close);
        grid.setAlignment(Pos.CENTER);

        alertWindow.setScene(new Scene(grid, 200, 150));
        alertWindow.showAndWait();
    }

    public void onFetch() throws Exception {
        // wipe all labels clean when user sends new fetch  request
        clearLabels();

        StringBuilder request = new StringBuilder();
        request.append(ipTextBox.getText()).append("?fields=");

        boolean entered = false;
        // iterate checkboxes
        // if checkbox is selected, append towards request
        for (Node child : checkerBox.getChildren()) {
            CheckBox box = (CheckBox) child;
            if(box.isSelected()) {
                request.append(box.getId().substring(3)).append(",");
                entered = true;
            }
        }

        // if user hasn't selected any checkboxes or ip field is empty, don't send request
        if (entered && !(ipTextBox.getText().isEmpty())) {
            request.deleteCharAt(request.length()-1);
            connectAndRead(request.toString());
        }
        else {
            alert("fetch");
        }
    }

    private void connectAndRead(String request) throws Exception {
        URL url = new URL(api + request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        response.deleteCharAt(response.length()-1).deleteCharAt(0); // remove curly braces from json response
        String[] arrResponse = (response.toString()).split(","); // split json response into string array

        String key, val;
        // separate every entry by (key, val) and set text on the appropriate label
        for(String el : arrResponse) {
            key = el.split(":")[0].replace("\"", "");
            val = el.split(":")[1].replace("\"", "");

            ((Label) lblBox.lookup("#" + key)).setText(val);
        }
    }

    public void onSave() throws IOException {
        DirectoryChooser dir = new DirectoryChooser();
        File selectedDirectory = dir.showDialog(checkerBox.getScene().getWindow());

        if (selectedDirectory != null && !filenameField.getText().isEmpty()) {

            try (FileWriter fw = new FileWriter(selectedDirectory + "/" + filenameField.getText() + ".txt")) {
                for (Node lbl : lblBox.getChildren()) {
                    if (!((Label) lbl).getText().equals("")) {
                        fw.write((lbl.getId() + " : " + ((Label) lbl).getText()) + System.lineSeparator());
                    }
                }
            }
        }
        else {
            alert("save");
        }
    }

    private void readPublicIP() throws Exception{

        // sending a request to the api returns a default response
        // in which the sender's public ip is visible - use that to set pub ip
        URL url = new URL(api + "?fields=query");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuilder requestedLine = new StringBuilder();
        String ip;
        String line;

        while ((line = reader.readLine()) != null) {
            requestedLine.append(line);
        }
        reader.close();

        ip = requestedLine.substring(10, requestedLine.length()-2);
        publicip.setText(ip);
    }

    private void readPrivateIP() throws Exception{
        String ip;

        // get the ip that corresponds to ip with len xxx.xxx.xxx.xxx or less
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                ip = addr.getHostAddress();

                if(ip.length() <= 15) {
                    localip.setText(ip);
                    break;
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // set pub and private ip on init, remove focus from text field
        try {
            readPublicIP();
            readPrivateIP();
            ipTextBox.setFocusTraversable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
