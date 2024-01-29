package org.example;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ATM {

    static final String USERID = "root@localhost";
    static final String PIN = "password";
    private Connection connection;
    private User currentUser;

    public ATM() {
        this.currentUser = null;
        initializeDatabase();
    }

    public void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", USERID, PIN);
            String createUserTableQuery = "CREATE TABLE IF NOT EXISTS users (userId VARCHAR(255) PRIMARY KEY, pin VARCHAR(4), balance DOUBLE)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createUserTableQuery)) {
                preparedStatement.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter User ID: ");
        String userId = scanner.nextLine();

        System.out.println("Enter pin: ");
        String pin = scanner.nextLine();

        if (authenticateUser(userId, pin)) {
            displayMenu();
        } else {
            System.out.println("Invalid credentials. Exiting....");
        }
    }

    public boolean authenticateUser(String userId, String pin) {
        try {
            String selectQuery = "SELECT * FROM users WHERE userId = ? AND pin = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, pin);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    currentUser = new User(userId, pin);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nATM Menu: ");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");

            System.out.println("Enter your choice: ");
            int choice = scanner.nextInt();

            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayTransactionHistory();
                    break;
                case 2:
                    makeWithdrawal();
                    break;
                case 3:
                    makeDeposit();
                    break;
                case 4:
                    makeTransfer();
                    break;
                case 5:
                    System.out.println("Exiting....");
                    closeDatabaseConnection();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void displayTransactionHistory() {
        if (currentUser != null) {
            System.out.println("\nTransaction History: ");
            for (Transaction transaction : currentUser.getTransactionHistory()) {
                System.out.println("Date: " + transaction.getDate() + ", amount: " + transaction.getAmount());
            }
        }
    }

    private void makeWithdrawal() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        currentUser.withdraw(amount);
        updateBalanceInDatabase();
    }

    private void makeDeposit() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        currentUser.deposit(amount);
    }

    private void makeTransfer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter recipient's User ID: ");
        String recipientId = scanner.nextLine();

        try {
            String selectQuery = "SELECT * FROM users WHERE userId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, recipientId);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    User recepient = new User(resultSet.getString("userId"), resultSet.getString("pin"));

                    System.out.println("Enter transfer amount: ");
                    double amount = scanner.nextDouble();
                    currentUser.transfer(recepient, amount);
                    updateBalanceInDatabase();
                } else {
                    System.out.println("Recipient not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void updateBalanceInDatabase() {
        try {
            String updateBalanceQuery = "UPDATE users SET balance = ? WHERE userId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceQuery)) {
                preparedStatement.setDouble(1, currentUser.getBalance());
                preparedStatement.setString(2, currentUser.getUserId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeDatabaseConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

