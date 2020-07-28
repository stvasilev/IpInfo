package ipinfo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.*;
import java.util.*;

public class Controller implements Initializable {

    private String api = "http://ip-api.com/json/";
    // request + ip + ?fields=

    @FXML
    private VBox checkerBox;
    @FXML
    private VBox lblBox;

    public TextField ipTextBox;
    public Label localip;
    public Label publicip;

    private void clearLabels() {
        for(Node node: lblBox.getChildren()) {
            ((Label) node).setText("");
        }
    }

    public void onFetch() throws Exception {
        clearLabels();

        StringBuilder request = new StringBuilder();
        request.append(ipTextBox.getText()).append("?fields=");

        Boolean entered = false;
        for (Node child : checkerBox.getChildren()) {
            CheckBox box = (CheckBox) child;
            if(box.isSelected()) {
                request.append(box.getId().substring(3)).append(",");
                entered = true;
            }
        }
        
        if (entered) {
            request.deleteCharAt(request.length()-1);
            connectAndRead(request.toString());
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

        response.deleteCharAt(response.length()-1).deleteCharAt(0);
        String[] arrResponse = (response.toString()).split(",");

        String key, val;
        for(String el : arrResponse) {
            el.replace("\"", "" );
            key = el.split(":")[0].replace("\"", "");
            val = el.split(":")[1].replace("\"", "");

            ((Label) lblBox.lookup("#" + key)).setText(val);
        }
    }

    public void onSave() throws IOException {
        DirectoryChooser dir = new DirectoryChooser();
        File selectedDirectory = dir.showDialog(checkerBox.getScene().getWindow());

        if (selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());

            try (FileWriter fw = new FileWriter(selectedDirectory + "/" + ipTextBox.getText() + ".txt")) {
                for (Node lbl : lblBox.getChildren()) {
                    if (((Label) lbl).getText() != "") {
                        fw.write((lbl.getId() + " : " + ((Label) lbl).getText()) + "\n");
                    }
                }
            }
        }
    }

    private void readPublicIP() throws Exception{

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
        ipTextBox.setText(ip);
    }

    private void readPrivateIP() throws Exception{
        String ip;

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
        try {
            readPublicIP();
            readPrivateIP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
