package CourseWork;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener {
    // Class attributes
    private User currentUser;
    private String[] combo = {"All", "Electronics", "Clothing"};
    private String[] tableColumns = {"Product ID", "Product Name", "Category", "Available Items", "Price", "Info"};
    private String[][] tableData = getData(); // Method to retrieve data for the table
    private DefaultTableModel tableModel;
    private JTextArea textArea;// Area to display product details
    private JTable table;// Table to display product listings
    private JComboBox<String> comboBox;// Dropdown for product categories
    private JButton cartBtn, addCartBtn, exitBtn; // Buttons for cart, adding to cart, and exit
    private ShoppingCart shoppingCart;  // Shopping cart object

    // Constructor
    GUI(User user) {
        this.currentUser = user;
        setupGUI();
    }

    // Set up the entire GUI layout and components
    private void setupGUI() {
        setTitle("Westminster Shopping Center");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setupTopPanel();
        setupCenterPanel();
        setupBottomPanel();
        setupTable();

        shoppingCart = new ShoppingCart(currentUser, this);
        pack(); // Adjusts the size of the frame to fit its content
    }

    // Set up the top panel
    private void setupTopPanel() {
        JLabel jLabel = new JLabel("Select Product Category");
        comboBox = new JComboBox<>(combo);
        comboBox.addActionListener(this);

        cartBtn = new JButton("Shopping Cart");
        cartBtn.addActionListener(this);

        JPanel topPanel = new JPanel();
        topPanel.add(jLabel);
        topPanel.add(comboBox);
        topPanel.add(cartBtn);
        add(topPanel, BorderLayout.NORTH);
    }

    // Set up the center panel
    private void setupCenterPanel() {
        // Initialize table model and table
        tableModel = new DefaultTableModel(tableData, tableColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        table = new JTable(tableModel);
        JScrollPane jScrollPane = new JScrollPane(table); // Scroll pane for table

        textArea = new JTextArea(15, 50);
        textArea.setEditable(false); // Make text area non-editable
        addCartBtn = new JButton("Add to Shopping cart");
        addCartBtn.addActionListener(this);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.add(textArea, BorderLayout.CENTER);
        detailsPanel.add(addCartBtn, BorderLayout.SOUTH);

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(jScrollPane, BorderLayout.CENTER);
        centerPanel.add(detailsScrollPane, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    // Setup the bottom panel
    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel();
        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(this);
        bottomPanel.add(exitBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Set up the table with custom renderers and listeners
    private void setupTable() {
        // Set font for the table header
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));

        // Enable grid lines, set grid color, and preferred viewport size
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setPreferredScrollableViewportSize(new Dimension(800, 800));

        // Set a custom cell renderer for all cells in the table
        table.setDefaultRenderer(Object.class, new RowRenderer());

        // Add a list selection listener to the table's selection model
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Check if the selection is not being adjusted and a row is selected
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    // Update details in the detailsTextArea based on the selected row
                    updateDetailsTextArea(table.getSelectedRow());
                }
            }
        });
    }


    // Handle GUI component events
    public void actionPerformed(ActionEvent e) {
        // Check if the event is triggered by the comboBox
        if (e.getSource() == comboBox) {
            // Retrieve the selected category from the comboBox
            String selectedCategory = comboBox.getSelectedItem().toString();

            // Update the table data based on the selected category
            updateTableData(selectedCategory);
        }
        // Check if the event is triggered by the cartBtn (shopping cart button)
        else if (e.getSource() == cartBtn) {
            // Open the shopping cart
            openShoppingCart();
        }
        // Check if the event is triggered by the addCartBtn (add to cart button)
        else if (e.getSource() == addCartBtn) {
            // Add the selected item to the shopping cart
            addToCart();
        }

        // Check if the event is triggered by the exitBtn (exit button)
        if (e.getSource() == exitBtn) {
            // Close the current GUI window
            this.dispose();

            // Create an instance of WestminsterShoppingManager to interact with the console menu
            WestminsterShoppingManager westminsterShoppingManager = new WestminsterShoppingManager();

            // Display the console menu using the MenuTask method
            westminsterShoppingManager.MenuTask();
        }
    }


    // Update table data based on the selected category
    private void updateTableData(String selectedCategory) {
        // Declare a new DefaultTableModel to hold the updated data
        DefaultTableModel newTableModel;

        // Check if the selected category is "All"
        if (selectedCategory.equals("All")) {
            // If "All" is selected, create a new DefaultTableModel with all data and columns
            newTableModel = new DefaultTableModel(getData(), tableColumns);
        } else {
            // If a specific category is selected, create a new DefaultTableModel with filtered data and columns
            newTableModel = new DefaultTableModel(getDataByCategory(selectedCategory), tableColumns);
        }

        // Set the newly created model as the model for the table
        table.setModel(newTableModel);
    }


    // Retrieve data filtered by a specific category
    private String[][] getDataByCategory(String category) {
        // Create an ArrayList to hold the filtered data
        ArrayList<String[]> filteredData = new ArrayList<>();

        // Iterate through each row of the complete data obtained from getData() method
        for (String[] row : getData()) {
            // Check if the category of the current row matches the specified category or if "All" categories are requested
            if (row[2].equals(category) || category.equals("All")) {
                // If the condition is met, add the current row to the filtered data
                filteredData.add(row);
            }
        }

        // Convert the ArrayList of String arrays to a 2D String array and return the filtered data
        return filteredData.toArray(new String[0][]);
    }


    private void updateDetailsTextArea(int selectedRow) {
        String productDetails = getProductDetails(selectedRow);
        textArea.setText(productDetails);
    }

    // Add the selected product to the shopping cart
    private void addToCart() {
        // Get the index of the currently selected row in the table
        int selectedRow = table.getSelectedRow();

        // Check if a valid row is selected (-1 indicates no selection)
        if (selectedRow != -1) {
            // Retrieve product details from the selected row
            String productId = (String) table.getValueAt(selectedRow, 0);
            String category = (String) table.getValueAt(selectedRow, 2);

            // Add the selected product to the existing shopping cart
            shoppingCart.displayProduct(productId, category);

            // Update the text area in the shopping cart to reflect the changes
            shoppingCart.updateTextArea();
        }
    }

    private void openShoppingCart() {
        // Set up the shopping cart GUI
        shoppingCart.setTitle("Shopping Cart");
        shoppingCart.pack();
        shoppingCart.setLayout(new BorderLayout());
        shoppingCart.setLocationRelativeTo(this); // Set the location relative to the main GUI
        shoppingCart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the shopping cart window on close

        // Make the shopping cart visible
        shoppingCart.setVisible(true);
    }

    // Retrieve and format details of the selected product
    private String getProductDetails(int selectedRow) {
        // Create a StringBuilder to build the product details string
        StringBuilder ProDetails = new StringBuilder();

        // Append Product ID, Category, Product Name, and Price
        ProDetails.append("Selected Product Details\n");
        ProDetails.append("Product ID: ").append(table.getValueAt(selectedRow, 0)).append("\n");
        ProDetails.append("Category: ").append(table.getValueAt(selectedRow, 2)).append("\n");
        ProDetails.append("Product Name: ").append(table.getValueAt(selectedRow, 1)).append("\n");
        ProDetails.append("Price: ").append(table.getValueAt(selectedRow, 4)).append("\n");

        // Extract infoValue column from the table
        String infoValue = String.valueOf(table.getValueAt(selectedRow, 5));
        String[] infoDetail = infoValue.split(",");

        // Check if infoDetail has at least 2 elements
        if (infoDetail.length > 1) {

            // Check the category of the product to determine the type of additional information
            if (table.getValueAt(selectedRow, 2).equals("Clothing")) {
                // If the category is Clothing, append Size and Colour
                ProDetails.append("Size: ").append(infoDetail[0]).append("\n");
                ProDetails.append("Colour: ").append(infoDetail[1]).append("\n");
            } else {
                // If the category is not Clothing, append Brand and Warranty
                ProDetails.append("Brand: ").append(infoDetail[0]).append("\n");
                ProDetails.append("Warranty: ").append(infoDetail[1]).append("\n");
            }
        } else {
            // Handle the case where infoDetail doesn't have at least 2 elements
            ProDetails.append("Details are incomplete. Manager has not given all the Details.");
        }

        // Convert the StringBuilder to a String and return the product details
        return ProDetails.toString();
    }

    // Retrieve product data from the Product.productList and format it into a 2D array
    public String[][] getData() {
        // Create a 2D array to hold the product data
        String[][] productData = new String[Product.productList.size()][7];

        // Iterate through each product in the Product.productList
        for (int i = 0; i < Product.productList.size(); i++) {
            // Retrieve common product details (ProductID, ProductName, Price, AvailableItems)
            productData[i][0] = Product.productList.get(i).getProductID();
            productData[i][1] = Product.productList.get(i).getProductName();
            productData[i][4] = String.valueOf(Product.productList.get(i).getPrice());
            productData[i][3] = String.valueOf(Product.productList.get(i).getAvailableItems());

            // Check if the product is an instance of Clothing
            if (Product.productList.get(i) instanceof Clothing) {
                Clothing clothing = (Clothing) Product.productList.get(i);
                productData[i][2] = "Clothing"; // Set category to Clothing
                productData[i][5] = clothing.getSize() + "," + clothing.getColour(); // Set additional information
            }
            // Check if the product is an instance of Electronics
            else if (Product.productList.get(i) instanceof Electronics) {
                Electronics electronics = (Electronics) Product.productList.get(i);
                productData[i][2] = "Electronics"; // Set category to Electronics
                productData[i][5] = electronics.getBrand() + "," + electronics.getWarranty(); // Set additional information
            }
        }
        return productData;
    }

    // Update the product table model with new data and notify listeners of the change
    public void updateProductTable() {
        // Set the data vector of the table model using the updated product data
        tableModel.setDataVector(getData(), tableColumns);

        // Notify listeners that the table data has changed
        tableModel.fireTableDataChanged();
    }

}
// Creating a custom TableCellRenderer by extending DefaultTableCellRenderer
class RowRenderer extends DefaultTableCellRenderer {

    // Override the getTableCellRendererComponent method
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Call the superclass method to get the default rendering component
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Retrieve the number of available items from column 3 as it is available Items column.
        int availableItems = Integer.parseInt(table.getValueAt(row, 3).toString());

        // Change the background color if available items are less than 4
        if (availableItems < 4) {
            c.setBackground(Color.RED); // Set the background color to red if available items are low
        } else {
            c.setBackground(table.getBackground()); // Set the background color to the default color of the table
        }

        // Set text color for selected rows
        if (isSelected) {
            c.setForeground(Color.BLUE); // Set the text color to blue for selected rows
        } else {
            c.setForeground(table.getForeground()); // Set the text color to the default color of the table for non-selected rows
        }

        // Return the customized component
        return c;
    }
}

