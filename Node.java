import java.io.BufferedReader;
import java.io.*;
import java.net.*;

public class Node extends Thread implements Comparable<Node>{
    private Socket conn;
    private BufferedReader msgReceiver;
    private PrintWriter msgSender;
    private int id;
    private String ip;
    private int port; 
    private String msg = "";

    public Node(Socket conn, BufferedReader msgReceiver, PrintWriter msgSender) {
        this.conn = conn;
        this.msgReceiver = msgReceiver;
        this.msgSender = msgSender;
    }

    public Node(int id, String ip, int port, Socket conn, BufferedReader msgReceiver, PrintWriter msgSender) {
        this.id = id;
        this.ip = ip;
        this.port = port;
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
                        System.out.println(Constants.VAGUE_OUT_OF_SERVICE); 
                        System.exit(0);
                        msgSender.close();
                        msgReceiver.close();
                        conn.close();
                    } catch (IOException e) {
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
    public Socket getConnection(){
        return this.conn;
    }
    @Override
    public int compareTo(Node n1){
        if(this.id > n1.id){
            return 1;
        }
        else if(this.id < n1.id){
            return -1;
        }else{
            return 0;
        }   
    }

}



