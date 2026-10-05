package client;
import common.ChatRemote;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;

public class LoginGUI extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private ChatRemote service;

    public LoginGUI() {
        setTitle("Login");
        setSize(350, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🎨 Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30)); // dark mode
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 🔹 Titre
        JLabel title = new JLabel("Mini Chat");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        // 🔹 Champs
        userField = new JTextField();
        passField = new JPasswordField();

        styleField(userField, "Username");
        styleField(passField, "Password");

        // 🔹 Boutons
        JButton loginBtn = new JButton("Se connecter");
        JButton registerBtn = new JButton("Créer un compte");

        styleButton(loginBtn, new Color(0, 153, 255));
        styleButton(registerBtn, new Color(100, 100, 100));

        // 🔹 Ajout composants
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(userField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(passField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(registerBtn);

        add(panel);

        connecterRMI();


        loginBtn.addActionListener(e -> login());


        registerBtn.addActionListener(e -> {
            new RegistrerGUI(service);
            dispose();
        });

        setVisible(true);
    }

    private void connecterRMI() {
        try {
            String IPserveur ="127.0.0.1";
            String url="rmi://"+IPserveur+":9005/chat";

            service =(ChatRemote) Naming.lookup(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        try {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (service.login(user, pass)) {
                new ChatGUI(user,service);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login incorrect !");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 🎨 Style champ
    private void styleField(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(250, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    // 🎨 Style bouton
    private void styleButton(JButton btn, Color color) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 35));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
    }


}

