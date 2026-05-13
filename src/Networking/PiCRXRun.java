package Networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Queue;

public class PiCRXRun implements Runnable {
    private Socket connSocket;

    private Queue<picoPacket> packetQueue;

    public PiCRXRun(Socket connSocket, Queue<picoPacket> packetQueue) {
        this.connSocket = connSocket;
        this.packetQueue = packetQueue;
    }

    @Override
    public void run() {
        String line;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()))) {
            while ((line = bf.readLine()) != null) {
                String[] parts = line.strip().split(",");
                String tag = parts[0];
                if (!tag.equals("D")) {
                    System.out.println("Non data packet was sent!");
                    continue;
                }
                double err = Double.parseDouble(parts[1]);
                double pv = Double.parseDouble(parts[2]);
                double cor = Double.parseDouble(parts[3]);

                picoPacket packet = new picoPacket("D", err, pv, cor);

                synchronized (packetQueue) {
                    packetQueue.add(packet);
                }
            }
        }

        catch (IOException e) {
            System.out.println("Failed when conecting to input stream");
        }
    }

}
