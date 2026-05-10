package GUI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import Networking.picoConnection;
import Networking.picoPacket;

public class appModel {
    // The gains
    private double Kp = 0;
    private double Ki = 0;
    private double Kd = 0;
    // The connection to the pico
    private picoConnection piC;
    private Queue<picoPacket> picoPackets = new LinkedList<>();

    private ArrayList<Observer<appModel, String>> observerlist = new ArrayList<>();

    private final int RESETVAL = 0;

    public appModel() {

        this.piC = new picoConnection(5000, "192.168.4.1", picoPackets);
        this.piC.recieve();

    }

    public void send(String type, double data) {
        String dataMsg;

        switch (type) {
            case "Kp" -> {
                this.Kp = data;
                dataMsg = "G" + "," + this.Kp + "," + this.Ki + "," + this.Kd;
            }

            case "Ki" -> {
                this.Ki = data;
                dataMsg = "G" + "," + this.Kp + "," + this.Ki + "," + this.Kd;
            }

            default -> {
                this.Kd = data;
                dataMsg = "G" + "," + this.Kp + "," + this.Ki + "," + this.Kd;
            }
        }

        // ROunds to 3 sig figs as 1000 moves 3 places right then rounds to an intger
        // then back 3 sig figs + doing double division
        alertObservers("Changed " + type + "'s value to " + Math.round(data * 1000) / 1000.0);

        this.piC.send(dataMsg);

    }

    public void reset() {
        this.Kd = RESETVAL;
        this.Ki = RESETVAL;
        this.Kp = RESETVAL;

        alertObservers("Reset all values to " + RESETVAL);

        String mssg;

        mssg = "G" + "," + this.Kp + "," + this.Ki + "," + this.Kd;
        this.piC.send(mssg);
    }

    public void addObserver(Observer<appModel, String> observer) {
        this.observerlist.add(observer);
    }

    public void alertObservers(String data) {
        for (Observer<appModel, String> o : this.observerlist)
            o.update(this, data);
    }

    public picoPacket getLatestPacket() {
        synchronized (this.picoPackets) {
            return this.picoPackets.poll();
        }
    }

    public double getKpVal() {
        return this.Kp;
    }

    public double getKiVal() {
        return this.Ki;
    }

    public double getKdVal() {
        return this.Kd;
    }

}