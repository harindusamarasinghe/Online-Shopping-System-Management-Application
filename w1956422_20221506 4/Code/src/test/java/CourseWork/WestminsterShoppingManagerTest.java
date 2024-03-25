package CourseWork;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WestminsterShoppingManagerTest {

    private final InputStream originalIn = System.in;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUp() {
        Product.productList.clear();
    }

    @Before
    public void setUpInput() {
        testIn = new ByteArrayInputStream("".getBytes()); // Initialize with an empty string
        testOut = new ByteArrayOutputStream();
        System.setIn(testIn);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(originalIn);
    }

    @Test
    public void testAddProduct() {
        WestminsterShoppingManager manager = new WestminsterShoppingManager();

        // Simulate adding a product
        String simulatedUserInput = "1\nE001\nTest Product\n10\n100.0\nM\nBlue\n";
        testIn = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(testIn);
        manager.addProduct(); // Method that reads from System.in

        // Assert conditions to verify the correct addition of the product
        Assert.assertFalse("Product list should not be empty", Product.productList.isEmpty());
        Assert.assertEquals("Product list should have 1 product", 1, Product.productList.size());

    }

    @Test
    public void testDeleteProduct() {
        WestminsterShoppingManager manager = new WestminsterShoppingManager();

        // First, adding a product
        String addInput = "1\nE002\nTest Clothing\n10\n100.0\nM\nBlue\n";
        testIn = new ByteArrayInputStream(addInput.getBytes());
        System.setIn(testIn);
        manager.addProduct();

        // Ensuring the product was added
        Assert.assertEquals("Product list should have 1 product after addition", 1, Product.productList.size());

        //simulate deleting the product
        String deleteInput = "E002\n"; // Assuming this is the ID of the product to delete
        testIn = new ByteArrayInputStream(deleteInput.getBytes());
        System.setIn(testIn);
        manager.deleteProduct();

        // Check if the product was deleted
        Assert.assertEquals("Product list should not have any product after deletion", 0, Product.productList.size());
    }
    @Test
    public void testSaveInFile() throws IOException {
        WestminsterShoppingManager manager = new WestminsterShoppingManager();

        // Create an instance of clothing subclass of Product
        Clothing testProduct = new Clothing("C001", "Test Clothing", 10, 200, "M", "Blue");

        Product.productList.add(testProduct);

        // Save products to the file.
        manager.saveInFile();


        Path filePath = Paths.get("product.txt");

        // Verify: Check if the correct content is written to the file
        String fileContent = new String(Files.readAllBytes(filePath));
        String expectedContent = "C001,Test Clothing,10,200.0,M,Blue,Clothing";
        Assert.assertEquals("File content should match expected product details", expectedContent.trim(), fileContent.trim());

        // Cleanup: Clear the product list and deleting the file
        Product.productList.clear();
        Files.deleteIfExists(filePath);
    }




}

