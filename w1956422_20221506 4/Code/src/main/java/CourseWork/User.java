package CourseWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

//Class representing a user with username, password, and firstOrder flag.
public class User {
    private String userName;
    private String password;

    private boolean firstOrder;

    // Getters and setters
    public void setFirstOrder(boolean firstOrder) {
        this.firstOrder = firstOrder;
    }

    public boolean isFirstOrder() {
        return firstOrder;
    }

    //constructor
    public User(String userName, String password, boolean firstOrder) {
        this.userName = userName;
        this.password = password;
        this.firstOrder=firstOrder;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}

//JFrame class for handling user login and signup functionalities.
class LoginSignupGUI extends JFrame {

    private ArrayList<User> userList;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginSignupGUI() {
        userList =loadFromFile();
        // Set up the JFrame
        setTitle("Login / Signup");
        setSize(300,150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });

        // Set layout
        setLayout(new FlowLayout());

        // Add components to the JFrame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signupButton);

        // Display the JFrame
        setVisible(true);
    }

    // Method to handle login process
    private void login() {
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());


        // Check if username and password match with any user in the list
        for (User user : userList) {
            if (user.getUserName().equals(enteredUsername) && user.getPassword().equals(enteredPassword)) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                openShoppingCart(user); // Open shopping cart on successful login
                return;
            }
        }
        // If no match found, show error message
        JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
    }

    // Method to load users from a file
    private static ArrayList<User> loadFromFile(){
        ArrayList<User> userList =new ArrayList<>();
        String line;
        try(BufferedReader reader = new BufferedReader(new FileReader("users.txt"))){

            while((line = reader.readLine()) != null){
                String []attribute =line.split(",");
                if (attribute.length == 3){
                    String userName=attribute[0];
                    String password =attribute[1];
                    User newUser = new User(userName,password,true);
                    userList.add(newUser);
                }

            }

        }catch (FileNotFoundException e) {
            // Handle the case where the file doesn't exist
            System.out.println("The file 'users.txt' does not exist.");
            // Initialize productList to an empty ArrayList
            userList =new ArrayList<>();
        }catch (IOException e){
            System.out.println("An error occurred while reading from the file!");
            e.printStackTrace();
        }
        return userList;

    }

    // Method to handle signup process
    private void signup() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());

        // Check if the username is already taken
        for (User user : userList) {
            if (user.getUserName().equals(newUsername)) {
                JOptionPane.showMessageDialog(this, "Username already taken. Please choose another one.");
                return;
            }
        }

        // Add the new user to the list
        userList.add(new User(newUsername, newPassword,true));

        saveUsersToFile();
        openShoppingCart(new User(newUsername, newPassword,true));

        JOptionPane.showMessageDialog(this, "Signup successful!");
    }

    // Method to save users to a file
    public void saveUsersToFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) { // 'true' to append
            User lastUser = userList.get(userList.size() - 1); // Get the last user (newly added user)
            writer.write(lastUser.getUserName() + "," + lastUser.getPassword() + "," + lastUser.isFirstOrder());
            writer.newLine();
            System.out.println("New User has been Successfully saved in the file!");
        } catch (IOException e) {
            System.out.println("An error has occurred while the user saving to the System !");
            e.printStackTrace();
        }

    }
    // Method to open the shopping cart interface
    private void openShoppingCart(User user) {
        GUI ShoppingCenter = new GUI(user);// Pass the user to the shopping cart
        ShoppingCenter.setTitle("Westminster Shopping Centre");
        ShoppingCenter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ShoppingCenter.pack();
        ShoppingCenter.setVisible(true);
        dispose(); // Close the current login/signup window
    }

}
