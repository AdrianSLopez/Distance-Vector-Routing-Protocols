import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MessageFormat implements Serializable{
    private static final long serialVersionUID = 565656L;
    private int numFields = 0;
    private String ipAdd = Constants.IP;
    private int port = Constants.PORT; 
    private List<String> serverUpdates = new ArrayList<String>();

    public MessageFormat(int numFields, String ipAdd, int port, List<String> serverUpdates) {
        this.numFields = numFields;
        this.ipAdd = ipAdd;
        this.port = port;
        this.serverUpdates = serverUpdates;
    }

    public MessageFormat() {
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

    @Override
    public String toString() {
        return "Message from " + this.ipAdd + ":" + this.port + " with " + numFields + " updates!";
    }
}