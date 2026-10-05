package server;

import DataBase.DataBaseConnection;
import common.ChatRemote;
import common.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatRemote {

    private HashMap<String, ClientCallback> clients=new HashMap<>();//(online)
    private List<String> allUsers = new ArrayList<>();// pour charger les usernames depuis base donne (offline)

    public ChatServiceImpl() throws RemoteException {
        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        try {

            String sql = "SELECT username FROM user";
            PreparedStatement ps = DataBaseConnection.makeConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                allUsers.add(rs.getString("username"));
            }

            System.out.println("Utilisateurs chargés depuis BD : " + allUsers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized boolean registrer(String username, ClientCallback client) throws RemoteException {


        if (clients.containsKey(username)) {
            return false;
        }

        clients.put(username, client);


        System.out.println(username + " connecté");
        return true;
    }


    @Override
    public void envoyerMessage(String username, String message) throws RemoteException {

        if (message.startsWith("@tous")) {

            String msg = message.replace("@tous", "").trim();
            String full = "[ALL] " + username + " : " + msg;


            try {
                PreparedStatement ps = DataBaseConnection.makeConnection().prepareStatement("INSERT INTO message(sender, receiver, content, type) VALUES(?,?,?,?)");
                ps.setString(1, username);
                ps.setString(2, "ALL");
                ps.setString(3, msg);
                ps.setString(4, "GLOBAL");
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            for (ClientCallback c : clients.values()) {
                c.recevoirMessage(full);
            }
        } else if (message.startsWith("@")) {
            String[] parts = message.split(" ", 2);
            String target = parts[0].substring(1);
            String msg;
            if (parts.length > 1) {
                msg = parts[1];
            } else {
                msg = "";
            }
            String full = "[PRV] " + username + " : " + msg;

            String sql = "insert into message (sender,receiver,content,type) values (?,?,?,?)";
            try {
                PreparedStatement ps1 = DataBaseConnection.makeConnection().prepareStatement(sql);
                ps1.setString(1, username);
                ps1.setString(2, target);
                ps1.setString(3, msg);
                ps1.setString(4, "PRIVATE");
                ps1.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            if (clients.containsKey(target)) {
                clients.get(target).recevoirMessage(full);

            }
        } else {
            String full = username + " : " + message;

            for (ClientCallback c : clients.values()) {
                c.recevoirMessage(full);
            }
        }
    }

    @Override
    public List<String> getUsers() throws RemoteException {
        List<String> result = new ArrayList<>();

        for (String user : allUsers) {
            if (clients.containsKey(user)) {
                result.add(user + " (online)");
            } else {
                result.add(user + " (offline)");
            }
        }

        return result;
    }

    @Override
    public List<String> getHistorique(String username) throws RemoteException {
        List<String> messages = new ArrayList<>();

        try {
            String sqlUser = "SELECT is_created_at FROM user WHERE username=?";
            PreparedStatement psUser = DataBaseConnection.makeConnection().prepareStatement(sqlUser);
            psUser.setString(1, username);
            ResultSet rsUser = psUser.executeQuery();

            Timestamp createdAt = null;
            if (rsUser.next()) {
                createdAt = rsUser.getTimestamp("is_created_at");
            }



            String sql = "SELECT * FROM message WHERE " +
                    "(sender=? OR receiver=? OR receiver='ALL') " +
                    "AND date_message >= ? ORDER BY id ASC";

            PreparedStatement ps = DataBaseConnection.makeConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setTimestamp(3, createdAt);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                String content = rs.getString("content");

                if (receiver.equals("ALL")) {
                    messages.add("[ALL] " + sender + " : " + content);
                }else if (receiver.equals(username)){

                    messages.add("[PRV] " + sender + " : " + content);
                }
                else{
                    messages.add(sender +" to "+receiver+ " : " + content);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;

    }

    @Override
    public synchronized void logout(String username) throws RemoteException {
        clients.remove(username);
        System.out.println(username + " déconnecté");
        for (ClientCallback c : clients.values()) {
            c.recevoirMessage("[SYSTEM] " + username + " est hors ligne");
        }
    }


    //test s il exite dans la bd
    @Override
    public boolean login(String username, String password) throws RemoteException {


        try {
            String sql = "SELECT * FROM user WHERE username=? AND password=?";
            PreparedStatement ps = DataBaseConnection.makeConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //ajout dans la base de donne
    @Override
    public boolean registerUser(String username, String password) throws RemoteException {

        try {
            // existance dans la bd ?
            String check = "SELECT * FROM user WHERE username=? AND password=?";
            PreparedStatement ps1 = DataBaseConnection.makeConnection().prepareStatement(check);
            ps1.setString(1, username);
            ps1.setString(2, password);

            ResultSet rs = ps1.executeQuery();

            // si oui -> cree
            if (!rs.next()) {
                String sql = "INSERT INTO user(username, password) VALUES(?,?)";
                PreparedStatement ps = DataBaseConnection.makeConnection().prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                allUsers.add(username);
                return true;

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


}
