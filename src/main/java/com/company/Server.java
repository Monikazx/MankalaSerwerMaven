package com.company;


import javax.net.ssl.SSLServerSocket;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    private static final int port = 8001;
    private static final String server = "127.0.0.1";
    private static Scanner scanner = new Scanner(System.in);
    private static ServerSocket serverSocket = null;



    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static boolean serverIsRunning = true;



    public static void main(String[] args) throws IOException {
        ServerSocket listener= new ServerSocket(port);
        while (true) {
            System.out.println("Server is waiting for client connection... ");
            Socket client = listener.accept();
            System.out.println("Connected to client... ");

            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }



}