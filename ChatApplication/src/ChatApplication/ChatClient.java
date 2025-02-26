package ChatApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static PrintWriter out;
    private static BufferedReader in;
    private static BufferedReader userInput;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static String userName;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Client");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        messageField = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        panel.add(messageField);
        panel.add(sendButton);
        frame.add(panel, BorderLayout.SOUTH);

        // Start connection to server
        String serverAddress = JOptionPane.showInputDialog("Enter server address (localhost for local):");
        try {
            Socket socket = new Socket(serverAddress, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            // Get user name
            userName = JOptionPane.showInputDialog("Enter your name:");
            out.println(userName);

            // Handle incoming messages
            new Thread(new IncomingMessageHandler()).start();

            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    // Handle incoming messages from the server
    private static class IncomingMessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

