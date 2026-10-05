package client;

import common.ChatRemote;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistrerGUI extends JFrame {

    public RegistrerGUI(ChatRemote service) {

        setTitle("Créer un compte");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🌙 Background
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(30, 30, 30));

        // 📦 Carte centrale
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(300, 260));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 🔷 Titre
        JLabel title = new JLabel("Créer un compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 🧾 Champs
        JTextField user = new JTextField();
        user.setMaximumSize(new Dimension(250, 35));
        user.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField pass = new JPasswordField();
        pass.setMaximumSize(new Dimension(250, 35));
        pass.setBorder(BorderFactory.createTitledBorder("Password"));

        // 🎨 Bouton moderne
        JButton valider = new JButton("Créer") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(0,150,136),
                        getWidth(), 0, new Color(63,81,181));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        valider.setForeground(Color.WHITE);
        valider.setFocusPainted(false);
        valider.setBorderPainted(false);
        valider.setContentAreaFilled(false);
        valider.setMaximumSize(new Dimension(250, 40));

        // 📦 Ajout composants
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(user);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(pass);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(valider);

        background.add(card);
        setContentPane(background);

        setVisible(true);

        valider.addActionListener(e -> {
            try {
                boolean ok = service.registerUser(user.getText(), new String(pass.getPassword()));
                            //enregistrement daans la bd
                if (ok) {
                    new ChatGUI(user.getText(),service);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Utilisateur existe !");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }
}
