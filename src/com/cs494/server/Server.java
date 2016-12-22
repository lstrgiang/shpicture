package com.cs494.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giangle on 12/9/16.
 */
public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static List<ClientThread> threads;
    private static int port = 2222;
    private static int maxClient = 10;
    public static void main(String args[]){
        establishConnection();
        DatabaseService.start();
        threads = new ArrayList<ClientThread>();
        try {
            acceptClientThread();
        }catch(IOException e){
            System.out.println("IOException when accepting clients");
            e.printStackTrace();
        }
    }
    public static void closeThread(int index) throws IOException {
        threads.get(index).interrupt();
        threads.remove(index);
    }
    private static void acceptClientThread() throws IOException {
        while (true) {
            clientSocket = serverSocket.accept();
            if (threads.size() < maxClient) {
                int index = threads.size() -1 >= 0 ? threads.size()-1 : 0;
                threads.add(new ClientThread(clientSocket, threads,index));
                threads.get(threads.size() - 1).start();
                System.out.println("Established new connection, available connection: "+String.valueOf(maxClient - threads.size()));
            } else {
                PrintStream os = new PrintStream(clientSocket.getOutputStream());
                os.println("service is busy");
                os.close();
                clientSocket.close();
            }
        }
    }
    private static void establishConnection()  {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port "+String.valueOf(port));
        } catch (IOException e) {
            System.out.println("Cannot create service");
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Cannot close the service");
                e.printStackTrace();
            }
        }
    }

}

