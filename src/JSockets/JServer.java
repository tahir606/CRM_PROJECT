package JSockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JServer {

    private ServerSocket ss;
    private DataOutputStream dos;

    public static List<Socket> sockets = new ArrayList<>();
    private final static int MAX_CLIENTS = 10;

    Scanner s = new Scanner(System.in);

    public JServer() {
        try {
            ss = new ServerSocket(2222);
            acceptSockets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptSockets() {
        new Thread(() -> {
            while (true) {
                try {
                    if (sockets.size() < 10) {
                        Socket s = ss.accept();
                        sockets.add(s);
                        System.out.println("Socket Accepted");
                    } else {
                        System.out.println("Max Clients Limit Reached");
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void broadcastMessages(String msg) {
        for (Socket s : sockets) {
            try {
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
