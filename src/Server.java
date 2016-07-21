import javax.swing.*;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by sentpim on 19.07.2016.
 */

public class Server extends JFrame{
    final int PORT = 3456;
    ArrayList<Connection> connections;
    JLabel serverLabel;
    JTextField serverName;

    boolean newConnection = true;

    public Server() throws IOException {
        connections = new ArrayList<Connection>();
        go();
    }

    public void go() throws IOException {

        serverLabel = new JLabel();
        serverName = new JTextField();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(150,150,200,120);
        serverLabel.setText("Адрес сервера:");
        serverName.setText(Inet4Address.getLocalHost().getHostAddress() + "");
        serverName.setEditable(false);
        serverLabel.setBounds(5,5,100,40);
        serverName.setBounds(50,10,100,40);
        add(serverLabel);
        add(serverName);
        setVisible(true);
        ServerSocket s1 = new ServerSocket(PORT);
        Socket client = s1.accept();
        while (true) {

            if (newConnection) {
                connections.add(new Connection(client,connections,connections.size()));
                newConnection = false;
            } else {
                Connection c = connections.get(connections.size() - 1);
                if (!c.isClosed()) {
                    c.addSecondPlayer(client);
                    newConnection = true;
                } else {
                    connections.remove(connections.size() - 1);
                    connections.add(new Connection(client,connections,connections.size()));
                    newConnection = false;
                }

            }
        }
    }

    public void resetConnection() {
        this.newConnection = true; //вызывает первый игрок, если закрывает окно не дождавшись соперника
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

}
