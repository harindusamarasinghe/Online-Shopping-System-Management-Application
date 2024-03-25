package CourseWork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Product {
    private String productID;
    private String productName;
    private int availableItems;
    private float price;

    // Static list to hold all products, loaded initially from a file.
    public static ArrayList<Product> productList =loadFromFile() ;

    // Getters and setters for product attributes.
    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public int getAvailableItems() {
        return availableItems;
    }

    public float getPrice() {
        return price;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setAvailableItems(int availableItems) {
        this.availableItems = availableItems;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    // Constructor to initialize a product.
    public Product(String productID, String productName, int availableItems, float price) {
        this.productID = productID;
        this.productName = productName;
        this.availableItems = availableItems;
        this.price = price;
    }

    //Static method to load products from a file.
    public static ArrayList<Product> loadFromFile(){
        Product.productList = new ArrayList<>();
        String line;
        try(BufferedReader reader = new BufferedReader(new FileReader("product.txt"))){

            while((line = reader.readLine()) != null){
                String []attribute =line.split(",");
                // Check if the line has enough attributes to create a product.
                if (attribute.length >= 6){
                    String proID=attribute[0];
                    String proName =attribute[1];
                    int AvailableItem = Integer.parseInt(attribute[2]);
                    float price = Float.parseFloat(attribute[3]);
                    // Differentiate between Clothing and Electronics based on the attribute.
                    if (Objects.equals(attribute[6],"Clothing")){
                        //creating a clothing product
                        String size = attribute[4];
                        String colour = attribute[5];
                        Clothing clothing=new Clothing(proID,proName,AvailableItem,price,size,colour);
                        Product.productList.add(clothing);
                    }else{
                        //creating an Electronic product
                        String brand = attribute[4];
                        int warranty = Integer.parseInt(attribute[5]);
                        Electronics electronics=new Electronics(proID,proName,AvailableItem,price,brand,warranty);
                        Product.productList.add(electronics);
                    }
                }

            }

        }catch (FileNotFoundException e) {
            // Handle the case where the file doesn't exist
            System.out.println("The file 'product.txt' does not exist.");
            // Initialize productList to an empty ArrayList
            Product.productList = new ArrayList<>();
        }catch (IOException e){
            System.out.println("An error occurred while reading from the file!");
            e.printStackTrace();
        }
        return Product.productList;
    }
}
class Electronics extends Product{
    private String brand;
    private int warranty;

    // Getters and setters for Electronics subclass
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }

    public String getBrand() {
        return brand;
    }

    public int getWarranty() {
        return warranty;
    }

    public Electronics(String productID, String productName, int availableItems, float price, String brand, int warranty) {
        super(productID, productName, availableItems, price);
        this.brand = brand;
        this.warranty = warranty;
    }
}

class Clothing extends Product{
    private String size;
    private String colour;
    // Getters and setters for Clothing subclass

    public void setSize(String size) {
        this.size = size;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getSize() {
        return size;
    }

    public String getColour() {
        return colour;
    }

    public Clothing(String productID, String productName, int availableItems, float price, String size, String colour) {
        super(productID, productName, availableItems, price);
        this.size = size;
        this.colour = colour;
    }
}