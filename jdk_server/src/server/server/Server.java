package server.server;

import server.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {
    private static final int POS_X = 500;
    private static final int POS_Y = 550;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;

    private final JButton btnStart = createButton("Start", this::startServer);
    private final JButton btnStop = createButton("Stop", this::stopServer);
    private final JTextArea logArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Сервер остановлен", SwingConstants.CENTER);
    private boolean isServerRunning;
    private final List<Client> clients = new ArrayList<>();
    private final List<String> chatHistory = new ArrayList<>();

    public Server() {
        setupWindow();
        setupComponents();
        setVisible(true);
    }

    private void setupWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat Server");
        setLayout(new BorderLayout());
    }

    private void setupComponents() {
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setForeground(Color.RED);
        updateStatusLabel();

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(btnStart);
        buttonPanel.add(btnStop);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private void startServer(ActionEvent e) {
        if (isServerRunning) {
            showMessage("Сервер уже запущен!", "Внимание");
        } else {
            isServerRunning = true;
            logMessage("Сервер запущен!");
            loadChatHistory();
            updateStatusLabel();
        }
    }

    private void stopServer(ActionEvent e) {
        if (!isServerRunning) {
            showMessage("Сервер не запущен!", "Предупреждение");
        } else {
            isServerRunning = false;
            logMessage("Сервер остановлен!");
            updateStatusLabel();
        }
    }

    private void logMessage(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void updateStatusLabel() {
        statusLabel.setText(isServerRunning ? "Сервер запущен" : "Сервер остановлен");
    }

    public void addClient(Client client) {
        clients.add(client);
        sendChatHistory(client);
    }

    public void receiveMessage(String login, String message) {
        if (isServerRunning) {
            logMessage(login + ": " + message);
            chatHistory.add(login + ": " + message);
            clients.forEach(client -> client.appendLog(login + ": " + message));
            saveMessageToFile(login + ": " + message);
        }
    }

    private void sendChatHistory(Client client) {
        chatHistory.forEach(client::appendLog);
    }

    private void saveMessageToFile(String message) {
        try (FileWriter writer = new FileWriter("chat.txt", true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("chat.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chatHistory.add(line);
                logMessage(line);
            }
            clients.forEach(this::sendChatHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
