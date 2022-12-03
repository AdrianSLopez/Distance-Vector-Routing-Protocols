import java.io.*;
import java.net.*;
import java.util.*;

public class Server{ 
    private static ServerSocket serverSocket;
    private static DatagramSocket dgSocket;
    private static Scanner s;
    private static boolean startupCmdEntered = false;
    private static int invalidUserInputCount = 1;
    private static List<Node> connectedToUs = new ArrayList<Node>();        // Servers that connect to us
    private static List<Node> connectionToServers = new ArrayList<Node>();  // Servers from topology file
    private static Map<Node, Integer> routingTable = new HashMap<>();
    private static Set<Node> neighbors = new HashSet<Node>();
    public static Map<Node,Node> nextHop = new HashMap<Node, Node>();
    private static MessageFormat message = new MessageFormat();
    private static MessageFormat messageReceived;
    private static int timerInterval;
    private static int id;
    public static int packets;
    
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(Constants.PORT);
            dgSocket = new DatagramSocket(Constants.PORT);

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

            Thread receivePackets = new Thread(new Runnable() {
                public void run() {
                    try {
                        DatagramPacket packetHolder = new DatagramPacket(new byte[254], 254);
                        ByteArrayInputStream bi;
                        ObjectInputStream oi;
                        while(true) {
                            dgSocket.receive(packetHolder);
                            packets++;
                            bi = new ByteArrayInputStream(packetHolder.getData());
                            oi = new ObjectInputStream(bi);
                            Server.messageReceived = (MessageFormat) oi.readObject(); //Object
                            oi.close();
                            bi.close();
                            updateRoutingTable(messageReceived.getServerUpdates());
                            System.out.println(Server.messageReceived);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receivePackets.start();
           
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
                readTopology(input[2]);
                timerInterval = (Integer.parseInt(input[4]) * 1000);
                beginRoutingPacketSending();
                return "";
            case "update": //update <server-id1> <server-id2> <Cost>
                if(id == Integer.parseInt(input[1])) {
                    update(getNodeById(Integer.parseInt(input[2])), Integer.parseInt(input[3]));
                }else if(id == Integer.parseInt(input[2])) {
                    update(getNodeById(Integer.parseInt(input[1])), Integer.parseInt(input[3]));
                }
                else{
                    System.out.println("<update> Error: None of these servers are your server");
                }
                return "updating SUCCESS";
            case "step":
                sendPacket();
                return "Routing Update Success";
            case "packets":
                return "Packets: "+packets;
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

    public static void update(Node neigborNode, int cost)
    {

        try{
            if(isSeverNeighbor(neigborNode)){
                routingTable.replace(neigborNode, routingTable.get(neigborNode), cost);
                message.updateLinkCost(neigborNode, cost);
            }
            else{
                System.out.println("<update> Error: Server " + neigborNode.getServerID() + " isn't your neigbor");
            }
        }catch(Exception e){
            System.out.println("<update> Error: server "+ neigborNode.getServerID() +" does not exist");
        }
    }

    public static boolean isSeverNeighbor(Node server){
        if (neighbors.contains(server)){
            return true;
        }
        return false;
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
                    if(entry.getKey().getServerID() == id){
                        entry.getKey().getConnection().close();
                        routingTable.remove(entry.getKey());
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
        }catch(ConcurrentModificationException e){
            System.out.println("Socket closed.");
        }
        catch(IOException e){
            System.out.println("Invalid.");
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
        }catch(ConcurrentModificationException e){
            System.out.println("<disable> Error: Socket Closed");
        }
        catch(IOException e){
            System.out.println("Not Valid.");
        }
    }
    private static void beginRoutingPacketSending() {
        Thread periodicUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try{
                        Thread.sleep(Server.timerInterval);
                        sendPacket();
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        periodicUpdate.start();
    }
    private static void sendPacket() {
        try {
            DatagramSocket socket = new DatagramSocket();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte [] data = bos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length);

            for(Node n: neighbors) {
                socket.connect(InetAddress.getByName("localhost"), n.getServerPort());
                socket.send(packet);
            }
            
            socket.close();
        }catch(Exception e) {
            e.printStackTrace();
        }        
    }
    private static void updateRoutingTable(List<String> serverUpdates) {
        if(serverUpdates.size() == 0) return;

        String[] i;
        Node n;

        for(String serverInfo: serverUpdates) {
            i = serverInfo.split(" ");
            n = getNodeById(Integer.parseInt(i[2]));

            if(neighbors.contains(n)){
                routingTable.replace(n, routingTable.get(n), Integer.parseInt(i[3]));
            }else {
                continue;
            }
        }
    }
}
