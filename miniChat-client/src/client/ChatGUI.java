package client;

import common.ChatRemote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ChatGUI extends JFrame {
    public JTextArea zoneChat ;
    public JTextField champMessage ;
    public JButton envoyer;
    public String username ;
    public ChatRemote service ;

    public JList<String> listusers ;
    public DefaultListModel<String> modelusers ;


    public ChatGUI (String username , ChatRemote service) throws MalformedURLException, NotBoundException, RemoteException {
        this.username = username;
        this.service = service;

        setTitle("Chat - " + username);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🌙 Couleur principale
        Color bgColor = new Color(245, 247, 250);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);

        // 🔷 HEADER
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 150, 136));
        header.setPreferredSize(new Dimension(100, 50));

        JLabel title = new JLabel("💬 Chat - " + username);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(title);

        // 👥 USERS PANEL
        modelusers = new DefaultListModel<>();
        listusers = new JList<>(modelusers);
        listusers.setBackground(Color.WHITE);
        listusers.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane usersScroll = new JScrollPane(listusers);
        usersScroll.setPreferredSize(new Dimension(180, 0));

        // 💬 CHAT AREA
        zoneChat = new JTextArea();
        zoneChat.setEditable(false);
        zoneChat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        zoneChat.setBackground(Color.WHITE);

        JScrollPane chatScroll = new JScrollPane(zoneChat);

        // ✉️ MESSAGE PANEL
        champMessage = new JTextField();
        champMessage.setFont(new Font("Arial", Font.PLAIN, 14));

        envoyer = new JButton("Envoyer");
        envoyer.setBackground(new Color(0, 150, 136));
        envoyer.setForeground(Color.WHITE);
        envoyer.setFocusPainted(false);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottom.add(champMessage, BorderLayout.CENTER);
        bottom.add(envoyer, BorderLayout.EAST);

        // 📦 CENTER PANEL
        JPanel center = new JPanel(new BorderLayout());
        center.add(chatScroll, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);

        // 🔗 ASSEMBLAGE
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(usersScroll, BorderLayout.WEST);
        mainPanel.add(center, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);

        enregistrer(); ;//test pour connaitre est ce qu il est connecté ou non
        chargerHistorique();
        envoyer.addActionListener(e->envoyerMessage());
        champMessage.addActionListener(e->envoyerMessage());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    service.logout(username);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    updateUsers();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();


    }
    public void enregistrer() throws MalformedURLException, NotBoundException, RemoteException {

            ClientImpl clientCallback=new ClientImpl(this);

        try {
           boolean ok = service.registrer(username,clientCallback);
           if (!ok){
               JOptionPane.showMessageDialog(this, "Utilisateur déjà connecté !");
               System.exit(0);

           }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }
    public void envoyerMessage(){

        String msg =champMessage.getText();
        try {
            service.envoyerMessage(username,msg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        champMessage.setText("");
    }

    public void afficherMessage(String message){
        zoneChat.append(message +"\n");
    }
    public void updateUsers(){
        modelusers.clear();
        try {
            for (String user : service.getUsers()){
                modelusers.addElement(user);

            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
    public void chargerHistorique() {
        try {
            for (String msg : service.getHistorique(username)) {
                zoneChat.append(msg + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
