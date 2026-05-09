package GUI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import Networking.picoConnection;
import Networking.picoPacket;

public class appModel {
    // The gains
    private int Kp = 0;
    private int Ki = 0;
    private int Kd = 0;
    // The connection to the pico
    private picoConnection piC;
    private Queue<picoPacket> picoPackets = new LinkedList<>();

    private ArrayList<Observer<appModel, String>> observerlist = new ArrayList<>();

    private final int RESETVAL = 0;

    public appModel() {

        this.piC = new picoConnection(8080, "192.168.4.1", picoPackets);
        this.piC.recieve();

    }

    public void send(String type, int data) {
        String dataMsg;

        switch (type) {
            case "Kp" -> {
                dataMsg = "G" + "," + data + "," + this.Ki + "," + this.Kd;
            }

            case "Ki" -> {
                dataMsg = "G" + "," + this.Kp + "," + data + "," + this.Kd;
            }

            default -> {
                dataMsg = "G" + "," + this.Kp + "," + this.Ki + "," + data;
            }
        }

        this.piC.send(dataMsg);

        alertObservers("Changed " + type + "'s value to" + data);
    }

    public void reset() {
        this.Kd = RESETVAL;
        this.Ki = RESETVAL;
        this.Kp = RESETVAL;

        alertObservers("Reset all values to " + RESETVAL);
    }

    public void addObserver(Observer<appModel, String> observer) {
        this.observerlist.add(observer);
    }

    public void alertObservers(String data) {
        for (Observer<appModel, String> o : this.observerlist)
            // TODO implement
            o.update(data);
    }

    public picoPacket getLatestPacket() {
        synchronized (this.picoPackets) {
            return this.picoPackets.poll();
        }
    }

}