import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class AppointmentScheduler extends JFrame {
    private JComboBox<String> contactComboBox;
    private JTextField titleField, startDateField, endDateField, descriptionField, locationField;
    private JComboBox<String> typeComboBox;
    private JButton addButton, updateButton, deleteButton;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    public AppointmentScheduler() {
        setTitle("Appointment Scheduler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        initializeAppointmentComponents();

        add(createAppointmentInputPanel(), BorderLayout.NORTH);
        add(createAppointmentTablePanel(), BorderLayout.CENTER);
        add(createAppointmentButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeAppointmentComponents() {
        loadContactData();
        loadTypeData();
    }

    private void loadContactData() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String contactQuery = "SELECT contact_name FROM contacts";
            try (PreparedStatement contactStatement = connection.prepareStatement(contactQuery);
                 ResultSet contactResultSet = contactStatement.executeQuery()) {
                while (contactResultSet.next()) {
                    contactComboBox.addItem(contactResultSet.getString("contact_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTypeData() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String typeQuery = "SELECT DISTINCT type FROM appointment_types";
            try (PreparedStatement typeStatement = connection.prepareStatement(typeQuery);
                 ResultSet typeResultSet = typeStatement.executeQuery()) {
                while (typeResultSet.next()) {
                    typeComboBox.addItem(typeResultSet.getString("type"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createAppointmentInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(contactComboBox);

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeComboBox);

        inputPanel.add(new JLabel("Start Date and Time:"));
        startDateField = new JTextField("yyyy-MM-dd HH:mm:ss");
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("End Date and Time:"));
        endDateField = new JTextField("yyyy-MM-dd HH:mm:ss");
        inputPanel.add(endDateField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        inputPanel.add(locationField);

        return inputPanel;
    }

    private JPanel createAppointmentTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        appointmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createAppointmentButtonPanel() {
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> performAddAppointment());
        updateButton.addActionListener(e -> performUpdateAppointment());
        deleteButton.addActionListener(e -> performDeleteAppointment());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private void performAddAppointment() {
        String contactName = (String) contactComboBox.getSelectedItem();
        String title = titleField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        LocalDateTime startDateTime = LocalDateTime.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDateTime = LocalDateTime.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String description = descriptionField.getText();
        String location = locationField.getText();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, description, and location cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertQuery = "INSERT INTO appointments (title, description, location, contact, type, start_date_time, end_date_time, customer_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, contactName);
                preparedStatement.setString(5, type);
                preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime));
                preparedStatement.setTimestamp(7, Timestamp.valueOf(endDateTime));
                preparedStatement.setInt(8, 1); // Replace with the correct customer_id
                preparedStatement.setInt(9, 2); // Replace with the correct user_id
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        refreshAppointmentTable();
        clearAppointmentInputFields();
    }

    private void performUpdateAppointment() {
        String contactName = (String) contactComboBox.getSelectedItem();
        String title = titleField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        LocalDateTime startDateTime = LocalDateTime.parse(startDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDateTime = LocalDateTime.parse(endDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String description = descriptionField.getText();
        String location = locationField.getText();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, description, and location cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE appointments SET title=?, description=?, location=?, type=?, start_date_time=?, end_date_time=? WHERE contact_name=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, type);
                preparedStatement.setTimestamp(5, Timestamp.valueOf(startDateTime));
                preparedStatement.setTimestamp(6, Timestamp.valueOf(endDateTime));
                preparedStatement.setString(7, contactName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        refreshAppointmentTable();
        clearAppointmentInputFields();
    }

    private void performDeleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected appointment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String deleteQuery = "DELETE FROM appointments WHERE appointment_id=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    preparedStatement.setInt(1, appointmentId);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            refreshAppointmentTable();
            clearAppointmentInputFields();
        }
    }

    private void refreshAppointmentTable() {
        tableModel.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM appointments";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Object[] rowData = {
                            resultSet.getInt("appointment_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getString("location"),
                            resultSet.getString("contact"),
                            resultSet.getString("type"),
                            resultSet.getTimestamp("start_date_time").toLocalDateTime(),
                            resultSet.getTimestamp("end_date_time").toLocalDateTime(),
                            resultSet.getInt("customer_id"),
                            resultSet.getInt("user_id")
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAppointmentInputFields() {
        titleField.setText("");
        descriptionField.setText("");
        locationField.setText("");
        startDateField.setText("yyyy-MM-dd HH:mm:ss");
        endDateField.setText("yyyy-MM-dd HH:mm:ss");
        contactComboBox.setSelectedIndex(0);
        typeComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppointmentScheduler::new);
    }
}
