import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client{
    private static String serverIP = "192.168.1.189";
    private static int serverPort = 8080 ;
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Scanner s = new Scanner(System.in);


    public static void main(String[] args) {
        try {
            socket = new Socket(serverIP, serverPort);

            System.out.println("CLIENT RUNNING....");

            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

            Thread sender = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    while(true){
                        msg = s.nextLine();
                        out.println(msg);
                        out.flush();
                    }
                }
            });
            sender.start();

            Thread receiver = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        while(msg!=null){
                            System.out.println("Server : "+msg);
                            msg = in.readLine();
                        }
                        System.out.println("Server out of service");
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver .start();


            
        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }

    
}



