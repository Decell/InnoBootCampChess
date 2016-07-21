import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * @author Nail Alishev
 * 11-401
 * Chess
 */
public class Logic implements Runnable {
    private PrintWriter pw;
    private BufferedReader br;

    public Logic(PrintWriter pw, BufferedReader br) {
        this.pw = pw;
        this.br = br;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while(true){

        }
    }
}