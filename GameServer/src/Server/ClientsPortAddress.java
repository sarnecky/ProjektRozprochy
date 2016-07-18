/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Enums.OneField;
import java.net.InetAddress;

/**
 *
 * @author ArkadiuszSadowski
 */
public class ClientsPortAddress {

    private InetAddress ipAddress;
    private int port;
    private String playerName;
    private Bonus bonus;
    public boolean killed=false;
    public int x,y; 
    public int life=5;
    public OneField field;
    
    public ClientsPortAddress(InetAddress ipAddress, int port, String playerName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.playerName = playerName;
    }

    public InetAddress getIpAddress() {
        return this.ipAddress;
    }

    public int getPort() {
        return this.port;
    }
    
    public String getPlayerName(){
    return this.playerName;
    }
    

}
