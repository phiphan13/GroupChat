/* 
    Author@: Phi Phan
    CSCI 4311
    Programming Assignment 1: Socket Programming 

    Goal of Assignment: Build a simple group chat application.

    Goals of Server:
        * Must start on a known port 
        * Broadcast everyone
        * Multithreaded 
        

*/
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private static ArrayList<ClientHandler> clientsList = new ArrayList<>(); ;  //array to hold clients
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static ArrayList<String> usersList = new ArrayList<>();             //arraylist to hold users
    
    //start of server
    public Server (int port) {
        
        try {
            // this opens the server on the port 
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Scanning for projects...");
                
            //listens for clients 
            while (true) {
                Socket client = serverSocket.accept();
                
                //create new clients object
                ClientHandler clientSocket = new ClientHandler(client, clientsList);

                //Thread begins to handle client
                clientsList.add(clientSocket);
                pool.execute(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("ServerStart Failed");
        }
    }
    
    //start of client threads
    public class ClientHandler implements Runnable {
        
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private ArrayList<ClientHandler> clientsList;
        
        //clientHandler creates input and output connections for each thread
        public ClientHandler(Socket client, ArrayList<ClientHandler> threads) throws IOException {
            this.clientSocket = client;
            this.clientsList = threads;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        
        @Override
        public void run(){
            try {
                
                //prompts Client to enter username store in username
                out.println("Enter the username using the following format 'username = Bob'");
                String splituser = in.readLine();
                String[] username = splituser.split(" = ", 0);
                if (username[0].equals("username")){
                    usersList.add(username[1]);

                    //Welcomes clients
                    broadcastAll("Welcome " + username[1]);
                    System.out.println(username[1] + " has connected.");
                
                    //if the user enters Bye, Client exits
                    String line = "";
                    while (!line.equals("Bye")) {
                        try {
                            line = in.readLine();
                            
                            if (line.equals("AllUsers")) {
                                out.println(usersList);
                            }
                            
                            broadcastAll(username[1] + ": " + line);
                            System.out.println(username[1] + ": " + line);
                        } catch (Exception e) {
                        }
                    }
                    //Once Client exits, prompts Server and Clients 
                    broadcastAll("Server: Goodbye " + username[1]);
                    usersList.remove(username[1]);
                }
                
                //if username isn't correct, prompts user
                out.println("Username was not entered correctly. Please exit the program.");
                clientSocket.close();
                in.close();
                out.close();

                //prompts server that the user left
                System.out.println(username[1] + " disconnected with a Bye message.");
            } catch(Exception e) {
            }
        }
        
        private void broadcastAll(String msg) {
            for (ClientHandler aClient : clientsList) {
                aClient.out.println(msg);
            }
        }
    }
    
    public static void main(String[] args) {
        Server server = new Server(8989);
    }
}