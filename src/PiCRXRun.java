import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PiCRXRun implements Runnable {

    public PiCRXRun(Socket s) {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            while (bf.readLine() != null) {

            }
        }

        catch (IOException e) {
            System.out.println("Failed when conecting to input stream");
        }
    }

    @Override
    public void run() {

    }
