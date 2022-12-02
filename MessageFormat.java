import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MessageFormat implements Serializable{
    public int id;
    private int numFields;
    private String ipAdd;
    private int port; 
    private List<String> routingTable = new ArrayList<String>();
    private int cost;

    public MessageFormat(){};

    public MessageFormat(int id, String ipAdd, int port, int cost){
        super();
        this.id = id; 
        this.ipAdd = ipAdd;
        this.port = port;
        this.cost= cost;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getIpAddress(){
        return ipAdd;
    }
    public void setIpAddress(String ipAdd) {
		this.ipAdd = ipAdd;
	}


    public int getNumFields(){
        return numFields;
    }
    public void setNumFields(int numFields){
        this.numFields = numFields;
    }

    
    public int getPort(){
        return port;
    }
    public void setPort(int port){
        this.port = port;
    }

    
    public int getCost(){
        return cost;
    }


    public void setType(int cost){
        this.cost = cost ;
    }

    public List<String>getRoutingTable(){
        return routingTable;
    }


    public void setRoutingTabel(List<String> routingTable){
        this.routingTable = routingTable;
    }

}