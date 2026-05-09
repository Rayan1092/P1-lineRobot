package Networking;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Queue;

public class picoConnection {
    private Socket picoSocket;
    private Queue<picoPacket> picoPackets;
    private PrintStream ps;

    public picoConnection(int port, String IP, Queue<picoPacket> picoPackets) {
        this.picoPackets = picoPackets;
        try {
            this.picoSocket = new Socket(IP, port);
            this.ps = new PrintStream(this.picoSocket.getOutputStream());
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

    // Added this for simplicity as well but kept the other just incase
    public void send(String mssg) {
        this.ps.println(mssg);
    }

    public void send(String type, int Kp, int ki, int kd) {
        this.ps.println(type + "," + Kp + "," + ki + "," + kd);
    }

}