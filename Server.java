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
    private static boolean startupCmdEntered = false;

    public Server(int port) {
        Server.port = port;
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("SERVER STARTED");

            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // To do steps
                // Send following to clients:
                    // Intro message along with startup command info
                        // be able to display command info
                        // handle user input in receive thread
                    // Create nodes from topology.txt
                    // Aswell with other Proj 2 requirements. Example: Update based on user input
                    // Display and listen for user avaailable commands
                    // Execute based on user command input
            
            out.println(introMsg());
            out.flush();

            // Server receives from the client from the following
            Thread receive = new Thread(new Runnable() {
                String msg ;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();

                        while(msg!=null){
                            System.out.println(msg);
                            out.println(executeCommand(msg.substring(8)));
                            out.flush();
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

    private static String executeCommand(String userInput) {
        switch(userInput) {
            case "help": 
                return (!startupCmdEntered)? help1(): help2();
            case "server":
                //READ TOPOLOGY FILE AND TIME INTERVAL
                    // Create node class
                    // for every server inputted in .txt file create node object
                    // connect to each server?? I think
                return "";
            
            default:
                return "\u001B[31m" + "Invalid input" + "\u001B[0m";
        }
    }

    private static String help1() {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_GREEN = "\u001B[32m";
        String param1 = ANSI_GREEN + " <topology file>" + ANSI_RESET;
        String param2 = ANSI_BLUE + " <routing-update-interval> " + ANSI_RESET;

        return """
             server -t """ + param1 + " " + """
                     -i """ + param2 + "\n\n         " + param1 + """
                                             The topology file contains the initial topology configuration for the server, e.g., timberlake_init.txt. 
                             """ + "         " + param2 + """
                                          It specifies the time interval between routing updates in seconds.
                                     """;
    }

    private static String help2() {
        return """
            update    .....
            step      .....
            packets   .....
            display   .....
            crash     .....
                                     """;
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
}
