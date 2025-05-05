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
    private String name, author, number, status;

    public Contact(String name, String author, String number) {
        this.name = name;
        this.author = author;
        this.number = number;
        this.status = "Available";
    }

    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getNumber() { return number; }
    public String getStatus() { return status; }
    public void checkOut() { this.status = "Checked Out"; }
    public void returnContact() { this.status = "Available"; }
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
    private JTextField nameField, authorField, numberField, searchField;
    private JButton addButton, searchButton, deleteButton, checkOutButton, returnButton, sortButton;

    public Phonebook() {
        setTitle("Phonebook Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("C:\\Users\\babal\\OneDrive\\Pictures\\library bg.jpg");
        backgroundPanel.setLayout(new BorderLayout());

        contactTableModel = new DefaultTableModel(new String[]{"Title", "Author", "Control Number", "Status"}, 0) {
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
        authorField = new JTextField(15);
        numberField = new JTextField(15);
        searchField = new JTextField(15);

        addButton = new JButton("Add Contact");
        searchButton = new JButton("Search");
        deleteButton = new JButton("Delete");
        checkOutButton = new JButton("Check Out");
        returnButton = new JButton("Return");
        sortButton = new JButton("Sort by Name");

        addButton.addActionListener(this);
        searchButton.addActionListener(this);
        deleteButton.addActionListener(this);
        checkOutButton.addActionListener(this);
        returnButton.addActionListener(this);
        sortButton.addActionListener(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Author:")); inputPanel.add(authorField);
        inputPanel.add(new JLabel("Number:")); inputPanel.add(numberField);
        inputPanel.add(addButton);

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.add(checkOutButton);
        actionPanel.add(returnButton);
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
        else if (e.getSource() == checkOutButton) checkOutContact();
        else if (e.getSource() == returnButton) returnContact();
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String author = authorField.getText().trim();
        String number = numberField.getText().trim();
        if (name.isEmpty() || author.isEmpty() || number.isEmpty()) return;
        for (Contact contact : contacts) {
            if (contact.getNumber().equals(number) || (contact.getName().equalsIgnoreCase(name) && contact.getAuthor().equalsIgnoreCase(author))) return;
        }
        contacts.add(new Contact(name, author, number));
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
            if (contact.getName().toLowerCase().contains(query) || contact.getAuthor().toLowerCase().contains(query)) {
                contactTableModel.addRow(new Object[]{contact.getName(), contact.getAuthor(), contact.getNumber(), contact.getStatus()});
            }
        }
    }

        private void deleteContact() {
        int row = contactTable.getSelectedRow();
        if (row == -1) return;
        contacts.remove(row);
        refreshTable();
    }

    private void checkOutContact() {
        int row = contactTable.getSelectedRow();
        if (row == -1) return;
        contacts.get(row).checkOut();
        refreshTable();
    }

    private void returnContact() {
        int row = contactTable.getSelectedRow();
        if (row == -1) return;
        contacts.get(row).returnContact();
        refreshTable();
    }

    private void refreshTable() {
        contactTableModel.setRowCount(0);
        for (Contact contact : contacts) {
            contactTableModel.addRow(new Object[]{contact.getName(), contact.getAuthor(), contact.getNumber(), contact.getStatus()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Phonebook::new);
    }
}
