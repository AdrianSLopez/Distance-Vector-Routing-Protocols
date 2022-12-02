import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MessageFormat implements Serializable{
    private int numFields = 0;
    private String ipAdd = Constants.IP;
    private int port = Constants.PORT; 
    private List<String> serverUpdates = new ArrayList<String>();

    public MessageFormat(){
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

    public List<String>getServerUpdates(){
        return serverUpdates;
    }
    
    public void setServerUpdates(List<String> serverUpdates){
        this.serverUpdates =serverUpdates;
    }

    public void updateLinkCost(Node server1, Node server2, int cost){
        this.numFields++;

        if(Constants.IP == server1.getServerIP()) {
            serverUpdates.add(server2.getServerIP() + "" + server2.getServerPort() + " " + server2.getServerID() + " " + cost);
        } else {
            serverUpdates.add(server1.getServerIP() + " " + server1.getServerPort() + " " + server1.getServerID() + " " + cost);
        }
    }
}