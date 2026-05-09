import java.io.IOError;
import java.io.IOException;
import java.net.Socket;

public class picoConnection {
    private Socket picoSocket;

    public picoConnection(int port, String IP) {
        try {
            this.picoSocket = new Socket(IP, port);
        } catch (IOException e) {
            System.out.println("Pico connection failed");
        }
    }
}