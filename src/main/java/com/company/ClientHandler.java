package com.company;

import mankalaDB.PolaczBD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    public static ArrayList<Player> connectedClients = new ArrayList<Player>();
    public static ArrayList<Match> matches = new ArrayList<Match>();


    public ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);
    }
    @Override
    public void run(){
        try {
            Player player;
            Match match;
            String sql;
            while (true) {
                String request[] = in.readLine().split(";");
                switch(request[0])
                {
                    case Messages.Client.Host: // action;socket;id;name
                        player = new Player(request[1],request[2]);
                        connectedClients.add(player);
                        match = new Match(player);
                        matches.add(match);
                        System.out.println("Gracz "+client+" stworzył nową grę.");
                        break;
                    case Messages.Client.Join: // action;socket1;socket2 (tego gracza);id;login
                        player = new Player(request[2],request[3]);
                        connectedClients.add(player);
                        int opponentsIndex = matches.indexOf(request[0]);
                        matches.get(opponentsIndex).playerBlack = player;
                        out.println(client.getPort()+Messages.Server.Start);
                    case Messages.Client.Login: // action;login;haslo
                        sql = "SELECT id, imie, nazwisko FROM dane_studentow WHERE imie='"+request[1]+"' AND nazwisko='"+request[2]+"';";
                        try {
                            ResultSet resultSet = PolaczBD.pobierzDane(sql);
                            if(resultSet!=null){
                                System.out.println("Poprawnie pobrano dane studenta "+resultSet.findColumn("id")+" "+
                                        resultSet.findColumn("imie") + " " + resultSet.findColumn("nazwisko"));
                                out.println(client.getPort()+Messages.Server.Logged);
                            }
                            //out.println(client.getPort()+Messages.Server.Disconnect);
                        } catch (SQLException throwables) {
                            System.out.println("Błąd podczas sprawdzania czy student istnieje");
                            throwables.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println("Nierozpoznana akcja");
                        break;
                }
            }
        } catch (IOException e){
            System.err.println("IOException in ClientHandler");
            System.err.println(e.getStackTrace());
        } finally{
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
