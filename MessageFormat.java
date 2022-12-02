import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MessageFormat implements Serializable{
    public int id;
    private int numFields;
    private String ipAdd;
    private int port; 
    private List<String> serverUpdates = new ArrayList<String>();
    private int cost;

    public MessageFormat(){};

    public MessageFormat(int id, String ipAdd, int port, int cost){
        super();
        this.id = id; 
        this.ipAdd = Constants.IP;
        this.port = Constants.PORT;
        this.cost= cost;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getIpAddress(){
        return this.ipAdd;
    }
    public int getPort(){
        return port;
    }
    public int getNumFields(){
        return numFields;
    }
    public void setNumFields(int numFields){
        this.numFields = numFields;
    }
    
    public int getCost(){
        return cost;
    }

    public void setType(int cost){
        this.cost = cost ;
    }

    public List<String>getServerUpdates(){
        return serverUpdates;
    }

    public void setServerUpdates(List<String> serverUpdates){
this.serverUpdates = serverUpdates;
    }
    public void addtoServerUpdates(String server1, String server2, int cost){
        this.numFields++;
        String msg = server1 + " to " +server2 +"  cost is" + Integer.toString(cost);
        serverUpdates.add(msg);
    }
}