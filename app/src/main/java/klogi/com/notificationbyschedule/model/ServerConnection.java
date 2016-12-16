package klogi.com.notificationbyschedule.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alaeddine on 28/09/16.
 */
public class ServerConnection {
    private int id;
    private String ipServer="";
    private String profondeurServer="";
    private String portServer="";
    private String ipServerSecondaire="";

    public ServerConnection(String ipServer, String profondeurServer, String portServer,String ipServerSecondaire) {
        this.ipServer = ipServer;
        this.profondeurServer = profondeurServer;
        this.portServer = portServer;
        this.ipServerSecondaire=ipServerSecondaire;
    }

    public ServerConnection(int id, String ipServer, String profondeurServer, String portServer,String ipServerSecondaire) {
        this.id = id;
        this.ipServer = ipServer;
        this.profondeurServer = profondeurServer;
        this.portServer = portServer;
        this.ipServerSecondaire=ipServerSecondaire;
    }
    public ServerConnection(String ipServer, String portServer,String profondeurServer ) {

        this.ipServer = ipServer;
        this.portServer = portServer;

    }


    public String getIpServerSecondaire() {
        return ipServerSecondaire;
    }

    public void setIpServerSecondaire(String ipServerSecondaire) {
        this.ipServerSecondaire = ipServerSecondaire;
    }

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public String getProfondeurServer() {
        return profondeurServer;
    }

    public void setProfondeurServer(String profondeurServer) {
        this.profondeurServer = profondeurServer;
    }

    public String getPortServer() {
        return portServer;
    }

    @Override
    public String toString() {
        return "ServerConnection{" +
                "id=" + id +
                ", ipServer='" + ipServer + '\'' +
                ", profondeurServer='" + profondeurServer + '\'' +
                ", portServer='" + portServer + '\'' +
                ", ipServerSecondaire='" + ipServerSecondaire + '\'' +
                '}';
    }

    public void setPortServer(String portServer) {
        this.portServer = portServer;
    }
}

