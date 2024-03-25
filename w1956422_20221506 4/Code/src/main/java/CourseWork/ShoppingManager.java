package CourseWork;

import java.io.*;
import java.util.*;

public interface ShoppingManager {
    public abstract void addProduct();
    public abstract void deleteProduct();
    public abstract void printProducts();

    public abstract void saveInFile();
}

class WestminsterShoppingManager implements ShoppingManager{

    //Menu options for the shopping manager
    static String[] menu = {"1.Add a new product","2.Delete a product","3.Print the list of the products","4.Save in a file","5.Open the GUI","0.Quit"};

    //saving current products to a file
    public void saveInFile(){
        //using buffer writer to write to a file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("product.txt"))){
            for (Product products:
                    Product.productList) {
                writer.write(products.getProductID()+","+products.getProductName()+","+products.getAvailableItems()+","+products.getPrice());

                // Differentiating between Clothing and Electronics and writing their specific details.
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

    //Prints details of all products in the productList.
    public void printProducts(){
        // Checking if there are products to print.
        if (Product.productList.size()==0){
            System.out.println("There is no Products in the system to print");
            return;
        }
        System.out.println("Product List in the System\n-------------------");
        bubbleSortByID(Product.productList); // Sort the product list by ID

        // Printing each product details.
        for (Product product:
                Product.productList) {
            System.out.println("Product ID :"+product.getProductID());
            System.out.println("Product Name :"+product.getProductName());
            System.out.println("Product Price :"+product.getPrice());
            System.out.println("Available Items "+product.getAvailableItems());
            // Checking for type of product and printing additional details.
            if (product.getClass()== Clothing.class){
                System.out.println("Product type : Clothing");
                Clothing clothingItem = (Clothing) product;
                System.out.println("Clothing Size :"+clothingItem.getSize());
                System.out.println("Clothing Colour :"+clothingItem.getColour());
            }else{
                System.out.println("Product type : Electronics");
                Electronics electronicItem = (Electronics) product;
                System.out.println("Electronic Item Brand :"+electronicItem.getBrand());
                System.out.println("Electronic Item's warranty period :"+electronicItem.getWarranty());
            }
            System.out.println(" ");
        }

    }

    //bubble sort algorithm used to sort productID's alphabetically
    public static void bubbleSortByID(List<Product> productList) {
        int n = productList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String ID1 = productList.get(j).getProductID();
                String ID2 = productList.get(j + 1).getProductID();
                if (ID1.compareToIgnoreCase(ID2) > 0) {
                    // Swap products if they are in the wrong order
                    Product temp = productList.get(j);
                    productList.set(j, productList.get(j + 1));
                    productList.set(j + 1, temp);
                }
            }
        }
    }

    //Deletes a product from the productList based on user input.
    public void deleteProduct() {
        boolean found = false;
        ArrayList<Product> productToRemove;
        String ID;
        if (Product.productList.size()==0){
            System.out.println("There is no Products in the System to Delete !");
            return;
        }else {
            productToRemove = new ArrayList<Product>();
            System.out.println("Enter the ID of the Product you want to delete : ");
            Scanner input = new Scanner(System.in);
            ID = input.next();

            for (Product products : Product.productList) {
                if (products.getProductID().trim().equals(ID.trim())) {
                    // Retrieving the product we need to delete because
                    // It is not allowed to modify (delete in here) from a collection directly while iterating over it.
                    productToRemove.add(products);

                    System.out.println("XXXXX Details of the Product removing XXXXX");
                    if (products.getClass() == Clothing.class) {
                        System.out.println("Product type : Clothing");
                    } else {
                        System.out.println("Product type : Electronics");
                    }
                    System.out.println("Product ID :"+ products.getProductID());
                    System.out.println("Product Name :" + products.getProductName());
                    System.out.println("Product Price :" + products.getPrice());
                    System.out.println(" ");
                    found = true;
                }
            }
        }


        if (!found) {
            System.out.println("Sorry couldn't find any matching products with ID " + ID + "\n");
            return;
        }

        //removing all the products in the productToRemove list from ProductList array
        for (int i = 0; i < productToRemove.size(); i++) {
            Product.productList.remove(productToRemove.get(i));
            System.out.println("Product Successfully Deleted From the System");
        }

        System.out.println("There are " + Product.productList.size() + " Number of Products Remaining in the System !");
    }

