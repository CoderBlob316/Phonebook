import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

class Contact {
    private String name, address, number;

    public Contact(String name, String address, String number) {
        this.name = name;
        this.address = address;
        this.number = number;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getNumber() { return number; }
}

public class Phonebook extends JFrame implements ActionListener {
    private ArrayList<Contact> contacts = new ArrayList<>();
    private DefaultTableModel contactTableModel;
    private JTable contactTable;
    private JTextField nameField, addressField, numberField, searchField;
    private JButton addButton, searchButton, deleteButton, sortButton, saveButton, loadButton;

    public Phonebook() {
        setTitle("Yellow Pages");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Font uiFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("Button.font", uiFont);
            UIManager.put("Label.font", uiFont);
            UIManager.put("TextField.font", uiFont);
            UIManager.put("Table.font", uiFont);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xFFF9C4)); // Light yellow

        contactTableModel = new DefaultTableModel(new String[]{"Name", "Address", "Number"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return true; }
        };

        contactTable = new JTable(contactTableModel);
        contactTable.setForeground(new Color(0x333333));
        contactTable.setBackground(Color.WHITE);
        contactTable.setRowHeight(30);

        contactTableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (row >= 0 && row < contacts.size() && column >= 0) {
                String newValue = contactTableModel.getValueAt(row, column).toString().trim();
                Contact contact = contacts.get(row);
                String name = contact.getName();
                String address = contact.getAddress();
                String number = contact.getNumber();
                switch (column) {
                    case 0: name = newValue; break;
                    case 1: address = newValue; break;
                    case 2: number = newValue; break;
                }
                contacts.set(row, new Contact(name, address, number));
            }
        });

        JTableHeader header = contactTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(0xFBC02D));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(contactTable);
        tableScrollPane.setPreferredSize(new Dimension(900, 650));

        nameField = new JTextField(15);
        addressField = new JTextField(15);
        numberField = new JTextField(15);
        searchField = new JTextField(15);

        addButton = new JButton("Add Contact");
        searchButton = new JButton("Search");
        deleteButton = new JButton("Delete");
        sortButton = new JButton("Sort by Name");
        saveButton = new JButton("Save Contacts");
        loadButton = new JButton("Load Contacts");

        addButton.addActionListener(this);
        searchButton.addActionListener(this);
        deleteButton.addActionListener(this);
        sortButton.addActionListener(this);
        saveButton.addActionListener(this);
        loadButton.addActionListener(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Address:")); inputPanel.add(addressField);
        inputPanel.add(new JLabel("Number:")); inputPanel.add(numberField);
        inputPanel.add(addButton);

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.add(deleteButton);
        actionPanel.add(sortButton);
        actionPanel.add(saveButton);
        actionPanel.add(loadButton);
        actionPanel.add(new JLabel("Search:"));
        actionPanel.add(searchField);
        actionPanel.add(searchButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tablePanel.setOpaque(false);
        tablePanel.add(tableScrollPane);

        contentPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == addButton) addContact();
        else if (source == searchButton) searchContact();
        else if (source == deleteButton) deleteContact();
        else if (source == sortButton) {
            sortContacts();
            refreshTable();
        } else if (source == saveButton) {
            saveContactsToFile();
        } else if (source == loadButton) {
            loadContactsFromFile();
        }
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String number = numberField.getText().trim();
        if (name.isEmpty() || address.isEmpty() || number.isEmpty()) return;

        contacts.add(new Contact(name, address, number));
        sortContacts();
        refreshTable();

        nameField.setText("");
        addressField.setText("");
        numberField.setText("");
    }

    private void sortContacts() {
        for (int i = 1; i < contacts.size(); i++) {
            Contact key = contacts.get(i);
            int j = i - 1;
            while (j >= 0 && contacts.get(j).getName().compareToIgnoreCase(key.getName()) > 0) {
                contacts.set(j + 1, contacts.get(j));
                j--;
            }
            contacts.set(j + 1, key);
        }
    }

    private void searchContact() {
        String query = searchField.getText().trim().toLowerCase();
        contactTableModel.setRowCount(0);
        for (Contact contact : contacts) {
            if (contact.getName().toLowerCase().contains(query) || contact.getAddress().toLowerCase().contains(query)) {
                contactTableModel.addRow(new Object[]{contact.getName(), contact.getAddress(), contact.getNumber()});
            }
        }
        searchField.setText("");
    }

    private void deleteContact() {
        int row = contactTable.getSelectedRow();
        if (row == -1) return;
        contacts.remove(row);
        refreshTable();
    }

    private void refreshTable() {
        contactTableModel.setRowCount(0);
        for (Contact contact : contacts) {
            contactTableModel.addRow(new Object[]{contact.getName(), contact.getAddress(), contact.getNumber()});
        }
    }

    private void saveContactsToFile() {
        try (PrintWriter writer = new PrintWriter("contacts.csv")) {
            for (Contact c : contacts) {
                writer.printf("%s,%s,%s%n", c.getName(), c.getAddress(), c.getNumber());
            }
            JOptionPane.showMessageDialog(this, "Contacts saved successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving contacts.");
            e.printStackTrace();
        }
    }

    private void loadContactsFromFile() {
        contacts.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("contacts.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    contacts.add(new Contact(parts[0], parts[1], parts[2]));
                }
            }
            sortContacts();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Contacts loaded successfully.");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "contacts.csv not found.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading contacts.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Phonebook::new);
    }
}
