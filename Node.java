import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Node extends Client {
    private int id;
    private String ip;
    private int port;
    // private Socket conn;

    public Node(int id, String ip, int port, Socket conn, BufferedReader msgReceiver, PrintWriter msgSender) {
        super(conn, msgReceiver, msgSender);

        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public int getServerID() {
        return this.id;
    }

    public String getServerIP() {
        return this.ip;
    }

    public int getServerPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return id + " " + ip + " " + port;
    }
}
