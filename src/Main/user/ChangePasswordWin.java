package Main.user;

import Main.Main;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;

public class ChangePasswordWin extends JFrame {
    private JLabel labelPassword = new JLabel("New password");
    private JTextField passwordTextField= new JTextField(20);
    private JButton changeButton = new JButton("Change");
    private JPanel panel = new JPanel(new GridBagLayout());
    private GridBagConstraints constraints = new GridBagConstraints();

    public ChangePasswordWin(){
        super("Change password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Window Listener
        WindowListener windowListener = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {
                Main.userManagementWin.setEnabled(true);
                Main.userManagementWin.setVisible(true);
            }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        };
        super.addWindowListener(windowListener);

        // Button setting
        ActionListener changeListener = e -> {
            if(passwordTextField.getText().isEmpty()){
                JOptionPane.showMessageDialog(null,"Password field cannot be empty.");
            }
            else{
                try {
                    changePasswordSQL(Main.user, passwordTextField.getText());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "Change applied!");
            }
        };
        changeButton.addActionListener(changeListener);

        // Panel setting
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(labelPassword, constraints);

        constraints.gridx = 1;
        panel.add(passwordTextField, constraints);

        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 10, 5, 10);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(changeButton, constraints);

        getContentPane().add(panel);

        // Display the window
        setLocation(900,350);
        pack();
    }

    private void changePasswordSQL(User user, String newPassword) throws SQLException {
        PreparedStatement pstatement = Main.connection.prepareStatement("UPDATE user " +
                "SET userPassword = ? WHERE userName = ? " );

        pstatement.setString(1, newPassword);
        pstatement.setString(2, user.getUserName());
        pstatement.executeUpdate();
        pstatement.close();
    }
}
