import java.io.*;
import java.net.*;
import java.util.*;

public class distance_vector_routing{ 
    private static ServerSocket serverSocket;
    private static DatagramSocket dgSocket;
    private static Scanner s;
    private static boolean startupCmdEntered = false;
    private static int invalidUserInputCount = 1;
    private static List<Node> connectedToUs = new ArrayList<Node>();        // Servers that connect to us
    private static List<Node> connectionToServers = new ArrayList<Node>();  // Servers from topology file
    private static Map<Node, Integer> routingTable = new HashMap<>();
    private static Set<Node> neighbors = new HashSet<Node>();
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

                        if(input.startsWith("help")){
                            System.out.println(executeCommand(input));
                            continue;
                        }

                        if(input.startsWith("server") && !startupCmdEntered) {
                            System.out.print(executeCommand(input));
                        }else if(input.startsWith("server") && startupCmdEntered) {
                            System.out.println(executeCommand("SERVER already used"));
                        }else if(!input.startsWith("server") && !startupCmdEntered) {
                            System.out.println(executeCommand("COMMAND IS NOT AN AVAILABLE COMMAND"));
                        }else {
                            System.out.println(executeCommand(input));
                        }
                        
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

            //handle incoming packets
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
                            messageReceived = (MessageFormat) oi.readObject();
                            oi.close();
                            bi.close();
                            updateRoutingTable();
                            System.out.println(Constants.GREEN + "RECEIVED A MESSAGE FROM SERVER " + getNodebyIPandPort(messageReceived.getIpAddress(), messageReceived.getPort()).getServerID() + " WITH " + messageReceived.getServerUpdates().size() + " UPDATE(S)." + Constants.RESET);
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
                try {
                    timerInterval = (Integer.parseInt(input[4]) * 1000);
                    readTopology(input[2]);                   
                    beginRoutingPacketSending(); 
                }catch(ArrayIndexOutOfBoundsException e) {
                    invalidUserInputCount++;
                    return (invalidUserInputCount%4== 0)? Constants.SERVER_FAILURE + " " + Constants.HELP_NOTIFICATION:Constants.SERVER_FAILURE;
                } catch(NumberFormatException e) {
                    invalidUserInputCount++;
                    return (invalidUserInputCount%4== 0)? Constants.SERVER_INVALID_TIMEINTERVAL + " " + Constants.HELP_NOTIFICATION:Constants.SERVER_INVALID_TIMEINTERVAL;
                }
                
                return "";
            case "update": //update <server-id1> <server-id2> <Cost>
                try{
                    if(id == Integer.parseInt(input[1])) {
                        return update(getNodeById(Integer.parseInt(input[2])), Integer.parseInt(input[3]));
                    }else if(id == Integer.parseInt(input[2])) {
                        return update(getNodeById(Integer.parseInt(input[1])), Integer.parseInt(input[3]));
                    }
                    else{
                        return Constants.RED + "<update> Neither server id " + input[1] + " or " + input[2] + " is your server id" + Constants.RESET;
                    }    
                }catch(ArrayIndexOutOfBoundsException e) {
                    return Constants.UPDATE_FAILURE;
                }catch(NumberFormatException e) {
                    return Constants.UPDATE_FAILURE_2;
                }
                
