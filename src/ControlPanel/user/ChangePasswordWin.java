package ControlPanel.user;

import ControlPanel.*;
import Server.Request.ChangePasswordRequest;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import javax.swing.*;

/**
 * @author Nicholas Tseng
 * This is the change password window, in this window,
 * users can change their own password.
 */
public class ChangePasswordWin extends JFrame {
    private JLabel labelPassword = new JLabel("New password");
    private JTextField passwordTextField = new JTextField(20);
    private JButton changeButton = new JButton("Change");
    private JPanel panel = new JPanel(new GridBagLayout());
    private GridBagConstraints constraints = new GridBagConstraints();

    public ChangePasswordWin() {
        super("Change password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Window Listener
        WindowListener windowListener = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                Main.userProfileWin.setEnabled(true);
                Main.userProfileWin.setVisible(true);
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        super.addWindowListener(windowListener);

        // Button setting
        ActionListener changeListener = e -> {
            // Check if the password field is empty, if so, return an error message.
            if (passwordTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password field cannot be empty.");
            } else {
                ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(Main.loginUser.getSessionToken(),
                        Main.loginUser.getUserName(), passwordTextField.getText());

                try {
                    Client.connectServer(changePasswordRequest);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                // Check if the change password request is successfully executed, if so, send an acknowledgement.
                if (Client.isRequestState()) {
                    JOptionPane.showMessageDialog(null, "Password change!");
                }
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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        setLocation(width / 4, height / 4);
        pack();
    }
}
