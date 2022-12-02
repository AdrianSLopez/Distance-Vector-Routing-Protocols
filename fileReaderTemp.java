import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimerTask;

public class fileReaderTemp {
	static String myIP = "";
	static int myID = Integer.MIN_VALUE+2;
	public static List<Node> nodes = new ArrayList<Node>();
	public static Node myNode = null;
	public static Map<Node,Integer> routingTable = new HashMap<Node,Integer>();
	public static Set<Node> neighbors = new HashSet<Node>();
	public static Map<Node,Node> nextHop = new HashMap<Node,Node>();
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("server <topology-file>");
		boolean run = true;
		
		while(run) {
			String textInput = in.nextLine();
			String[] command = textInput.split(" ");
			String word = command[0];

				if(command.length != 2){
					System.out.println("Incorrect command. Please try again.1");
				}
				else if((command[1]=="")){
					System.out.println("Incorrect command. Please try again.2");
				}
				else {
					String filename = command[1];
					readFile(filename);
				}
			}
		
		in.close();
	}
	
	public static void readFile(String filename) {
		
		File file = new File(filename);
		
		try {
			Scanner scanner = new Scanner(file);
			int serversNum = scanner.nextInt();
			int neighborsNum = scanner.nextInt();
			scanner.nextLine();
			
			for(int i = 0 ; i < serversNum; i++) {
				String line = scanner.nextLine();
				String[] serversInfo = line.split(" ");
				
				System.out.println("Node(Integer.parseInt(" + serversInfo[0] + "), " + serversInfo[1] + ", Integer.parseInt(" + serversInfo[2] + "));"); //reading test
				
				Node node = new Node(Integer.parseInt(serversInfo[0]), serversInfo[1], Integer.parseInt(serversInfo[2]));
				nodes.add(node);
				int cost = Integer.MAX_VALUE-2;
				if(serversInfo[1].equals(myIP)) {
					myID = Integer.parseInt(serversInfo[0]);
					myNode = node;
					cost = 0;
					nextHop.put(node, myNode);
				}
				else{
					nextHop.put(node, null);
				}
				routingTable.put(node,cost);
//				connect(serversInfo[1], Integer.parseInt(serversInfo[2]),myID); //don't need this yet
			}
			for(int i = 0 ; i < neighborsNum;i++) {
				String line = scanner.nextLine();
				String[] serversInfo = line.split(" ");
				int fromID = Integer.parseInt(serversInfo[0]);
				int toID = Integer.parseInt(serversInfo[1]); 
				int cost = Integer.parseInt(serversInfo[2]);
				
				System.out.println("neighbor"+ i +": fromID " + fromID + ", toID " + toID + ", cost " + cost + "."); //reading test

				if(fromID == myID){
					Node to = getNodeById(toID);
					routingTable.put(to, cost);
					neighbors.add(to);
					nextHop.put(to, to);
				}
				if(toID == myID){
					Node from = getNodeById(fromID);
					routingTable.put(from, cost);
					neighbors.add(from);
					nextHop.put(from, from);
				}
			}
			System.out.println("Topology file read.");
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println(file.getAbsolutePath()+" not found.");
		}
	}
	
	public static Node getNodeById(int id){
		for(Node node:nodes) {
			if(node.getServerID() == id) {
				return node;
			}
		}
		return null;
	}
	
	public static void display() {
		
	    	System.out.printf("----------------------------------------------------------------------------%n");
	    	System.out.printf("|%-11s|%-20s|%-41s|%n", " Line Number ", "     Line Entry", "                 Comments");
	    	System.out.printf("----------------------------------------------------------------------------%n");

	    	System.out.printf("|%-13s|%-20s|%-41s|%n", "Floating", "double",  64);

	    	System.out.printf("----------------------------------------------------------------------------%n");    
	}
	

}