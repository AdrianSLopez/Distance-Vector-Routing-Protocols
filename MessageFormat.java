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

    public void updateLinkCost(Node server,  int cost){
        this.numFields++;
        serverUpdates.add(server.getServerIP() + "" + server.getServerPort() + " " + server.getServerID() + " " + cost);
    }
}