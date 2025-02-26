import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class HotelRoom {
    int roomNumber;
    boolean isBooked;
    String customerName;

    public HotelRoom(int roomNumber) {
        this.roomNumber = roomNumber;
        this.isBooked = false;
        this.customerName = "";
    }
}

public class HotelReservationSystem {
    private final List<HotelRoom> rooms;
    private JFrame frame;
    private JTextArea displayArea;
    private JTextField nameField, roomField;

    public HotelReservationSystem() {
        rooms = new ArrayList<>();
        for (int i = 101; i <= 110; i++) {
            rooms.add(new HotelRoom(i));
        }
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Hotel Reservation System");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        JButton checkButton = new JButton("Check Availability");
        JButton bookButton = new JButton("Book Room");
        JButton displayButton = new JButton("View Bookings");

        nameField = new JTextField(15);
        roomField = new JTextField(5);
        displayArea = new JTextArea(15, 30);
        displayArea.setEditable(false);

        checkButton.addActionListener(e -> checkAvailability());
        bookButton.addActionListener(e -> bookRoom());
        displayButton.addActionListener(e -> displayBookings());

        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        panel.add(checkButton);
        panel.add(bookButton);
        panel.add(displayButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void checkAvailability() {
        StringBuilder availableRooms = new StringBuilder("Available Rooms: ");
        for (HotelRoom room : rooms) {
            if (!room.isBooked) {
                availableRooms.append(room.roomNumber).append(" ");
            }
        }
        displayArea.setText(availableRooms.toString());
    }

    private void bookRoom() {
        String name = nameField.getText().trim();
        String roomStr = roomField.getText().trim();
        if (name.isEmpty() || roomStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter name and room number.");
            return;
        }
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number.");
            return;
        }

        for (HotelRoom room : rooms) {
            if (room.roomNumber == roomNumber) {
                if (room.isBooked) {
                    JOptionPane.showMessageDialog(frame, "Room is already booked.");
                } else {
                    room.isBooked = true;
                    room.customerName = name;
                    JOptionPane.showMessageDialog(frame, "Room booked successfully!");
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Room not found.");
    }

    private void displayBookings() {
        StringBuilder bookings = new StringBuilder("Current Bookings:\n");
        for (HotelRoom room : rooms) {
            if (room.isBooked) {
                bookings.append("Room ").append(room.roomNumber).append(" - ").append(room.customerName).append("\n");
            }
        }
        displayArea.setText(bookings.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelReservationSystem::new);
    }
}
