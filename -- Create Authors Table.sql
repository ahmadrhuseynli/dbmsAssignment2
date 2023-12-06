CREATE TABLE Authors (
    AuthorID INT PRIMARY KEY,
    AuthorName VARCHAR(255),
    Birthdate DATE,
    Nationality VARCHAR(50)
);

CREATE TABLE Books (
    BookID INT PRIMARY KEY,
    Title VARCHAR(255),
    AuthorID INT,
    StockQuantity INT,
    ISBN VARCHAR(13),
    Price DECIMAL(10, 2),
    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID)
);

CREATE TABLE Customers (
    CustomerID INT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100),
    Phone VARCHAR(15)
);

CREATE TABLE Orders (
    OrderID INT PRIMARY KEY,
    CustomerID INT,
    BookID INT,
    Quantity INT,
    OrderDate DATE,
    TotalAmount DECIMAL(10, 2),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);


INSERT INTO Authors (AuthorID, AuthorName, Birthdate, Nationality) VALUES
(1, 'Author 1', '1990-01-01', 'Country A'),
(2, 'Author 2', '1985-03-15', 'Country B'),
(3, 'Author 3', '1978-07-20', 'Country C');

INSERT INTO Books (BookID, Title, AuthorID, StockQuantity, ISBN, Price) VALUES
(101, 'Book 1', 1, 50, 'ISBN001', 19.99),
(102, 'Book 2', 2, 30, 'ISBN002', 15.99),
(103, 'Book 3', 3, 20, 'ISBN003', 24.99);

INSERT INTO Customers (CustomerID, FirstName, LastName, Email, Phone) VALUES
(501, 'John', 'Doe', 'john.doe@email.com', '123-456-7890'),
(502, 'Jane', 'Smith', 'jane.smith@email.com', '987-654-3210');

INSERT INTO Orders (OrderID, CustomerID, BookID, Quantity, OrderDate, TotalAmount) VALUES
(1001, 501, 101, 2, '2023-01-15', 39.98),
(1002, 502, 102, 1, '2023-02-20', 15.99),
(1003, 501, 103, 3, '2023-03-25', 74.97);
