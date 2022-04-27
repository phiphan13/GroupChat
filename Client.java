/** 
    Author@: Phi Phan
    CSCI 4311
    Programming Assignment 1: Socket Programming 

    Goal of Assignment: Build a simple group chat application.

    Goals of Client:
        * Able to have multiple clients
        * Clients can talk to each other and display onto server
        * if user types Bye, types goodbye user

*/
import java.io.*;
import java.net.*;

public class Client {
    
    //Start of Client socket
    public Client(String address, int port){
        try (Socket socket = new Socket(address, port)) {
            
            //displays serverIp and Port
            System.out.println("Server IP: " + socket.getInetAddress());    //grabs IP
            System.out.println("Server port: " + socket.getPort());         //grabs port
            
            //creates outputs for user to see
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader outputs = new BufferedReader(new InputStreamReader(System.in));
            
            //begins threads to read messages
            readServer s1 = new readServer(socket);
            new Thread(s1).start();
            
            //messages are displayed on servers
            String line = "";
            System.out.println("Type \"Bye\" to exit the program or \"AllUsers\" to see current users.");

            //if client says Bye, disconnect Client and display Goodbye (User) in the server.
            while (!line.equals("Bye")){
                try {
                    line = outputs.readLine();
                    out.println(line);
                } catch (IOException e){
                }
            }
            socket.close();
            outputs.close();
            out.close();

            //end case if there is no server
        } catch (IOException e) {
            System.out.println("No server connection.");
        }
    }
    
    public class readServer implements Runnable{
        private Socket serverSocket;
        private BufferedReader in;
        
        //reads the server
        public readServer(Socket socket) throws IOException {
            serverSocket = socket;
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        }
        
        @Override
        public void run(){
            // displays messages from the server
            try {
                while (true) {
                String message = in.readLine();
                
                if (message == null) {
                    break;
                }
                
                System.out.println(message + "\n");
                }
            } catch (IOException e) {
            } finally {
                try {
                    serverSocket.close();
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    //connects client to serverport
    public static void main(String[] args) {
        Client client = new Client("localhost", 8989);
    }
}