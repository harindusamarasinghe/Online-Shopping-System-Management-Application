package CourseWork;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart extends JFrame {
    private User currentUser;
    private ArrayList<CartItem> cartList = new ArrayList<>();

    private Map<String, Integer> categoryCount = new HashMap<>();

    JTable table;
    JTextArea textArea;
    JButton proceedButton;
    String[] tableColumns = {"Product", "Quantity", "Price"};
    DefaultTableModel tableModel;
    double total;
    private GUI guiReference;


    // Constructor for the ShoppingCart class
    ShoppingCart(User user, GUI guiReference) {

        // Set the current user and reference to the GUI
        this.currentUser = user;
        this.guiReference = guiReference;

        // Create a DefaultTableModel with initial data and table columns
        tableModel = new DefaultTableModel(new String[0][0], tableColumns);

        // Create a JTable with the initialized table model
        table = new JTable(tableModel);

        // Set font for the table header
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 15));

        // Create a JTextArea for additional information and set its initial content
        textArea = new JTextArea(10, 30);
        textArea.setText(setTextArea());

        // Create a JButton for proceeding with the shopping cart
        proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onProceedButtonClick(); // Handle the click event for the "Proceed" button
            }
        });

        // Create a panel for the text area with a BorderLayout
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.add(new JScrollPane(textArea)); // Add a JScrollPane to the text area

        // Create a panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(proceedButton);

        // Set the main frame layout to BorderLayout
        this.setLayout(new BorderLayout());

        // Add the table at the top of the frame
        this.add(new JScrollPane(table), BorderLayout.NORTH);

        // Add the text area panel in the center of the frame
        this.add(textAreaPanel, BorderLayout.CENTER);

        // Add the button panel at the bottom of the frame
        this.add(buttonPanel, BorderLayout.SOUTH);
    }


    private void onProceedButtonClick() {
        updateProductItems();
        // Clear the cart list
        cartList.clear();
        // Reset the category count
        categoryCount.clear();
        // Update the table and text area
        updateTable();
        updateTextArea();
        currentUser.setFirstOrder(false);
        updateUserDetails();
        updateGuiTable();
        System.out.println("User "+currentUser.getUserName()+" has successfully proceeded an order!");

        // Display the success message
        JOptionPane.showMessageDialog(this, "Order Purchase successful!");
    }
    private void updateGuiTable() {
        if (guiReference != null) {
            guiReference.updateProductTable();
        }
    }
    private void updateUserDetails() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))){
            writer.write(currentUser.getUserName() + "," + currentUser.getPassword() + "," + currentUser.isFirstOrder());
            writer.newLine();
            System.out.println("User details have been updated!");
        }catch (IOException e){
            System.out.println("An error has occurred while the user saving to the System !");
            e.printStackTrace();
    }
    }

    private void updateProductItems() {
        for (CartItem cartItem : cartList) {
            for (Product product : Product.productList) {
                if (product.getProductID().equals(cartItem.getProduct().getProductID())) {
                    int newQuantity = product.getAvailableItems() - cartItem.getQuantity();
                    product.setAvailableItems(Math.max(newQuantity, 0));
                }
            }

        }
        saveInFile();
    }
    public void saveInFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("product.txt"))){
            for (Product products:
                    Product.productList) {
                writer.write(products.getProductID()+","+products.getProductName()+","+products.getAvailableItems()+","+products.getPrice());

                if (products.getClass()== Clothing.class){
                    Clothing clothing = (Clothing) products;
                    writer.write(","+clothing.getSize()+","+clothing.getColour()+",Clothing");
                }else{
                    Electronics electronics = (Electronics) products;
                    writer.write(","+electronics.getBrand()+","+electronics.getWarranty()+",Electronic");

                }
                writer.newLine();

            }
            System.out.println("All the Products has been Successfully saved in the file!");
        }catch (IOException e){
            System.out.println("An error has occurred while saving to the File !");
            e.printStackTrace();
        }

    }

    public String setTextArea() {
        StringBuilder text = new StringBuilder();
        text.append("Total ").append(calTotal()).append("\n");
        // Check if the user's firstOrder attribute is true
        if (currentUser.isFirstOrder()) {
            double discount = 0.1 * calTotal();
            discount = Math.round(discount * 100.0) / 100.0; // Round off to two decimal places
            text.append("First Purchase Discount (10%): -$").append(discount).append("\n");
            total -= discount; // Apply the discount to the total
        } else {
            text.append("First Purchase Discount (10%): -$ 0\n");
        }

        if (shouldApplyDiscount()) {
            // Apply a 20% discount
            double categoryDiscount = 0.2 * calTotal();
            categoryDiscount = Math.round(categoryDiscount * 100.0) / 100.0;
            text.append("More than 3 Items in a Category Discount (20%): -$").append(categoryDiscount).append("\n");
            total -= categoryDiscount;
        } else {
            text.append("More than 3 Items in a Category Discount (20%): -$0\n");
        }
        text.append("\n").append("Final Total ").append(total).append("\n");

        return text.toString();
    }
    private double calCategoryTotal(String category) {
        double categoryTotal = 0.0;

        for (CartItem cartItem : cartList) {
            if (cartItem.getProduct() instanceof Clothing && category.equals("Clothing")) {
                categoryTotal += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            } else if (cartItem.getProduct() instanceof Electronics && category.equals("Electronics")) {
                categoryTotal += cartItem.getProduct().getPrice() * cartItem.getQuantity();
            }
        }

        return categoryTotal;
    }

    private boolean shouldApplyDiscount() {
        int clothingCount = 0;
        int electronicsCount = 0;

        for (CartItem cartItem : cartList) {
            if (cartItem.getProduct() instanceof Clothing) {
                clothingCount += cartItem.getQuantity();
            } else if (cartItem.getProduct() instanceof Electronics) {
                electronicsCount += cartItem.getQuantity();
            }
        }

        return clothingCount >= 3 || electronicsCount >= 3;
    }


    public void addProduct(Product product) {
        displayProduct(product.getProductID(), product instanceof Clothing ? "Clothing" : "Electronics");
    }


    public double calTotal() {
        total = 0.0;
        int columnIndex = 2;

        for (int i = 0; i < table.getRowCount(); i++) {
            total += Double.parseDouble(String.valueOf(table.getValueAt(i, columnIndex)));
        }

        return total;
    }

    private void updateTable() {
        tableModel.setRowCount(0); // Clear the existing rows

        for (CartItem cartItem : cartList) {
            Object[] rowData = {(cartItem.getProduct().getProductID() + " " + cartItem.getProduct().getProductName()),
                    cartItem.getQuantity(), cartItem.getProduct().getPrice() * cartItem.getQuantity()};
            tableModel.addRow(rowData);
        }
    }

    public void displayProduct(String productId, String category) {
        // Check if the product is already in the cart
        for (CartItem cartItem : cartList) {
            if (cartItem.getProduct().getProductID().equals(productId)) {
                // If yes, just update the quantity
                cartItem.incrementQuantity();
                updateTable();
                updateTextArea();
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                return;
            }
        }

        // If the product is not found, add it to the cartList with quantity 1
        for (Product originalProduct : Product.productList) {
            if (originalProduct.getProductID().equals(productId)) {
                CartItem newCartItem = new CartItem(originalProduct);
                cartList.add(newCartItem); // Add the new product to the cartList
                updateTable();
                updateTextArea();
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                return;
            }
        }

    }

    public void updateTextArea() {
        textArea.setText(setTextArea());
    }

    private class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product) {
            this.product = product;
            this.quantity = 1; // Initialize quantity to 1
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            this.quantity++;
        }
    }
}

