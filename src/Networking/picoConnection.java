package Networking;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

public class picoConnection {
    private Socket picoSocket;
    private int port;
    private String picoIP;
    private Queue<picoPacket> picoPackets;

    public picoConnection(int port, String IP, Queue<picoPacket> picoPackets) {
        this.port = port;
        this.picoIP = IP;
        this.picoPackets = picoPackets;

        try {
            this.picoSocket = new Socket(IP, port);
        }

        catch (IOException e) {
            System.out.println("Pico connection failed");
        }

    }

    public void recieve() {
        Runnable piRunnable = new PiCRXRun(this.picoSocket, this.picoPackets);
        Thread RXThread = new Thread(piRunnable);
        RXThread.start();

    }

    public void send(String type, int Kp, int ki, int kd) {
        try (PrintStream pw = new PrintStream(this.picoSocket.getOutputStream())) {
            pw.println(type + ", " + Kp + ", " + ki + ", " + kd);
        } catch (IOException e) {
            System.out.println("Could not send data to pico");
        }
    }
}