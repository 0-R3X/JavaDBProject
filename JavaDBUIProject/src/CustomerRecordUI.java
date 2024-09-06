import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerRecordUI extends JFrame {
    private JTextField nameField, addressField, postalCodeField, phoneField;
    private JComboBox<String> countryComboBox, divisionComboBox;
    private JButton addButton, updateButton, deleteButton, scheduleButton;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerRecordUI() {
        setTitle("Customer Record Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        initializeComponents();

        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        nameField = new JTextField();
        addressField = new JTextField();
        postalCodeField = new JTextField();
        phoneField = new JTextField();

        String[] countries = {"India", "USA", "UK", "China"};
        String[] divisions = {"New Delhi", "New York", "London", "Beijing"};

        countryComboBox = new JComboBox<>(countries);
        divisionComboBox = new JComboBox<>(divisions);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        scheduleButton = new JButton("Schedule");

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("Customer ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Address");
        tableModel.addColumn("Postal Code");
        tableModel.addColumn("Phone Number");

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getSelectionModel().addListSelectionListener(e -> displaySelectedCustomer());
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 6, 10, 10));

        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);

        inputPanel.add(new JLabel("Postal Code:"));
        inputPanel.add(postalCodeField);

        inputPanel.add(new JLabel("Phone Number:"));
        inputPanel.add(phoneField);

        inputPanel.add(new JLabel("Country:"));
        inputPanel.add(countryComboBox);

        inputPanel.add(new JLabel("First-Level Division:"));
        inputPanel.add(divisionComboBox);

        return inputPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(customerTable);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();

        addButton.addActionListener(e -> performAdd());
        updateButton.addActionListener(e -> performUpdate());
        deleteButton.addActionListener(e -> performDelete());
        JButton scheduleButton = new JButton("Schedule");
    scheduleButton.addActionListener(e -> openAppointmentScheduler());


        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(scheduleButton);

        return buttonPanel;
    }

    private void performAdd() {
        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneField.getText();
        String country = (String) countryComboBox.getSelectedItem();
        String division = (String) divisionComboBox.getSelectedItem();

        if (name.isEmpty() || address.isEmpty() || postalCode.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] rowData = {generateCustomerID(), name, address, postalCode, phoneNumber};
        tableModel.addRow(rowData);
        clearInputFields();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO customers (customer_id, name, address, postal_code, phone_number, country, division) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, (String) rowData[0]);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, address);
                preparedStatement.setString(4, postalCode);
                preparedStatement.setString(5, phoneNumber);
                preparedStatement.setString(6, country);
                preparedStatement.setString(7, division);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performUpdate() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneField.getText();
        String country = (String) countryComboBox.getSelectedItem();
        String division = (String) divisionComboBox.getSelectedItem();

        if (name.isEmpty() || address.isEmpty() || postalCode.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setValueAt(name, selectedRow, 1);
        tableModel.setValueAt(address, selectedRow, 2);
        tableModel.setValueAt(postalCode, selectedRow, 3);
        tableModel.setValueAt(phoneNumber, selectedRow, 4);

        clearInputFields();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE customers SET name=?, address=?, postal_code=?, phone_number=?, country=?, division=? WHERE customer_id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3, postalCode);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setString(5, country);
                preparedStatement.setString(6, division);
                preparedStatement.setString(7, (String) customerTable.getValueAt(selectedRow, 0));

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void performDelete() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        clearInputFields();
        String customerID = (String) customerTable.getValueAt(selectedRow, 0);
        tableModel.removeRow(selectedRow);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteAppointmentsQuery = "DELETE FROM appointments WHERE customer_id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAppointmentsQuery)) {
                preparedStatement.setString(1, customerID);
                preparedStatement.executeUpdate();
            }

            String deleteCustomerQuery = "DELETE FROM customers WHERE customer_id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteCustomerQuery)) {
                preparedStatement.setString(1, customerID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Customer deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displaySelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            String customerID = (String) customerTable.getValueAt(selectedRow, 0);

            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM customers WHERE customer_id=?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, customerID);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            nameField.setText(resultSet.getString("name"));
                            addressField.setText(resultSet.getString("address"));
                            postalCodeField.setText(resultSet.getString("postal_code"));
                            phoneField.setText(resultSet.getString("phone_number"));
                            countryComboBox.setSelectedItem(resultSet.getString("country"));
                            divisionComboBox.setSelectedItem(resultSet.getString("division"));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void openAppointmentScheduler() {
        JFrame schedulerFrame = new JFrame("Appointment Scheduler");
        schedulerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        schedulerFrame.setLayout(new BorderLayout());
        schedulerFrame.setPreferredSize(new Dimension(600, 400));
        
        AppointmentScheduler appointmentScheduler = new AppointmentScheduler();
    
        schedulerFrame.add(appointmentScheduler, BorderLayout.CENTER);
    
        JButton closeSchedulerButton = new JButton("Close Scheduler");
        closeSchedulerButton.addActionListener(e -> schedulerFrame.dispose());
        schedulerFrame.add(closeSchedulerButton, BorderLayout.SOUTH);
    
        schedulerFrame.pack();
        schedulerFrame.setLocationRelativeTo(null);
        schedulerFrame.setVisible(true);
    }
    

    private void clearInputFields() {
        nameField.setText("");
        addressField.setText("");
        postalCodeField.setText("");
        phoneField.setText("");
        countryComboBox.setSelectedIndex(0);
        divisionComboBox.setSelectedIndex(0);
    }

    private String generateCustomerID() {
        return "CUS-" + java.util.UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerRecordUI::new);
    }
}
