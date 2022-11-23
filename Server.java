import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread{ 
    private static int port = 8080;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Scanner s = new Scanner(System.in);

    public Server(int port) {
        Server.port = port;
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("SERVER RUNNING....");

            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread sender = new Thread(new Runnable() {
                String message;

                @Override
                public void run() {
                    while(true) {
                        message = s.nextLine();
                        out.println(message);
                        out.flush();
                    }
                }
            });
            sender.start();

            Thread receive = new Thread(new Runnable() {
                String msg ;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();

                        while(msg!=null){
                            System.out.println("Client : " + msg);
                            msg = in.readLine();
                        }

                        System.out.println("Client disconnected");

                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receive.start();

            

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return Server.port;
    }

}