            case "step":
                return sendPacket();
            case "packets":
                return Constants.GREEN + packets + " packet(s) received." + Constants.RESET;
            case "display":
                return display();
            case "disable":
                try{
                    return disable(Integer.parseInt(input[1]));    
                }catch(ArrayIndexOutOfBoundsException e) {
                    return Constants.DISABLE_FAILURE_3;
                }catch(NumberFormatException e) {
                    return Constants.DISABLE_FAILURE_5;
                }               
            case "crash":
                crash();
                return Constants.CRASH_SUCCESS;
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
                    id = Integer.parseInt(info[0]);
                    continue;
                }

                Socket connToServer = new Socket(info[1], Integer.valueOf(info[2]).intValue());
                System.out.println(Constants.connectedTo(info[1], info[2]));
                Node serverNode = new Node(Integer.valueOf(info[0]).intValue(), info[1], Integer.valueOf(info[2]).intValue(), connToServer, new BufferedReader(new InputStreamReader(connToServer.getInputStream())), new PrintWriter(connToServer.getOutputStream()));
                
                serverNode.start();
                serverNode.sendMessage(Constants.CONNECTION_FROM);
                connectionToServers.add(serverNode);
                routingTable.put(serverNode, -1);
            }

            for(int i = 0; i < neighborsNum; i++) {
                info = fs.nextLine().split(" ");
                int fromID = Integer.parseInt(info[0]);
                int toID = Integer.parseInt(info[1]);
                int cost = Integer.parseInt(info[2]);
                
                if(id == fromID || id == toID) {
                    Node neigborNode = (fromID == id)? getNodeById(toID): getNodeById(fromID);

                    routingTable.replace(neigborNode, -1, cost);
                    neighbors.add(neigborNode);
                }else {
                    continue;
                }
            }
            startupCmdEntered = true;
            fs.close();
        } catch(FileNotFoundException e) {
            System.out.println(Constants.SERVER_INVALID_FILE);
        } catch(ConnectException e) {
            System.out.println(Constants.SERVER_OFFLINE);
        } catch(Exception e) {
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

    private static Node getNodebyIPandPort(String ip, int port) {
        for(Node n: connectionToServers) {
            if((n.getServerIP().compareTo(ip) == 0) && (n.getServerPort() == port)) {
                return n;
            }
        }
        return null;
    }

    public static String update(Node neigborNode, int cost) {
        try{
            if(isSeverNeighbor(neigborNode)){
                routingTable.replace(neigborNode, routingTable.get(neigborNode), cost);
                message.updateLinkCost(neigborNode, cost);
                return Constants.UPDATE_SUCCESS;
            }
            else{
                return Constants.RED + "<update> Server " + neigborNode.getServerID() + " isn't your neigbor." + Constants.RESET;
            }
        }catch(NullPointerException e){
            return Constants.RED + "<update> invalid server id." + Constants.RESET;
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
    public static String display() {
        try{
            ArrayList<Node> tableSorted = new ArrayList<Node>(routingTable.keySet());
            Collections.sort(tableSorted);
            Formatter fm1 = new Formatter();
            int cost;
			fm1.format("%20s %15s %20s \n","\nDestination Server","Next Hop","Cost of Path");
            fm1.format("%30s", "______________________________________________________________\n");
            for(Node n : tableSorted){
                cost = routingTable.get(n);
                fm1.format("%10s %20s %21s", "<" + id + ">", "<" + n.getServerID() + ">", "<" + ((cost == -1)? "infin":cost) + ">\n");
            }
            System.out.println(fm1);
            return Constants.DISPLAY_SUCCESS;
        }catch(Exception e){
            return Constants.DISPLAY_FAILURE;
        }
    }
    public static String disable(int id)
    {
        String result = "";
        try{
            Node rip = getNodeById(id);

            if (neighbors.contains(rip)){
                for(int i = 0; i < connectionToServers.size(); i++){
                    if(connectionToServers.get(i).getServerID() == id){
                        connectionToServers.get(i).getConnection().close();
                        connectedToUs.remove(i);
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
                        neighbors.remove(rip);
                    }
                }
                rip.getConnection().close();
                result = Constants.DISABLE_SUCCESS;       
            } else{
                if((distance_vector_routing.id == id))  result = Constants.DISABLE_FAILURE_4;
                if(getNodeById(id) == null) result = Constants.RED + "<disable> no server with id " + id + "." + Constants.RESET;
                if(getNodeById(id) != null) result = Constants.RED + "<disable> server " + id + " is not your neigbor." + Constants.RESET;
            }
        }catch(ConcurrentModificationException e){
            result = Constants.DISABLE_FAILURE_1;
        }
        catch(IOException e){
            result = Constants.DISABLE_FAILURE_2;
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
                routingTable.replace(entry.getKey(), entry.getValue(),-1); 
            }

            System.out.println(Constants.CRASH_MESSAGE);
        }catch(ConcurrentModificationException e){
            System.out.println("<disable> Error: Socket Closed");
        }
        catch(IOException e){
            System.out.println("Not Valid.");
        }

        System.exit(0);
    }
    private static void beginRoutingPacketSending() {
        Thread periodicUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try{
                        Thread.sleep(timerInterval);
                        sendPacket();
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        periodicUpdate.start();
    }
    private static String sendPacket() {
        if(neighbors.size() == 0) return Constants.STEP_NO_NEIGHBORS;
        try {
            DatagramSocket socket;
            DatagramPacket packet;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte [] data = bos.toByteArray();
            
            for(Node n: neighbors) {
                socket = new DatagramSocket();
                packet = new DatagramPacket(data, data.length);
                socket.connect(InetAddress.getByName("localhost"), n.getServerPort());
                socket.send(packet);
                socket.close();
            }
            message.setServerUpdates(new ArrayList<String>());
            message.setnumFields();
            return Constants.STEP_SUCCESS;
        }catch(Exception e) {
            e.printStackTrace();
            return Constants.STEP_FAILURE;
        }        
    }
    private static void updateRoutingTable() {
        if(messageReceived.getServerUpdates().size() == 0) return;
        String[] update;
        Node serverThatSentMessage = getNodebyIPandPort(messageReceived.getIpAddress(), messageReceived.getPort());

        for(String serverInfo: messageReceived.getServerUpdates()) {
            update = serverInfo.split(" ");

            if(id == Integer.parseInt(update[2])) {
                routingTable.replace(serverThatSentMessage, routingTable.get(serverThatSentMessage), Integer.parseInt(update[3]));
            }else {
                continue;
            }
        }
    }
}