    public void addProduct(){
        System.out.println("What type of Product do You want to Add?\n1.Clothing\n2.Electronics");
        Scanner input = new Scanner(System.in);
        try{
        int type=input.nextInt();
        input.nextLine();
        if (type==1){
            //validating ID to be only a single word
            String proId = getSingleWordInput(input, "Enter the Product ID of the Clothing :");
            System.out.println("Enter the Product Name of the Clothing :");
            String proName = input.nextLine();
            System.out.println("Enter the no of items of the Clothing");
            int items = getIntInput(input);
            System.out.println("Enter the price of the Clothing");
            float price = getFloatInput(input);
            input.nextLine();

            String size = "";
            boolean validSize = false;
            while (!validSize) {
                System.out.println("Enter the size of the Clothing (S, M, L, XL, XXL):");
                size = input.next().trim().toUpperCase(); // taking input, trim whitespace, and convert to uppercase

                if (size.equals("S") || size.equals("M") || size.equals("L") || size.equals("XL") || size.equals("XXL")) {
                    validSize = true;
                } else {
                    System.out.println("Invalid size. Please enter one of the following sizes: S, M, L, XL, XXL.");
                }
            }
            System.out.println("Enter the colour of the Clothing :");
            String colour = input.nextLine();

            Clothing newCloth = new Clothing(proId,proName,items,price,size,colour);
            Product.productList.add(newCloth);
            System.out.println("\nNew Cloth successfully added to the System !");

        }else if (type==2){
            String proId = getSingleWordInput(input, "Enter the Product ID of the Electronic :");
            System.out.println("Enter the Product Name of the Electronic :");
            String proName = input.nextLine();
            System.out.println("Enter the number of "+proName+"Adding to the system :");
            int items = getIntInput(input);
            System.out.println("Enter the price of the Electronic :");
            float price = getFloatInput(input);
            input.nextLine();
            System.out.println("Enter the Brand of the Electronic :");
            String brand = input.nextLine();
            System.out.println("Enter the warranty of the Electronic (In months) :");
            int warranty = getIntInput(input);
            while (warranty>12){
                System.out.println("Enter a number between 1-12 :");
                warranty = getIntInput(input);
            }

            Electronics newElectro = new Electronics(proId,proName,items,price,brand,warranty);
            Product.productList.add(newElectro);;
            System.out.println("\nNew Electronic successfully added to the System !");

        }else{
            System.out.println("Invalid input");
            addProduct();

        }
    }catch (InputMismatchException e){
            System.out.println("Invalid input! Please enter a valid Integer.");
            addProduct();
        }
    }
    // Gets an int input from the user with error handling
    private int getIntInput(Scanner input) {
        while(true){
            try{
                return input.nextInt();
            } catch (InputMismatchException e){
                input.nextLine(); // clear buffer
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }
    // Gets a float input from the user with error handling
    private float getFloatInput(Scanner input) {
        while(true){
            try{
                return input.nextFloat();
            } catch (InputMismatchException e){
                input.nextLine(); // clear buffer
                System.out.println("Invalid input. Please enter a valid decimal number.");
            }
        }
    }
    private String getSingleWordInput(Scanner input, String prompt) {
        String result;
        while(true){
            System.out.println(prompt);
            result = input.nextLine();
            if(result.split("\\s+").length == 1){
                break;
            }
            System.out.println("Invalid input. Please enter a single word.");
        }
        return result;
    }


    public void MenuTask() {
        boolean exit = false;

        while (!exit) {
            System.out.println("MENU");
            for (int i = 0; i < menu.length; i++) {
                System.out.println(menu[i]);
            }
            System.out.println("Which action do you want to choose? ");
            Scanner input = new Scanner(System.in);

            try {
                int response = input.nextInt();
                switch (response) {
                    case 1:
                        if (Product.productList.size() <= 50) {
                            addProduct();
                            System.out.println("There are " + Product.productList.size() + " Products In the System!\n");
                        } else {
                            System.out.println("Sorry! You have 50 Products already in the System ");
                        }
                        break;
                    case 2:
                        deleteProduct();
                        break;
                    case 3:
                        printProducts();
                        break;
                    case 4:
                        saveInFile();
                        break;
                    case 5:
                        new LoginSignupGUI();
                        break;
                    case 0:
                        System.out.println("Bye, Manager!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Sorry! Invalid Response. Try Again..");
                }


            } catch (InputMismatchException e) {
                System.out.println("Invalid Response. Please Select a number between 1-4");
                input.nextLine();
            }
        }
    }


    public static void main(String[] args) {
        WestminsterShoppingManager Manager = new WestminsterShoppingManager();
        Manager.MenuTask();
    }

}
