package JSockets;

import Email.EmailDashController;
import JCode.FileHelper;
import JCode.trayHelper;
import objects.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class JClient {

    public static Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    FileHelper fh = new FileHelper();
    Network network;
    static trayHelper th;

    private static boolean displayed = false;

    public JClient() {
        try {
            network = fh.getNetworkDetails();
            th = new trayHelper();
            socket = new Socket();
            socket.connect(new InetSocketAddress(network.getHost(), 9001), 3000);
            th.displayNotification("Alert!", "Connected to Email Receiver");
            displayed = false;
            startListening();
        } catch (Exception e) {
            System.out.println(e);
//            e.printStackTrace();
            restartSocket();
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                dis = new DataInputStream(socket.getInputStream());
                boolean isServerOpen = true;
                while (isServerOpen) {
                    try {
                        String d = dis.readUTF();
                        if (d.equals("R")) {
//                            EmailDashController.reload = true;
                            EmailDashController.loadEmailsStatic();
                        }
                        else {
                            th.displayNotification("Email Received", "Email Received from " + d);
                            EmailDashController.loadEmailsStatic();
                        }
                    } catch (IOException e) {
                        // The client may have closed the socket.
                        isServerOpen = false;
                        restartSocket();
                        System.out.println(e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendMessage(String msg) {
        new Thread(() -> {
            try {
                System.out.println(msg);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(msg);      //R is for reboot

            } catch (IOException e) {
                // The client may have closed the socket.
                restartSocket();
                e.printStackTrace();
//                System.out.println(e);
            }
        }).start();
    }

    private static void restartSocket() {
        if (!displayed) {
            th.displayNotification("Alert!", "Software not connected to Email Receiver!\nTrying again in 5 seconds!");
            displayed = true;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        new JClient();
    }


}
