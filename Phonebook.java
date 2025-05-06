import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

class Contact {
    private String name, address, number, status;

    public Contact(String name, String address, String number) {
        this.name = name;
        this.address = address;
        this.number = number;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getNumber() { return number; }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;
    
    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Background image not found.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(Color.DARK_GRAY);
        }
    }
}

public class Phonebook extends JFrame implements ActionListener {
    private ArrayList<Contact> contacts = new ArrayList<>();
    private DefaultTableModel contactTableModel;
    private JTable contactTable;
    private JTextField nameField, addressField, numberField, searchField;
    private JButton addButton, searchButton, deleteButton, sortButton;

    public Phonebook() {
        setTitle("Yellow Pages");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("C:\\Users\\babal\\OneDrive\\Pictures\\library bg.jpg");
        backgroundPanel.setLayout(new BorderLayout());

        contactTableModel = new DefaultTableModel(new String[]{"Name", "Address", "Number"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        contactTable = new JTable(contactTableModel);
        contactTable.setForeground(Color.WHITE);
        contactTable.setBackground(Color.DARK_GRAY);

        JScrollPane tableScrollPane = new JScrollPane(contactTable);
        tableScrollPane.getViewport().setOpaque(false);

        JTableHeader header = contactTable.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);

        nameField = new JTextField(15);
        addressField = new JTextField(15);
        numberField = new JTextField(15);
        searchField = new JTextField(15);

        addButton = new JButton("Add Contact");
        searchButton = new JButton("Search");
        deleteButton = new JButton("Delete");
        sortButton = new JButton("Sort by Name");

        addButton.addActionListener(this);
        searchButton.addActionListener(this);
        deleteButton.addActionListener(this);
        sortButton.addActionListener(this);

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

        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(searchPanel, BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        contentPanel.add(tableScrollPane, BorderLayout.EAST);

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) addContact();
        else if (e.getSource() == searchButton) searchContact();
        else if (e.getSource() == deleteButton) deleteContact();
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String number = numberField.getText().trim();
        if (name.isEmpty() || address.isEmpty() || number.isEmpty()) return;
        for (Contact contact : contacts) {
            if (contact.getNumber().equals(number) || (contact.getName().equalsIgnoreCase(name) && contact.getAddress().equalsIgnoreCase(address))) return;
        }
        contacts.add(new Contact(name, address, number));
        sortContacts();
        refreshTable();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Phonebook::new);
    }
}
