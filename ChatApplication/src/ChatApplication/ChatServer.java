package ChatApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static JTextArea serverTextArea;
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Chat Server");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        serverTextArea = new JTextArea();
        serverTextArea.setEditable(false);
        frame.add(new JScrollPane(serverTextArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton startButton = new JButton("Start Server");
        startButton.addActionListener(e -> startServer());
        panel.add(startButton);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                serverTextArea.append("Server started...\n");

                while (true) {
                    new ClientHandler(serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Get client name
                out.println("Enter your name:");
                clientName = in.readLine();

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                broadcastMessage(clientName + " has joined the chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    serverTextArea.append(clientName + ": " + message + "\n");
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                broadcastMessage(clientName + " has left the chat.");
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
