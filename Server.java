import java.io.*;
import java.net.*;
import java.util.*;

public class Server{ 
    private static int port = 8080;
    protected static String ip = "192.168.1.189";
    protected static ServerSocket serverSocket;
    private static Scanner s;
    private static boolean startupCmdEntered = false;
    private static int invalidUserInputCount = 1;
    private static List<Node> connectedToUs = new ArrayList<Node>();        // Servers that connect to us
    private static List<Node> connectionToServers = new ArrayList<Node>();  // Servers from topology file

    public Server(int port) {
        Server.port = port;
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(introMsg());

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
                            // Client is able to send and receive msgs from server that connected to us.
                                // To handle msgs sent and received, might have to be done in the Client class.
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
        // Verify user input
        String[] input = userInput.split(" ");
        String helpNotif = "Use 'help' command to view a list of available command(s).";
        String invalidNotif = "\u001B[31m" + "Invalid input." + "\u001B[0m";

        switch(input[0]) {
            case "help": 
                return (!startupCmdEntered)? help1(): help2();
            case "server":
                if(startupCmdEntered){
                    invalidUserInputCount++;

                    if(invalidUserInputCount%3 == 0) {
                        return invalidNotif + " " + helpNotif;
                    } else {
                        return invalidNotif;
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
            
            default:
                invalidUserInputCount++;

                if(invalidUserInputCount%4== 0) {
                    return invalidNotif + " " + helpNotif;
                } else {
                    return invalidNotif;
                }
        }
    }

    private static String introMsg() {
        return 
        """
                             WELCOME TO
        ===========================================================
        |                                                         |  
        |    SSSSSS  EEEEEE  RRRRR   V       V EEEEEE  RRRRR      |
        |    S       E       R    R   V     V  E       R    R     |
        |    SSSSSS  EEEE    RRRRR     V   V   EEEE    RRRRR      |
        |         S  E       R    R     V V    E       R    R     |
        |    SSSSSS  EEEEEE  R     R     V     EEEEEE  R     R    |
        |                                                         |
        |                                              Port:""" + port + """
          |
        =========================================================== 
         Use 'help' command to view a list of available command(s).                                                   
                """;
    }

    private static String help1() {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_GREEN = "\u001B[32m";
        String param1 = ANSI_GREEN + " <topology file>" + ANSI_RESET;
        String param2 = ANSI_BLUE + " <routing-update-interval> " + ANSI_RESET;

        return """
            \tserver -t """ + param1 + " " + """
                            -i """ + param2 + "\n                 " + param1 + """
                                                    The topology file contains the initial topology configuration for the server, e.g., timberlake_init.txt. 
                                    """ + "                 " + param2 + """
                                                 It specifies the time interval between routing updates in seconds.
                                            """;
    }

    private static String help2() {
        return """
                \tupdate    .....
                        step      .....
                        packets   .....
                        display   .....
                        crash     .....
                                     """;
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
                if(Server.ip.equals(serverInfo[1]) && (Integer.valueOf(serverInfo[2]).intValue() == Server.port)) continue; //skips if serverinfo is itself, avoids connecting to self

                Socket connToServer = new Socket(serverInfo[1], Integer.valueOf(serverInfo[2]).intValue());
                System.out.println("(" + serverInfo[1] + ", " + serverInfo[2] + ") connected");
                Node serverNode = new Node(Integer.valueOf(serverInfo[0]).intValue(), serverInfo[1], Integer.valueOf(serverInfo[2]).intValue(), connToServer, new BufferedReader(new InputStreamReader(connToServer.getInputStream())), new PrintWriter(connToServer.getOutputStream()));
                
                serverNode.start();
                serverNode.sendMessage("(" + ip + ", " + port + "): " + "AUTOMATED MESSAGE FOR TESTING PURPOSE");
                connectionToServers.add(serverNode);
            }
            fs.close();

        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
