import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.*;
public class EventManagementSystem  {
    private Connection connection;
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JComboBox<String> searchDropdown, deleteDropdown;
    private JTextArea customerDetailsArea;
    private JComboBox<String> dateDropdown;
    private JTextField txtEventName,txtLocation, txtEventDate,txtName, txtPhone,txtEmail, txtAmount;
    private JTextArea eventDetailsArea;
    private JComboBox<String>  customerDropdown, eventDropdown;
    private JTextField txtCustomerName;
    private JTextArea bookingDetailsArea;
    private JButton btnBookEvent,btnSearchBookings, btnCancelBooking;
    private JComboBox<String> bookingCustomerDropdown;
    private JComboBox<String> bookingSelectorDropdown;
    private JComboBox<String> bookingDropdown;
    private JTextField txtAmountDisplay;
    JTextField amountField = new JTextField();
    public EventManagementSystem() {
        connectToDatabase();
        initializeGUI();
        loadEventDates();
        loadCustomerDropdowns();
        loadEventDropdown();
        loadCustomerDropdown();

    }
    private void connectToDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/xe", "system", "it23118");
            System.out.println("Connected to Oracle Database!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeGUI() {
        frame = new JFrame("Entertainment Event Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customers", createCustomerPanel());
        tabbedPane.addTab("Events", createEventPanel());
        tabbedPane.addTab("Bookings", createBookingPanel());
        tabbedPane.addTab("Payments", createPaymentPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel lblName = new JLabel("Name:");
        txtName = new JTextField(30);
        JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField(30);
        JLabel lblPhone = new JLabel("Phone:");
        txtPhone = new JTextField(30);
        JButton btnAdd = new JButton("Add");
        btnAdd.setPreferredSize(new Dimension(80, 25));
        btnAdd.setMaximumSize(new Dimension(80, 25));
        searchDropdown = new JComboBox<>();
        deleteDropdown = new JComboBox<>();
        JButton btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(80, 25));
        btnSearch.setMaximumSize(new Dimension(80, 25));
        // 🔄 Update Button
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setPreferredSize(new Dimension(80, 25));
        btnUpdate.setMaximumSize(new Dimension(80, 25));
        JButton btnDelete = new JButton("Delete");
        btnDelete.setPreferredSize(new Dimension(80, 25));
        btnDelete.setMaximumSize(new Dimension(80, 25));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblName, gbc);
        gbc.gridx = 1;
        panel.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblEmail, gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblPhone, gbc);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        panel.add(btnAdd, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(new JLabel("Select Customer to Search:"), gbc);
        gbc.gridx = 1;
        panel.add(searchDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        panel.add(btnSearch, gbc);

        // 🆕 Update Button
        gbc.gridy = 6;
        panel.add(btnUpdate, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(new JLabel("Select Customer to Delete:"), gbc);
        gbc.gridx = 1;
        panel.add(deleteDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        panel.add(btnDelete, gbc);

        // 🔗 Action Listeners
        btnAdd.addActionListener(e -> addCustomer(txtName.getText(), txtEmail.getText(), txtPhone.getText(), txtName, txtEmail, txtPhone));
        btnSearch.addActionListener(e -> searchCustomer(searchDropdown.getSelectedItem().toString(), txtName, txtEmail, txtPhone));
        btnDelete.addActionListener(e -> deleteCustomer((String) deleteDropdown.getSelectedItem()));
        btnUpdate.addActionListener(e -> updateCustomer(txtName.getText(), txtEmail.getText(), txtPhone.getText()));

        loadCustomerDropdowns();
        return panel;
    }

    private void addCustomer(String name, String email, String phone, JTextField txtName, JTextField txtEmail, JTextField txtPhone) {
        try {
            // Email regex pattern
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);

            if (!matcher.matches()) {
                JOptionPane.showMessageDialog(null, "Invalid email format!");
                return;
            }

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT customer_seq.NEXTVAL FROM dual");
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Error: Sequence customer_seq is not found.");
                return;
            }
            rs.close(); stmt.close();

            String query = "INSERT INTO Customer (customer_id, name, email, phone_no) VALUES (customer_seq.NEXTVAL, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Customer added successfully!");
                txtName.setText("");
                txtEmail.setText("");
                txtPhone.setText("");
                loadCustomerDropdowns();
            } else {
                JOptionPane.showMessageDialog(null, "Error: No rows affected during insert.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding customer.");
        }
    }
    private void searchCustomer(String selectedItem, JTextField txtName, JTextField txtEmail, JTextField txtPhone) {
        try {
            if (selectedItem == null || !selectedItem.contains(" - ")) {
                JOptionPane.showMessageDialog(null, "Please select a valid customer.");
                return;
            }

            String name = selectedItem.split(" - ", 2)[1];  // Extract name part

            String query = "SELECT * FROM Customer WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String email = rs.getString("email");
                String phone = rs.getString("phone_no");
                // Populate fields
                txtName.setText(name);
                txtEmail.setText(email);
                txtPhone.setText(phone);
                // Store the customerId for updating later (optional: store in a global field)
                txtName.putClientProperty("customerId", customerId);
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found.");
            }
            rs.close();
            pstmt.close();
            loadCustomerDropdowns();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching customer.");
        }
    }
    private void updateCustomer(String name, String email, String phone) {
        try {
            String selectedItem = (String) searchDropdown.getSelectedItem();
            if (selectedItem == null || !selectedItem.contains(" - ")) {
                JOptionPane.showMessageDialog(null, "Please select a customer to update.");
                return;
            }

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required.");
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                JOptionPane.showMessageDialog(null, "Invalid email format.");
                return;
            }

            int customerId = Integer.parseInt(selectedItem.split(" - ")[0]);

            String query = "UPDATE Customer SET name = ?, email = ?, phone_no = ? WHERE customer_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setInt(4, customerId);
            int rows = pstmt.executeUpdate();
            pstmt.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Customer updated successfully.");
                loadCustomerDropdowns();
                txtName.setText("");
                txtEmail.setText("");
                txtPhone.setText("");// Refresh dropdowns

                // Clear text fields
                searchDropdown.setSelectedItem(null); // Optionally reset dropdown
            } else {
                JOptionPane.showMessageDialog(null, "No customer found to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating customer.");
        }
    }
    private void deleteCustomer(String selectedItem) {
        try {
            if (selectedItem == null || !selectedItem.contains(" - ")) {
                JOptionPane.showMessageDialog(null, "Please select a valid customer.");
                return;
            }

            String name = selectedItem.split(" - ", 2)[1];  // Extract name part

            String query = "DELETE FROM Customer WHERE name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            int rowsDeleted = pstmt.executeUpdate();
            pstmt.close();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Customer deleted successfully.");
                loadCustomerDropdowns();
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting customer.");
        }
    }
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        bookingCustomerDropdown = new JComboBox<>();
        eventDropdown = new JComboBox<>();

        btnBookEvent = new JButton("Book Event");
        bookingDetailsArea = new JTextArea(10, 40);
        bookingDetailsArea.setEditable(false);

        inputPanel.add(new JLabel("Select Customer:"));
        inputPanel.add(bookingCustomerDropdown);
        inputPanel.add(new JLabel("Select Event (Name - Date):"));
        inputPanel.add(eventDropdown);
        inputPanel.add(new JLabel());
        inputPanel.add(btnBookEvent);
        btnCancelBooking = new JButton("Cancel Booking");
        inputPanel.add(btnCancelBooking);


        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookingDetailsArea), BorderLayout.CENTER);

        btnBookEvent.addActionListener(e -> bookEvent());
        btnCancelBooking.addActionListener(e -> cancelBooking());


        loadCustomerDropdowns();
        loadEventDropdown();

        return panel;
    }

    private void loadCustomerDropdowns() {
        try {
            if (searchDropdown != null) searchDropdown.removeAllItems();
            if (deleteDropdown != null) deleteDropdown.removeAllItems();
            if (bookingCustomerDropdown != null) bookingCustomerDropdown.removeAllItems();

            PreparedStatement ps = connection.prepareStatement("SELECT customer_id, name FROM Customer");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String entry = rs.getInt("customer_id") + " - " + rs.getString("name");
                if (searchDropdown != null) searchDropdown.addItem(entry);
                if (deleteDropdown != null) deleteDropdown.addItem(entry);
                if (bookingCustomerDropdown != null) bookingCustomerDropdown.addItem(entry);
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadEventDropdown() {
        try {
            eventDropdown.removeAllItems();  // Clear old items
            PreparedStatement ps = connection.prepareStatement("SELECT event_id, name, event_date FROM Event");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("event_id");
                String name = rs.getString("name");
                Date date = rs.getDate("event_date");
                eventDropdown.addItem(id + " - " + name + " - " + date);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void bookEvent() {
        try {
            String customerItem = (String) bookingCustomerDropdown.getSelectedItem();
            String eventItem = (String) eventDropdown.getSelectedItem();

            if (customerItem == null || eventItem == null) {
                JOptionPane.showMessageDialog(frame, "Please select both customer and event.");
                return;
            }

            int customerId = Integer.parseInt(customerItem.split(" - ")[0]);
            String customerName = customerItem.split(" - ")[1];

            int eventId = Integer.parseInt(eventItem.split(" - ")[0]);
            String eventName = eventItem.split(" - ")[1];

            // Step 1: Get booking_id from sequence
            int bookingId = 0;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT booking_seq.NEXTVAL FROM dual");
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            // Step 2: Insert into Booking table
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Booking (booking_id, booking_date) VALUES (?, SYSDATE)");
            ps.setInt(1, bookingId);
            ps.executeUpdate();
            ps.close();

            // Step 3: Insert into Makes (customer to booking)
            ps = connection.prepareStatement("INSERT INTO Makes (customer_id, booking_id) VALUES (?, ?)");
            ps.setInt(1, customerId);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
            ps.close();

            // Step 4: Insert into For_Event (event to booking)
            ps = connection.prepareStatement("INSERT INTO For_Event (event_id, booking_id) VALUES (?, ?)");
            ps.setInt(1, eventId);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
            ps.close();

            // ✅ Confirmation message with names
            bookingDetailsArea.append("Booking successful!\nCustomer: " + customerName +
                    "\nEvent: " + eventName + "\n\n");


        }
        catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to book the event.");
        }

    }
    private JPanel createEventPanel() {
        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Add Event Section
        JLabel lblEventName = new JLabel("Event Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        eventPanel.add(lblEventName, gbc);

        txtEventName = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        eventPanel.add(txtEventName, gbc);

        JLabel lblLocation = new JLabel("Location:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        eventPanel.add(lblLocation, gbc);

        txtLocation = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        eventPanel.add(txtLocation, gbc);

        JLabel lblEventDate = new JLabel("Event Date (YYYY-MM-DD):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        eventPanel.add(lblEventDate, gbc);

        txtEventDate = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        eventPanel.add(txtEventDate, gbc);

        // 👇 NEW: Amount label and field
        JLabel lblAmount = new JLabel("Amount:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        eventPanel.add(lblAmount, gbc);

        txtAmount = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        eventPanel.add(txtAmount, gbc);

        JButton btnAddEvent = new JButton("Add Event");
        btnAddEvent.setPreferredSize(new Dimension(100, 25));
        btnAddEvent.setMaximumSize(new Dimension(100, 25));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        eventPanel.add(btnAddEvent, gbc);

        // Search Event Section
        JLabel lblSelectDate = new JLabel("Select Event Date:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        eventPanel.add(lblSelectDate, gbc);

        dateDropdown = new JComboBox<>();
        dateDropdown.setPreferredSize(new Dimension(150, dateDropdown.getPreferredSize().height));
        gbc.gridx = 1;
        gbc.gridy = 5;
        eventPanel.add(dateDropdown, gbc);

        JButton btnSearch = new JButton("Search Event");
        btnSearch.setPreferredSize(new Dimension(100, 25));
        btnSearch.setMaximumSize(new Dimension(100, 25));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        eventPanel.add(btnSearch, gbc);

        eventDetailsArea = new JTextArea(5, 30);
        eventDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(eventDetailsArea);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        eventPanel.add(scrollPane, gbc);

        // Action listeners
        btnAddEvent.addActionListener(e -> addEvent());
        btnSearch.addActionListener(e -> searchEventByDate());

        return eventPanel;
    }

    // AddEvent method

    private void addEvent() {
        String location = txtLocation.getText();
        String name = txtEventName.getText();
        String eventDateStr = txtEventDate.getText();
        String amountStr = txtAmount.getText();

        if (location.isEmpty() || name.isEmpty() || eventDateStr.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields including amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr); // Parse the amount

            if (amount < 0) {
                JOptionPane.showMessageDialog(frame, "Amount cannot be negative.");
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(eventDateStr);
            Date eventDate = new Date(parsedDate.getTime());

            String checkQuery = "SELECT * FROM Event WHERE event_date = ? AND location = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setDate(1, eventDate);
            checkStmt.setString(2, location);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "An event already exists for this location on the selected date.");
                return;
            }

            // Add `amount` field to the INSERT statement
            String query = "INSERT INTO Event (event_id, location, name, event_date, amount) VALUES (event_seq.NEXTVAL, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, location);
            pstmt.setString(2, name);
            pstmt.setDate(3, eventDate);
            pstmt.setDouble(4, amount);  // Set the amount

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Event added successfully!");
                txtEventName.setText("");
                txtLocation.setText("");
                txtEventDate.setText("");
                txtAmount.setText(""); // Clear the new amount field
                loadEventDropdown();
                loadEventDates();
            } else {
                JOptionPane.showMessageDialog(frame, "Error: No rows affected.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid amount.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding event: " + e.getMessage());
        }
    }

    private void deleteCompletedEvents() {
        try {
            // Get the current date
            java.util.Date currentDate = new java.util.Date();
            java.sql.Date sqlCurrentDate = new java.sql.Date(currentDate.getTime());

            // SQL query to delete events with a date earlier than today
            String deleteQuery = "DELETE FROM Event WHERE event_date < ?";
            PreparedStatement pstmt = connection.prepareStatement(deleteQuery);
            pstmt.setDate(1, sqlCurrentDate); // Set the current date parameter

            // Execute the delete query
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(rowsDeleted + " completed events deleted.");
            } else {
                System.out.println("No completed events found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting completed events.");
        }
    }
    private void searchEventByDate() {
        String selectedDate = (String) dateDropdown.getSelectedItem(); // Get the selected date from the dropdown

        if (selectedDate == null || selectedDate.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select a date.");
            return;
        }

        try {
            // Convert the selected date (String) into java.sql.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(selectedDate);
            Date eventDate = new Date(parsedDate.getTime()); // Convert to java.sql.Date for DB compatibility

            // Check if the event date is completed (i.e., the event date is in the past)
            java.util.Date currentDate = new java.util.Date();
            if (eventDate.before(new java.sql.Date(currentDate.getTime()))) {
                // If the event date is in the past, remove it from the dropdown and delete the event from DB
                deleteCompletedEventFromDB(eventDate);  // Delete completed event from database
                dateDropdown.removeItem(selectedDate);
                JOptionPane.showMessageDialog(frame, "The events for this date have already been completed and removed from the database.");
                return; // Exit the method as there is no need to search for events on this date
            }

            // Query to find events on the selected date
            String query = "SELECT * FROM Event WHERE event_date = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDate(1, eventDate);  // Set the event date parameter

            ResultSet rs = pstmt.executeQuery();

            eventDetailsArea.setText("");  // Clear the previous event details

            boolean eventsFound = false;  // Flag to check if any events were found

            while (rs.next()) {
                String eventName = rs.getString("name");  // Get event name
                String location = rs.getString("location");  // Get event location
                eventDetailsArea.append("Event: " + eventName + "\nLocation: " + location + "\n\n");  // Display the event details

                eventsFound = true;
            }

            if (!eventsFound) {
                eventDetailsArea.setText("No events scheduled for this date.");  // Display message if no events are found
            }

        } catch (Exception e) {
            e.printStackTrace();  // Print the stack trace for debugging
            JOptionPane.showMessageDialog(frame, "Error searching events: " + e.getMessage());
        }
    }
    private void deleteCompletedEventFromDB(Date eventDate) {
        try {
            String deleteQuery = "DELETE FROM Event WHERE event_date = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setDate(1, eventDate);  // Set the event date parameter for deletion

            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Completed event(s) successfully deleted.");
                JOptionPane.showMessageDialog(frame, "Completed event(s) successfully deleted.");

                // Refresh the dropdown after deletion
                loadEventDates();
            } else {
                System.out.println("No event found for the given date.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting completed event: " + e.getMessage());
        }
    }

    private void loadEventDates() {
        try {
            // Cleanup completed events first
            deleteCompletedEvents();

            String query = "SELECT DISTINCT TO_CHAR(event_date, 'YYYY-MM-DD') AS event_date FROM Event";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Clear existing items in the dropdown
            dateDropdown.removeAllItems();

            // Add available dates to the dropdown
            boolean addedDate = false;
            while (rs.next()) {
                String eventDate = rs.getString("event_date");
                dateDropdown.addItem(eventDate);
                addedDate = true;
            }

            if (!addedDate) {
                dateDropdown.addItem("No events available");
            }

            // Refresh UI to make sure dropdown is updated
            dateDropdown.revalidate();
            dateDropdown.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading event dates: " + e.getMessage());
        }
    }
    private void cancelBooking() {
        try {
            String customerItem = (String) bookingCustomerDropdown.getSelectedItem();
            String eventItem = (String) eventDropdown.getSelectedItem();

            if (customerItem == null || eventItem == null) {
                JOptionPane.showMessageDialog(frame, "Please select both customer and event to cancel.");
                return;
            }

            int customerId = Integer.parseInt(customerItem.split(" - ")[0]);
            String customerName = customerItem.split(" - ")[1];

            int eventId = Integer.parseInt(eventItem.split(" - ")[0]);
            String eventName = eventItem.split(" - ")[1];

            String findBooking = "SELECT b.booking_id FROM Booking b " +
                    "JOIN Makes m ON b.booking_id = m.booking_id " +
                    "JOIN For_Event f ON b.booking_id = f.booking_id " +
                    "WHERE m.customer_id = ? AND f.event_id = ?";
            PreparedStatement ps = connection.prepareStatement(findBooking);
            ps.setInt(1, customerId);
            ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                rs.close();
                ps.close();

                // Delete from For_Event
                ps = connection.prepareStatement("DELETE FROM For_Event WHERE booking_id = ?");
                ps.setInt(1, bookingId);
                ps.executeUpdate();
                ps.close();

                // Delete from Makes
                ps = connection.prepareStatement("DELETE FROM Makes WHERE booking_id = ?");
                ps.setInt(1, bookingId);
                ps.executeUpdate();
                ps.close();

                // Delete from Booking
                ps = connection.prepareStatement("DELETE FROM Booking WHERE booking_id = ?");
                ps.setInt(1, bookingId);
                ps.executeUpdate();
                ps.close();

                bookingDetailsArea.append("Booking cancelled!\nCustomer: " + customerName + "\nEvent: " + eventName + "\n\n");
            } else {
                rs.close();
                ps.close();
                bookingDetailsArea.append("No booking found for " + customerName + " and event " + eventName + ".\n\n");
            }


        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error while cancelling the booking.");
        }
    }
    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer Dropdown
        JLabel lblCustomer = new JLabel("Select Customer:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        paymentPanel.add(lblCustomer, gbc);

        customerDropdown = new JComboBox<>();
        loadCustomerDropdowns(); // Load customers
        customerDropdown.addActionListener(e -> loadBookingsForCustomer());
        gbc.gridx = 1;
        paymentPanel.add(customerDropdown, gbc);

        // Booking Dropdown
        JLabel lblBooking = new JLabel("Select Booking:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        paymentPanel.add(lblBooking, gbc);

        bookingDropdown = new JComboBox<>();
        bookingDropdown.addActionListener(e -> updateAmountField());
        gbc.gridx = 1;
        paymentPanel.add(bookingDropdown, gbc);

        // Amount field (non-editable)
        JLabel lblAmount = new JLabel("Amount:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        paymentPanel.add(lblAmount, gbc);

        txtAmountDisplay = new JTextField(20);
        txtAmountDisplay.setEditable(false);
        gbc.gridx = 1;
        paymentPanel.add(txtAmountDisplay, gbc);

        // Payment Button
        JButton btnPay = new JButton("Make Payment");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        paymentPanel.add(btnPay, gbc);

        btnPay.addActionListener(e -> makePayment());
        loadCustomerDropdown();


        return paymentPanel;
    }
    private void loadCustomerDropdown() {
        try {
            customerDropdown.removeAllItems();
            PreparedStatement ps = connection.prepareStatement("SELECT customer_id, name FROM Customer");
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                String item = rs.getInt("customer_id") + " - " + rs.getString("name");
                System.out.println("Adding to customerDropdown: " + item);
                customerDropdown.addItem(item);
            }
            if (!found) {
                System.out.println("No customers found in DB.");
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadBookingsForCustomer() {
        bookingDropdown.removeAllItems();
        String selectedCustomer = (String) customerDropdown.getSelectedItem();
        if (selectedCustomer == null) return;

        try {
            // Extract userId safely
            String[] customerParts = selectedCustomer.split(" - ");
            if (customerParts.length < 2) return;  // Prevent array out of bounds
            int userId = Integer.parseInt(customerParts[0]);

            // Updated query to join Makes, For_Event, and Event to fetch the required data
            String query = """
            SELECT b.booking_id, e.name || ' (' || e.event_date || ')' AS event_info
            FROM Booking b
            JOIN Makes m ON b.booking_id = m.booking_id
            JOIN For_Event fe ON b.booking_id = fe.booking_id
            JOIN Event e ON fe.event_id = e.event_id
            WHERE m.customer_id = ?
        """;
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);  // Bind the customer_id to the query
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookingDropdown.addItem(rs.getInt("booking_id") + " - " + rs.getString("event_info"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateAmountField() {
        try {
            // Get the selected booking from the dropdown
            String selectedBooking = (String) bookingDropdown.getSelectedItem();
            if (selectedBooking == null) {
                System.out.println("No booking selected");
                return;
            }

            // Extract the booking_id from the selected booking dropdown item
            String[] bookingParts = selectedBooking.split(" - ");
            if (bookingParts.length < 1) {
                System.out.println("Invalid booking format");
                return;  // Prevent array out of bounds
            }

            int bookingId = Integer.parseInt(bookingParts[0]);
            System.out.println("Extracted booking_id: " + bookingId);  // Debugging

            // SQL query to fetch the amount from the Event table based on the booking_id
            String query = """
        SELECT e.amount 
        FROM For_Event fe
        JOIN Event e ON fe.event_id = e.event_id
        JOIN Makes m ON fe.booking_id = m.booking_id
        WHERE m.booking_id = ?
        """;
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, bookingId);  // Bind the booking_id to the query
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double amount = rs.getDouble("amount");
                System.out.println("Amount fetched: " + amount);  // Debugging

                // Update the text field with the amount
                if (txtAmountDisplay != null) {
                    txtAmountDisplay.setText(String.valueOf(amount));  // Update the amount in the text field
                    System.out.println("Amount set in text field: " + amount);  // Debugging
                } else {
                    System.out.println("Amount field is null");  // Debugging: Check if the text field is null
                }
            } else {
                System.out.println("No amount found for booking_id: " + bookingId);  // Debugging
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void makePayment() {
        String selectedBooking = (String) bookingDropdown.getSelectedItem();
        if (selectedBooking == null || txtAmountDisplay.getText().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Select a booking and ensure amount is visible.");
            return;
        }

        int bookingId = Integer.parseInt(selectedBooking.split(" - ")[0]);
        double amount = Double.parseDouble(txtAmountDisplay.getText());

        try {
            // Step 1: Check if the payment already exists for this booking
            String checkPaymentQuery = "SELECT COUNT(*) FROM Has_Payment WHERE booking_id = ?";
            PreparedStatement psCheck = connection.prepareStatement(checkPaymentQuery);
            psCheck.setInt(1, bookingId);
            ResultSet rsCheck = psCheck.executeQuery();

            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                // If there's already a payment, don't allow a new payment
                JOptionPane.showMessageDialog(frame, "Payment already made for this booking.");
                rsCheck.close();
                psCheck.close();
                return;
            }
            rsCheck.close();
            psCheck.close();

            // Step 2: Proceed with inserting the payment
            // Insert into Payment table
            String paymentQuery = "INSERT INTO Payment (payment_id, amount) VALUES (payment_seq.NEXTVAL, ?)";
            PreparedStatement psPayment = connection.prepareStatement(paymentQuery, new String[] {"payment_id"});
            psPayment.setDouble(1, amount);
            psPayment.executeUpdate();

            // Get generated payment_id
            ResultSet rsPayment = psPayment.getGeneratedKeys();
            int paymentId = -1;
            if (rsPayment.next()) {
                paymentId = rsPayment.getInt(1);
            }

            // Step 3: Insert into Has_Payment
            String hasPaymentQuery = "INSERT INTO Has_Payment (booking_id, payment_id) VALUES (?, ?)";
            PreparedStatement psHasPayment = connection.prepareStatement(hasPaymentQuery);
            psHasPayment.setInt(1, bookingId);
            psHasPayment.setInt(2, paymentId);
            psHasPayment.executeUpdate();

            // Close resources
            rsPayment.close();
            psPayment.close();
            psHasPayment.close();

            JOptionPane.showMessageDialog(frame, "Payment successful!");
            txtAmountDisplay.setText("");  // Clear the amount field
            bookingDropdown.removeAllItems();  // Clear the booking dropdown

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error making payment: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EventManagementSystem::new);
    }
}
