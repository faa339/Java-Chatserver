/*
Server end of the chatroom
*/
package com.mycompany.hw4farisalotaibi;

import java.io.*;
import java.net.*;
import java.util.*;

public class server {

    /**
     * @param args the command line arguments
     */
    static int portnum = 5190;
    static ArrayList<Connection> Clients;
  
    public static void main(String[] args) {
        ServerSocket ss = null;
        Clients = new ArrayList<Connection>();
        try{
            ss = new ServerSocket(portnum);
            while(true){
                Socket newCl = ss.accept();
                Connection newConnect = new Connection(newCl);
                newConnect.start();
            }
        }catch(IOException e){
            System.out.println("Server error " + e.getMessage());
        }
    }
    
    synchronized public static void broadcast(String Message){
        for(Connection c: Clients){
            c.cout.println(Message);
        }
    }
    synchronized public static void addConnect(Connection c){
        Clients.add(c);
    }
    
    synchronized public static void delClient(Connection c){
        Clients.remove(c);
    }
}

//Client class
class Connection extends Thread{
    Socket client;
    PrintStream cout;
    Scanner cin;
    String username;
    Connection(Socket newclient){
        client=newclient;
        try{
            cout = new PrintStream(client.getOutputStream());
            cin = new Scanner(client.getInputStream());
            cin.nextLine(); //skip first input
            username = cin.nextLine();
            server.addConnect(this);
            cout.println("Connected");
        }catch(IOException e){
            System.out.println("error " + e.toString());
        }
    }

    public void  run(){
        String text;
        
        while(true){
            if(cin.hasNextLine()){
                text = cin.nextLine();
                if(text.equals("exit")){
                    try {
                        client.close();
                    }catch(IOException e){
                        System.out.println("error " + e.toString());
                    }
                    break;
                }else{
                    String message = this.username +": "+ text;
                    server.broadcast(message);
                }
            }
        }
        server.broadcast(username + " disconnected");
        server.delClient(this);
    }
}
