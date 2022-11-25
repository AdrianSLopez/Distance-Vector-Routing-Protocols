import java.io.BufferedReader;
import java.io.*;
import java.net.*;

public class Client extends Thread{
    private Socket conn;
    private BufferedReader msgReceiver;
    private PrintWriter msgSender;
    private String msg = "";

    public Client(Socket conn, BufferedReader msgReceiver, PrintWriter msgSender) {
        this.conn = conn;
        this.msgReceiver = msgReceiver;
        this.msgSender = msgSender;
    }

    @Override
    public void run() {
        try {
            Thread sender = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(msg.length() == 0) continue;
                        msgSender.println(msg);
                        msgSender.flush();
                        msg = "";
                    }

                }
            });
            sender.start();

            Thread receiver = new Thread(new Runnable() {
                String msgReceived;

                @Override
                public void run() {
                    try {
                        msgReceived = msgReceiver.readLine();
                        while(msgReceived!=null){
                            System.out.println(msgReceived);
                            msgReceived = msgReceiver.readLine();
                        }
                        System.out.println("Server out of service");

                        msgSender.close();
                        msgReceiver.close();
                        conn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        this.msg = msg;
    }  
}



