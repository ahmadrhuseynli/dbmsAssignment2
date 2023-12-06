# dbmsAssignment2
# Bookstore Database Management System

## Overview

Welcome to the Bookstore Database Management System! This Java application is designed to manage information related to a bookstore, including books, authors, customers, and orders. Follow the instructions below to set up and run the application.

## Prerequisites

Before running the application, make sure you have the following prerequisites installed:

- Java Development Kit (JDK) 7 or later
- MySQL Database Server
- MySQL Connector/J (JDBC Driver)

## Setup

1. **Database Setup:**
   - Create a MySQL database for the application.
   - Execute the SQL scripts provided in the "SQL Scripts and Queries Document" to create tables and populate sample data.

2. **JDBC Connection Configuration:**
   - Open the `BookstoreDBMS.java` file in your preferred code editor.
   - Locate the following lines and replace the placeholder values with your actual database connection details:

    ```java
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/myadadata";
    private static final String USERNAME = "myadadata";
    private static final String PASSWORD = "7777";
    ```

3. **Compile the Application:**
   - Open a terminal or command prompt.
   - Navigate to the directory containing the Java files.
   - Compile the Java files using the following command:

    ```bash
    javac *.java
    ```

4. **Run the Application:**
   - Run the compiled Java application using the following command:

    ```bash
    java BookstoreDBMS
    ```

