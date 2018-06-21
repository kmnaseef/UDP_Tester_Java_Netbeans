package udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
      
public class UdpController implements Initializable {
    udpreceive receiveobj;
    DatagramSocket udpsocket;
    DatagramPacket sendpacket;
    
    @FXML
    private Button sendbutton, clearbutton;
    @FXML
    private  TextField sendip, sendport, hostip, hostport, listenport;
    public TextArea statustext;
    @FXML
    public TextArea sendmsg, rcvmsg;
    @FXML
    private Label localiplabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        
        try {
            udpsocket = new DatagramSocket(43000);
            localiplabel.setText(InetAddress.getLocalHost().getHostAddress());
        }catch (SocketException | UnknownHostException ex) {
            rcvmsg.setText("Error: "+ ex.getMessage());
        }
        receiveobj = new udpreceive();
        Thread receivethread = new Thread(receiveobj);
        receivethread.setDaemon(true);
        receivethread.start();
    }    

    @FXML
    public void send(ActionEvent event) {    
        String sendmessage = sendmsg.getText();
        byte[] msg = sendmessage.getBytes();
        try {
            sendpacket = new DatagramPacket(msg,sendmessage.length());
            sendpacket.setAddress(InetAddress.getByName(sendip.getText()));
            sendpacket.setPort(Integer.parseInt(sendport.getText()));
            udpsocket.send(sendpacket);
        } catch (IOException | NumberFormatException ex) {
            statustext.setText("Error: " + ex.getMessage());
        }
    }
    
    public class udpreceive implements Runnable{
      
        byte[] rcvdmsg = new byte[1024];
        DatagramPacket rcvpacket = new DatagramPacket(rcvdmsg, rcvdmsg.length);
        String rcvdmessage;

        @Override
        public void run() {
            while(true)
            {
                try {
                    udpsocket.receive(rcvpacket);
                    rcvdmsg = rcvpacket.getData();
                    rcvdmessage = new String(rcvdmsg);
                    rcvmsg.setText(rcvdmessage);
                    hostip.setText(rcvpacket.getAddress().getHostAddress());
                    hostport.setText(String.valueOf(rcvpacket.getPort()));
                } catch (IOException ex) {
                    rcvmsg.setText("eror "+ex.getMessage());
                }
            }
        }    
    }
}
