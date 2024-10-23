package server.client;

import server.server.Server;

import javax.swing.*;
import java.awt.*;

public class Client extends JFrame {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;

    private final JTextArea logTextArea = new JTextArea();
    private final JTextField ipAddressField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("8189");
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField("pass");
    private final JButton btnLogin = new JButton("Login");
    private final JTextField textMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");
    private final Server server;

    public Client(Server server) {
        this.server = server;

        // Prompt user for login
        String login = JOptionPane.showInputDialog("Введите ваш логин:");
        if (login == null || login.trim().isEmpty()) {
            showError("Поле логина должно быть заполнено. Программа будет остновлена.");
        }
        loginField.setText(login);

        setupWindow();
        setupComponents();
        setVisible(true);
        server.addClient(this);
    }

    private void setupWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setTitle("Chat client");
    }

    private void setupComponents() {
        JPanel panelTop = new JPanel(new GridLayout(2, 3));
        panelTop.add(ipAddressField);
        panelTop.add(portField);
        panelTop.add(loginField);
        panelTop.add(passwordField);
        panelTop.add(btnLogin);
        add(panelTop, BorderLayout.NORTH);

        logTextArea.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logTextArea);
        add(scrollLog, BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(textMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        btnSend.addActionListener(e -> sendMessage());
        textMessage.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = textMessage.getText().trim();
        String login = loginField.getText();
        if (!message.isEmpty()) {
            server.receiveMessage(login, message);
            textMessage.setText("");
        }
    }

    public void appendLog(String message) {
        logTextArea.append(message + "\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}
