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
    private static Map<Node, Integer> routingTable = new HashMap<>();
    private static Set<Node> neighbors = new HashSet<Node>();
    public static Map<Node,Node> nextHop = new HashMap<Node, Node>();
    static int id;

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
                return "updating SUCCESS";
            case "step":
            step(Integer.parseInt(input[4]));
                return "stepping SUCCESS";
            case "packets":
                return "packets SUCCESS";
            case "display":
                display();
                return Constants.GREEN + "\n<display> SUCCESS" + Constants.RESET;
            case "disable":
                return disable(Integer.parseInt(input[1]));
            case "crash":
                crash();
                return "crash SUCCESS";
            default:
                invalidUserInputCount++;

                if(invalidUserInputCount%4== 0) {
                    return Constants.INVALID_AND_HELP_NOTIF;
                } else {
                    return Constants.INVALID_NOTIF;
                }
        }
    }

    private static void readTopology(String filename) {
        //At this point file name is valid or validation can occur here
        try {
            File f = new File(filename);
            Scanner fs = new Scanner(f);
            String[] info;
            int serversNum = fs.nextInt();
            int neighborsNum = fs.nextInt();
            fs.nextLine();

            for(int i = 0; i < serversNum; i++) {
                info = fs.nextLine().split(" ");
                
                if(Constants.IP.equals(info[1]) && (Integer.valueOf(info[2]).intValue() == Constants.PORT)) {
                    Server.id = Integer.parseInt(info[0]);
                    continue;
                }

                Socket connToServer = new Socket(info[1], Integer.valueOf(info[2]).intValue());
                System.out.println(Constants.connectedTo(info[1], info[2]));
                Node serverNode = new Node(Integer.valueOf(info[0]).intValue(), info[1], Integer.valueOf(info[2]).intValue(), connToServer, new BufferedReader(new InputStreamReader(connToServer.getInputStream())), new PrintWriter(connToServer.getOutputStream()));
                
                serverNode.start();
                serverNode.sendMessage(Constants.CONNECTION_FROM);
                connectionToServers.add(serverNode);
            }

            for(int i = 0; i < neighborsNum; i++) {
                info = fs.nextLine().split(" ");
                int fromID = Integer.parseInt(info[0]);
                int toID = Integer.parseInt(info[1]);
                int cost = Integer.parseInt(info[2]);

                Node neigborNode = (fromID == Server.id)? getNodeById(toID): getNodeById(fromID);

                routingTable.put(neigborNode, cost);
                neighbors.add(neigborNode);
                // nextHop.put(neigborNode, neigborNode);
            }

            fs.close();
        } catch (Exception e) {
            System.out.println(Constants.VAUGE_ERROR);
            e.printStackTrace();
        }
    }

    public static Node getNodeById(int id){
		for(Node node: connectionToServers) {
			if(node.getServerID() == id) {
				return node;
			}
		}
		return null;
	}

    public static void update()
    {
        
        try{

            System.out.println("RECEIVED A MESSAGE FROM SERVER <server-ID>");

        }catch(Exception e){
            System.out.println("<update> Error: server [id] does not exist");
        }
    }
    public static void step(int interval)
    {
        try{

        }catch(Exception e){
            System.out.println("<step> Error....");
        }
    }
    public static void packets()
    {
        try{

        }catch(Exception e){
            System.out.println("<packets> Error....");
        }
    }
    public static void display()
    {
        try{
            ArrayList<Node> tableSorted = new ArrayList<Node>(routingTable.keySet());
            Collections.sort(tableSorted);
            Formatter fm1 = new Formatter();
			fm1.format("%20s %15s %20s \n","Destination Server","Next Hop","Cost of Path");
            fm1.format("%30s", "______________________________________________________________\n");
            for(Node n : tableSorted){
                fm1.format("%15s %18s %20s \n", "<" + id + ">", "<" + n.getServerID() + ">", "<" + routingTable.get(n) + ">");
            }
            System.out.println(fm1);
        }catch(Exception e){
            System.out.println("<display> Error: Could not display all values.");
            System.out.println(e);
        }
    }
    public static String disable(int id)
    {
        String result = "";
        try{

            if (neighbors.contains(getNodeById(id))){
                getNodeById(id).getConnection().close();
                for(int i = 0; i < connectionToServers.size(); i++){
                    if(connectionToServers.get(i).getServerID() == id){
                        connectionToServers.get(i).getConnection().close();
                    }    
                }   
                for(Map.Entry<Node, Integer> entry: routingTable.entrySet()){
                    if(entry.getValue() == id){
                        entry.getKey().getConnection().close();
                    }
                }
                for(Node entry: neighbors){
                    if(entry.getServerID() == id){
                        entry.getConnection().close();
                    }
                 }
                result = "<disable> SUCCESS";       
            } else{
                result = "<disable> Cannot disable " + id;
            }
        }catch(IOException e){
            System.out.println(e);
        }
        return result;
    }
    public static void crash()
    {
        try{
            for(int i = 0; i < connectedToUs.size(); i++){
                connectedToUs.get(i).getConnection().close();
            }
            for(int i = 0; i < connectionToServers.size(); i++){
                connectionToServers.get(i).getConnection().close();
            }
            for(Node entry: neighbors){
               entry.getConnection().close();
            }
            for(Map.Entry<Node, Integer> entry: routingTable.entrySet()){
                routingTable.replace(entry.getKey(), entry.getValue(),null); 
            }
        }catch(Exception e){
            System.out.println("<disable> Error");
        }
    }
}
