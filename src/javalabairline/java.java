package javalabairline;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class java extends JFrame {
    private JTextField nameField, flightField, destinationField, searchField;
    private JTextArea displayArea;

    // Database credentials
    private final String DB_URL = "jdbc:mysql://localhost:3306/airlne_db";
    private final String USER = "root";
    private final String PASS = "logan"; // Replace with your MySQL password

    public java() {
        setTitle("✈ Airline Reservation System");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "✍ Book a Ticket",
                TitledBorder.LEFT, TitledBorder.TOP, labelFont, Color.DARK_GRAY
        ));
        inputPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Passenger Name:");
        nameLabel.setFont(labelFont);
        nameField = new JTextField();
        nameField.setFont(fieldFont);

        JLabel flightLabel = new JLabel("Flight Number:");
        flightLabel.setFont(labelFont);
        flightField = new JTextField();
        flightField.setFont(fieldFont);

        JLabel destLabel = new JLabel("Destination:");
        destLabel.setFont(labelFont);
        destinationField = new JTextField();
        destinationField.setFont(fieldFont);

        JButton bookButton = new JButton("✅ Book Ticket");
        JButton viewButton = new JButton("📋 View All Bookings");
        styleButton(bookButton);
        styleButton(viewButton);

        inputPanel.add(nameLabel); inputPanel.add(nameField);
        inputPanel.add(flightLabel); inputPanel.add(flightField);
        inputPanel.add(destLabel); inputPanel.add(destinationField);
        inputPanel.add(bookButton); inputPanel.add(viewButton);

        add(inputPanel, BorderLayout.NORTH);

        // Display Area
        displayArea = new JTextArea();
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        displayArea.setBackground(new Color(245, 245, 245));
        displayArea.setBorder(BorderFactory.createTitledBorder("🧾 Bookings List"));
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Search Booking"));

        searchField = new JTextField();
        searchField.setFont(fieldFont);
        JButton searchButton = new JButton("Search");
        styleButton(searchButton);

        searchPanel.add(new JLabel("Search by Name or Flight No:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        add(searchPanel, BorderLayout.SOUTH);

        // Button Actions
        bookButton.addActionListener(e -> bookTicket());
        viewButton.addActionListener(e -> viewBookings());
        searchButton.addActionListener(e -> searchBookings(searchField.getText()));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void bookTicket() {
        String name = nameField.getText();
        String flight = flightField.getText();
        String destination = destinationField.getText();

        if (name.isEmpty() || flight.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO passengers (name, flight_number, destination) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, flight);
            stmt.setString(3, destination);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            nameField.setText("");
            flightField.setText("");
            destinationField.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewBookings() {
        displayArea.setText("");
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM passengers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String record = "Name: " + rs.getString("name") +
                        " | Flight No: " + rs.getString("flight_number") +
                        " | Destination: " + rs.getString("destination");
                displayArea.append(record + "\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            displayArea.setText("Error retrieving data: " + ex.getMessage());
        }
    }

    private void searchBookings(String keyword) {
        displayArea.setText("");

        if (keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name or flight number to search.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM passengers WHERE name LIKE ? OR flight_number LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);

            ResultSet rs = stmt.executeQuery();
            boolean found = false;

            while (rs.next()) {
                found = true;
                String record = "Name: " + rs.getString("name") +
                        " | Flight No: " + rs.getString("flight_number") +
                        " | Destination: " + rs.getString("destination");
                displayArea.append(record + "\n");
            }

            if (!found) {
                displayArea.setText("No matching bookings found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            displayArea.setText("Error retrieving search results: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new java().setVisible(true));
    }
}
