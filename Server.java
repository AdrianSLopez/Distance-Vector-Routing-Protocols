import java.io.*;
import java.net.*;
import java.util.*;

public class Server{ 
    protected static ServerSocket serverSocket;
    private static Scanner s;
    private static boolean startupCmdEntered = false;
    private static int invalidUserInputCount = 1;
    private static List<Node> connectedToUs = new ArrayList<Node>();        // Servers that connect to us
    private static List<Node> connectionToServers = new ArrayList<Node>();  // Servers from topology file

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(Constants.PORT);
            System.out.println(Constants.INTRO_MSG);

            // Handle user input
            Thread userInput = new Thread(new Runnable() {
                boolean crash = false;

                @Override
                public void run() {
                    s = new Scanner(System.in);

                    while(!crash) {
                        String input = s.nextLine();
                        System.out.println(executeCommand(input));
                    }
                }
            });
            userInput.start();
            
            // Handle incoming connections
            Thread incomingConn = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while(true) {
                            Socket clientScoket = serverSocket.accept();
                            Node c = new Node(clientScoket, new BufferedReader(new InputStreamReader(clientScoket.getInputStream())), new PrintWriter(clientScoket.getOutputStream()));
                            
                            c.start();
                            connectedToUs.add(c);
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            incomingConn.start(); 
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static String executeCommand(String userInput) {
        String[] input = userInput.split(" ");
;


        switch(input[0]) {
            case "help": 
                return (!startupCmdEntered)? Constants.HELP_1: Constants.HELP_2;
            case "server":
                if(startupCmdEntered){
                    invalidUserInputCount++;

                    if(invalidUserInputCount%3 == 0) {
                        return Constants.INVALID_AND_HELP_NOTIF;
                    } else {
                        return Constants.INVALID_NOTIF;
                    }
                } 
                startupCmdEntered = true;
                // Verify parameters
                    // file dir valid
                    // time input valid
                
                
                readTopology(input[2]);

                //READ TOPOLOGY FILE AND TIME INTERVAL
                    // Create node class
                    // for every server inputted in .txt file create node object
                    // connect to each server?? I think
                return "";
            case "update":
                return "updating...";
            case "step":
                return "stepping...";
            case "packets":
                return "packets...";
            case "display":
                return "displaying...";
            case "disable":
                return "disabling...";
            case "crash":
                return "crashing...";
            default:
                invalidUserInputCount++;

                if(invalidUserInputCount%4== 0) {
                    return Constants.INVALID_AND_HELP_NOTIF;
                } else {
                    return Constants.INVALID_NOTIF;
                }
        }
    }

    //Only server info is read from file
    private static void readTopology(String filename) {
        //At this point file name is valid or validation can occur here
        try {
            File f = new File(filename);
            Scanner fs = new Scanner(f);
            String[] serverInfo;
            
            while (fs.hasNextLine()) {
                serverInfo = fs.nextLine().split(" ");
                if(Constants.IP.equals(serverInfo[1]) && (Integer.valueOf(serverInfo[2]).intValue() == Constants.PORT)) continue; //skips if serverinfo is itself, avoids connecting to self

                Socket connToServer = new Socket(serverInfo[1], Integer.valueOf(serverInfo[2]).intValue());
                System.out.println(Constants.connectedTo(serverInfo[1], serverInfo[2]));
                Node serverNode = new Node(Integer.valueOf(serverInfo[0]).intValue(),
                    serverInfo[1], Integer.valueOf(serverInfo[2]).intValue(),
                    connToServer, new BufferedReader(new InputStreamReader(connToServer.getInputStream())),
                    new PrintWriter(connToServer.getOutputStream()));
                
                serverNode.start();
                serverNode.sendMessage(Constants.CONNECTION_FROM);
                connectionToServers.add(serverNode);
            }
            fs.close();
        } catch (Exception e) {
            System.out.println(Constants.VAUGE_ERROR);
            e.printStackTrace();
        }
    }

    public static void update()
    {

    }
}
