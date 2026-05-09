package Networking;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

public class picoConnection {
    private Socket picoSocket;

    public picoConnection(int port, String IP, Queue<picoPacket> picoPackets) {
        try {
            this.picoSocket = new Socket(IP, port);

            Runnable piRunnable = new PiCRXRun(picoSocket, picoPackets);
            Thread RXThread = new Thread(piRunnable);
            RXThread.start();
        } catch (IOException e) {
            System.out.println("Pico connection failed");
        }
    }
}