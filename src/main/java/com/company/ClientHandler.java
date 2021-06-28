package com.company;

import com.sun.tools.javac.Main;
import mankalaDB.PolaczBD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    public PrintWriter out;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);
    }

    @Override
    public void run(){
        try {
            StringBuilder listOfMatches = new StringBuilder();
            listOfMatches.append(Messages.Server.Matches+";");

            for (Player player:Server.clients) {
                if(Server.matches.stream().anyMatch(x->((x.playerWhite.client == player.client) && (x.playerBlack == null)))){
                    listOfMatches.append(player.name+";");
                }
            }

            out.println(listOfMatches);

            while (true) {
                String request[] = in.readLine().split(";");

                Arrays.stream(request).forEach(x-> System.out.print(x));
                System.out.println();

                switch(request[0])
                {
                    case Messages.Client.Host: // action;socket;id;name
                        if(!Server.matches.stream().anyMatch(x-> x.playerWhite.client == this))
                        {
                            Server.matches.add(new Match(Server.clients.stream().filter(x -> x.client == this).toList().get(0)));
                        }
                        else
                        {
                            Server.matches.removeIf(x-> x.playerWhite.client==this);
                        }
                        sendListOfMatches();
                        break;
                    case Messages.Client.Join: // action;socket1;socket2 (tego gracza);id;login

                        break;
                    case Messages.Client.Login: // action;login;haslo
                        String sql = "SELECT id, imie, nazwisko FROM dane_studentow WHERE imie='"+request[1]+"' AND nazwisko='"+request[2]+"';";
                        try {
                            ResultSet resultSet = PolaczBD.pobierzDane(sql);

                            if(resultSet.next()){
                                System.out.println("Poprawnie zalogowano gracza" );
                                Server.clients.stream().filter(x-> x.client == this).findFirst().get().name = request[1];
                            }
                            else {
                                System.out.println("Niepoprawne dane logowania");
                                out.println(Messages.Server.Disconnect);
                            }
                            //out.println(client.getPort()+Messages.Server.Disconnect);
                        } catch (SQLException throwables) {

                        }
                        break;
                    case Messages.Server.Matches:

                        break;
                    default:
                        System.out.println("Nierozpoznana akcja");
                        break;
                }
            }
        } catch (IOException e){
            System.err.println("IOException in ClientHandler");
            System.err.println(e.getStackTrace());
        }catch (NullPointerException e){

        } finally{
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.clients.removeIf(x-> x.client == this);
            Server.matches.removeIf(x-> x.playerWhite.client==this || x.playerBlack.client==this);
            System.err.println("Gracz został rozłączony. Aktualna liczba graczy ->" + Server.clients.size());
        }
    }

    private void sendListOfMatches()
    {
        StringBuilder listOfMatches = new StringBuilder();
        listOfMatches.append(Messages.Server.Matches+";");

        for (Player player:Server.clients) {
            if(Server.matches.stream().anyMatch(x->((x.playerWhite.client == player.client) && (x.playerBlack == null)))){
                listOfMatches.append(player.name+";");
            }
        }

        System.out.println(":)" + listOfMatches);

        for (Player player:Server.clients) {
            player.client.out.println(listOfMatches);
        }

    }
}
