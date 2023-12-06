import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.math.BigDecimal;

public class BookstoreDBMS {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/myadadata";
    private static final String USERNAME = "myadadata";
    private static final String PASSWORD = "7777";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            displayTableNames(connection);
            displayTableColumns(connection, "Books");
            displayPrimaryKeys(connection, "Books");
            displayForeignKeys(connection, "Books");

            getAllBooks();
            updateBookDetails(101, 40, BigDecimal.valueOf(22.99));
            insertBook(new Book(0, "New Book", 1, 10, "ISBN004", BigDecimal.valueOf(29.99)));

            Customer customer = new Customer(503, "Alice", "Johnson", "alice.johnson@email.com", "555-123-4567");
            Book bookToOrder = new Book(0, "New Book", 1, 10, "ISBN004", BigDecimal.valueOf(29.99));
            Order order = new Order(1004, customer.getCustomerID(), bookToOrder.getBookID(), 2, java.sql.Date.valueOf("2023-04-30"), BigDecimal.valueOf(59.98));
            insertOrder(order);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayTableNames(Connection connection) throws SQLException {
        String tableNamesQuery = "SHOW TABLES";
        try (PreparedStatement preparedStatement = connection.prepareStatement(tableNamesQuery)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Table Names:");
                while (resultSet.next()) {
                    String tableName = resultSet.getString(1);
                    System.out.println(tableName);
                }
                System.out.println("------------------------");
            }
        }
    }

    private static void displayTableColumns(Connection connection, String tableName) throws SQLException {
        String columnsQuery = "DESCRIBE " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(columnsQuery)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Columns for Table " + tableName + ":");
                while (resultSet.next()) {
                    String columnName = resultSet.getString("Field");
                    String dataType = resultSet.getString("Type");
                    System.out.println(columnName + " (" + dataType + ")");
                }
                System.out.println("------------------------");
            }
        }
    }

    private static void displayPrimaryKeys(Connection connection, String tableName) throws SQLException {
        String primaryKeyQuery = "SHOW KEYS FROM " + tableName + " WHERE Key_name = 'PRIMARY'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(primaryKeyQuery)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Primary Keys for Table " + tableName + ":");
                while (resultSet.next()) {
                    String primaryKeyColumnName = resultSet.getString("Column_name");
                    System.out.println(primaryKeyColumnName);
                }
                System.out.println("------------------------");
            }
        }
    }

    private static void displayForeignKeys(Connection connection, String tableName) throws SQLException {
        String foreignKeysQuery = "SELECT * FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = '" + tableName + "' AND CONSTRAINT_NAME != 'PRIMARY'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(foreignKeysQuery)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Foreign Keys referencing Table " + tableName + ":");
                while (resultSet.next()) {
                    String foreignKeyName = resultSet.getString("COLUMN_NAME");
                    String referencedTableName = resultSet.getString("REFERENCED_TABLE_NAME");
                    String referencedColumnName = resultSet.getString("REFERENCED_COLUMN_NAME");
                    System.out.println("Foreign Key: " + foreignKeyName);
                    System.out.println("Referenced Table: " + referencedTableName);
                    System.out.println("Referenced Column: " + referencedColumnName);
                    System.out.println("------------------------");
                }
            }
        }
    }

    public static void getAllBooks() {
        String selectQuery = "SELECT * FROM Books";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("All Books:");
            while (resultSet.next()) {
                int bookID = resultSet.getInt("BookID");
                String title = resultSet.getString("Title");
                int authorID = resultSet.getInt("AuthorID");
                int stockQuantity = resultSet.getInt("StockQuantity");
                String isbn = resultSet.getString("ISBN");
                BigDecimal price = resultSet.getBigDecimal("Price");
                System.out.println("BookID: " + bookID + ", Title: " + title + ", AuthorID: " + authorID +
                        ", StockQuantity: " + stockQuantity + ", ISBN: " + isbn + ", Price: " + price);
            }
            System.out.println("------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBookDetails(int bookID, int newStockQuantity, BigDecimal newPrice) {
        String updateQuery = "UPDATE Books SET StockQuantity = ?, Price = ? WHERE BookID = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, newStockQuantity);
            preparedStatement.setBigDecimal(2, newPrice);
            preparedStatement.setInt(3, bookID);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertBook(Book book) {
        String insertQuery = "INSERT INTO Books (Title, AuthorID, StockQuantity, ISBN, Price) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setInt(2, book.getAuthorID());
            preparedStatement.setInt(3, book.getStockQuantity());
            preparedStatement.setString(4, book.getIsbn());
            preparedStatement.setBigDecimal(5, book.getPrice());
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertOrder(Order order) {
        String insertOrderQuery = "INSERT INTO Orders (OrderID, CustomerID, BookID, Quantity, OrderDate, TotalAmount) VALUES (?, ?, ?, ?, ?, ?)";
        String updateStockQuery = "UPDATE Books SET StockQuantity = StockQuantity - ? WHERE BookID = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement insertOrderStatement = connection.prepareStatement(insertOrderQuery);
             PreparedStatement updateStockStatement = connection.prepareStatement(updateStockQuery)) {

            // Setting values for the insert order query
            insertOrderStatement.setInt(1, order.getOrderID());
            insertOrderStatement.setInt(2, order.getCustomerID());
            insertOrderStatement.setInt(3, order.getBookID());
            insertOrderStatement.setInt(4, order.getQuantity());
            insertOrderStatement.setDate(5, order.getOrderDate());
            insertOrderStatement.setBigDecimal(6, order.getTotalAmount());

            // Setting values for the update stock query
            updateStockStatement.setInt(1, order.getQuantity());
            updateStockStatement.setInt(2, order.getBookID());

            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Execute the insert order query
                insertOrderStatement.executeUpdate();

                // Execute the update stock query
                updateStockStatement.executeUpdate();

                // Commit the transaction
                connection.commit();

                System.out.println("Order placed successfully.");
            } catch (SQLException e) {
                // Rollback the transaction in case of an exception
                connection.rollback();
                e.printStackTrace();
            } finally {
                // Reset auto-commit to true for subsequent queries
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Class definitions for Book, Author, Customer, and Order go here...
}


class Book {
    private int bookID;
    private String title;
    private int authorID;
    private int stockQuantity;
    private String isbn;
    private BigDecimal price;

    // Constructors, getters, and setters go here...

    public Book(int bookID, String title, int authorID, int stockQuantity, String isbn, BigDecimal price) {
        this.bookID = bookID;
        this.title = title;
        this.authorID = authorID;
        this.stockQuantity = stockQuantity;
        this.isbn = isbn;
        this.price = price;
    }

    // Getters and setters for each field...

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

class Author {
    private int authorID;
    private String authorName;
    private Date birthdate;
    private String nationality;

    // Constructors, getters, and setters go here...

    public Author(int authorID, String authorName, Date birthdate, String nationality) {
        this.authorID = authorID;
        this.authorName = authorName;
        this.birthdate = birthdate;
        this.nationality = nationality;
    }

    // Getters and setters for each field...

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}

class Customer {
    private int customerID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // Constructors, getters, and setters go here...

    public Customer(int customerID, String firstName, String lastName, String email, String phone) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters for each field...

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

class Order {
    private int orderID;
    private int customerID;
    private int bookID;
    private int quantity;
    private Date orderDate;
    private BigDecimal totalAmount;

    // Constructors, getters, and setters go here...

    public Order(int orderID, int customerID, int bookID, int quantity, Date orderDate, BigDecimal totalAmount) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.bookID = bookID;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    // Getters and setters for each field...

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
