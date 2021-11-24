import java.io.*;
import java.util.*;

class Main {
    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        CSVReader.readCSV("Balances.csv");

        // Menu
        Boolean exit = false;
        while (!exit) {

            // Clearing screen
            System.out.print("\033[H\033[2J");
            System.out.flush();

            System.out.println("Main Menu:");
            System.out.println("1. List Users");
            System.out.println("2. Add Users");
            System.out.println("3. Create Expense");
            System.out.println("4. Settle Balance");
            System.out.println("5. Show Balances");
            System.out.println("6. Remove Users");
            System.out.println("7. Exit Menu");

            int input;
            while (true) {
                try {
                    input = sc.nextInt();
                    break;
                } catch (InputMismatchException err) {
                    System.out.println("Please enter a valid option.");
                    sc.nextLine();
                }
            }

            switch (input) {
            case 1:
                System.out.printf("%-15s%-15s\n", "UserID", "Username");
                for (User user : User.getUsersList()) {
                    System.out.printf("%-15s%-15s\n", user.getUserID(), user.getUserName());
                }
                break;
            case 2:
                System.out.println("What is the name of the new user?");
                String newUserName = sc.next();
                String newUserID = User.newUser(newUserName);

                System.out.printf("New User - %s - created with User ID %s\n", newUserName, newUserID);
                break;
            case 3:
                System.out.print("Enter the user ID (of person who paid): ");
                String paidUserID = sc.next();
                System.out.print("Enter the amount: ");
                double amount = sc.nextDouble();
                ArrayList<String> owedUsersList = new ArrayList<>();
                System.out.print("Enter the number of users who will split: ");
                int n = sc.nextInt();
                System.out.println("Enter their user ID(s) (space separated)");
                for (int i = 0; i < n; i++) {
                    owedUsersList.add(sc.next());
                }
                String type = "";
                System.out.println("Enter the type of split:");
                System.out.println("\t1 for equal");
                System.out.println("\t2 for exact");
                System.out.println("\t3 for percentage");
                char ch = sc.next().charAt(0);
                ArrayList<Double> values = new ArrayList<Double>();

                switch (ch) {
                case '1':
                    type = "equal";
                    break;
                case '2':
                    double totAmount = 0.0;
                    System.out.println("Enter the amounts (space separated)");
                    for (int i = 0; i < n; i++) {
                        values.add(sc.nextDouble());
                        totAmount += values.get(i);
                    }
                    if (totAmount == amount) {
                        type = "exact";
                    } else {
                        System.out.println("Inconsistent amounts entered !");
                        type = "";
                    }
                    break;
                case '3':
                    System.out.println("Enter the amounts (space separated)");
                    double totPercent = 0.0;
                    for (int i = 0; i < n; i++) {
                        values.add(sc.nextDouble());
                        totPercent += values.get(i);
                    }
                    if (totPercent == 100.0) {
                        type = "percent";
                    } else {
                        System.out.println("Invalid percentages entered!");
                        type = "";
                    }
                    break;

                }

                Split sp = new Split(paidUserID, amount, owedUsersList, type, values);

                TransactionManager.updateBalances(sp);
                break;
            case 4:
                System.out.println("Enter the User IDs of the two people who are settling their balance:");

                String user1 = sc.next();
                String user2 = sc.next();

                if (TransactionManager.balances.containsKey(user1) && TransactionManager.balances.containsKey(user2)) {
                    TransactionManager.balances.get(user1).put(user2, 0.0);
                    TransactionManager.balances.get(user2).put(user1, 0.0);

                    // Updating the CSV File
                    CSVWriter.writeCSV("Balances.csv");
                } else {
                    System.out.println("Invalid User IDs.");
                }

                break;
            case 5:
                System.out.println("1. Show balances for a single user.");
                System.out.println("2. Show balances for all users.");

                int inputForCase5;
                while (true) {
                    try {
                        inputForCase5 = sc.nextInt();
                        break;
                    } catch (InputMismatchException err) {
                        System.out.println("Please enter a valid option.");
                    }
                }

                switch (inputForCase5) {
                case 1:
                    System.out.println("Enter the relevant User ID:");
                    String userID = sc.next();

                    if (TransactionManager.balances.containsKey(userID)) {
                        User reqUser = null;
                        for (User user : User.getUsersList()) {
                            if (user.getUserID().equals(userID)) {
                                reqUser = user;
                            }
                        }
                        reqUser.showAllBalances();
                    } else {
                        System.out.println("Invalid User ID.");
                    }
                    break;
                case 2:
                    System.out.printf("%-15s%-15s%-15s\n", "UserID", "Username", "Balance");
                    for (User user : User.getUsersList()) {
                        System.out.printf("%-15s%-15s%-15s\n", user.getUserID(), user.getUserName(), user.getBalance());
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
                }
                break;
            case 6:
                System.out.println("Enter the User ID of the user to be removed:");
                String userID = sc.next();

                if (TransactionManager.balances.containsKey(userID)) {
                    User usr = null;
                    for (User user : User.getUsersList()) {
                        if (user.getUserID().equals(userID)) {
                            usr = user;
                        }
                    }

                    System.out.printf("Are you sure you want to remove %s? (Y/N)\n", usr.getUserName());

                    String yesNo = sc.next();
                    if (yesNo.equalsIgnoreCase("y")) {
                        User.removeUser(userID);
                    } else if (yesNo.equalsIgnoreCase("n")) {
                        break;
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                break;
            case 7:
                exit = true;
                break;
            default:
                System.out.println("Invalid option.");
                break;

            }
            if (!exit) {
                System.out.println();
                System.out.println("Do you want to return to Main Menu? (Y/N)");

                String yesNo = sc.next();
                if (yesNo.equalsIgnoreCase("n")) {
                    exit = true;
                }
            }
        }

        sc.close();
        CSVWriter.writeCSV("Balances.csv");

    }
}

class User {
    private String userID;
    private String userName;
    private double balance;

    private static ArrayList<User> usersList;

    static {
        usersList = new ArrayList<>();
    }

    User(String userID) {
        this.userID = userID;
        this.addUser();
    }

    String getUserID() {
        return userID;
    }

    String getUserName() {
        return userName;
    }

    double getBalance() {
        balance = 0.0;
        for (String user : TransactionManager.balances.keySet()) {
            balance += TransactionManager.balances.get(userID).get(user);
        }
        return balance;
    }

    void showAllBalances() {
        for (String user : TransactionManager.balances.keySet()) {
            if (userID.equals(user)) {
                continue;
            }

            double balAmt = TransactionManager.balances.get(userID).get(user);
            if (balAmt > 0) {
                System.out.printf("User %s owes %s an amount of %f \n", user, userID, balAmt);
            } else if (balAmt < 0) {
                System.out.printf("User %s owes %s an amount of %f \n", userID, user, -balAmt);
            } else {
                System.out.printf("User %s and %s are settled up \n", userID, user);
            }

        }
    }

    static ArrayList<User> getUsersList() {
        return usersList;
    }

    void setUserID(String userID) {
        this.userID = userID;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    void setBalance(double balance) {
        this.balance = balance;
    }

    private static String generateUserID() {
        int lastExistingID = Integer.parseInt(usersList.get(usersList.size() - 1).getUserID());
        int newUserID = lastExistingID + 1;
        return String.format("%05d", newUserID);
    }

    static String newUser(String userName) {
        String userID = generateUserID();
        User userObj = new User(userID);
        userObj.setBalance(0.0);
        userObj.setUserName(userName);

        TransactionManager.newUser(userID);
        CSVWriter.writeCSV("Balances.csv");

        return userID;
    }

    private void addUser() {
        usersList.add(this);
    }

    static void removeUser(String userID) {
        // Removing the personal HashMap for the user
        TransactionManager.balances.remove(userID);

        // Removing the user from the Users List
        for (User user : usersList) {
            if (user.getUserID().equals(userID)) {
                usersList.remove(user);
                break;
            }
        }

        // Removing the entries for that user in others' HashMaps
        for (Map.Entry<String, LinkedHashMap<String, Double>> hashMapEntry : TransactionManager.balances.entrySet()) {
            hashMapEntry.getValue().remove(userID);
        }

        // Updating CSV File
        CSVWriter.writeCSV("Balances.csv");
    }

}

class Split {

    // define appropriate variables
    private LinkedHashMap<String, Double> userSplit;
    private String paidUserID;

    LinkedHashMap<String, Double> getSplit() {
        return userSplit;
    }

    String getPaidUserID() {
        return paidUserID;
    }

    Split(String paidUserID, double amount, ArrayList<String> owedUsers, String type, ArrayList<Double> values) {
        this.paidUserID = paidUserID;
        userSplit = new LinkedHashMap<>();
        if (type.equalsIgnoreCase("exact")) {
            for (int i = 0; i < owedUsers.size(); i++) {
                userSplit.put(owedUsers.get(i), values.get(i));
            }
        } else if (type.equalsIgnoreCase("percent")) {
            for (int i = 0; i < owedUsers.size(); i++) {
                userSplit.put(owedUsers.get(i), amount * values.get(i) / 100);
            }

        } else if (type.equalsIgnoreCase("equal")) {
            for (int i = 0; i < owedUsers.size(); i++) {
                userSplit.put(owedUsers.get(i), amount / owedUsers.size());
            }
        } else {
            System.out.println("Invalid split type, no changes made!");
        }
    }

}

class TransactionManager {

    // store all balances
    static LinkedHashMap<String, LinkedHashMap<String, Double>> balances;

    // addTransaction(Split) //update "balances"

    static void setBalances(LinkedHashMap<String, LinkedHashMap<String, Double>> balances) {
        TransactionManager.balances = balances;
    }

    static void updateBalances(Split sp) {
        HashMap<String, Double> split = sp.getSplit();
        String paidUserID = sp.getPaidUserID();
        for (Map.Entry<String, Double> splitEntry : split.entrySet()) {
            // splitEntry is a LinkedHashMap received from Split with key owedUserID and
            // value the
            // amount owed
            String owedUserID = splitEntry.getKey();
            double owedAmount = splitEntry.getValue();

            if (owedUserID.equals(paidUserID)) {
                continue;
            }

            double newOwedAmount = balances.get(owedUserID).get(paidUserID) - owedAmount;
            double newLentAmount = -newOwedAmount;

            balances.get(owedUserID).put(paidUserID, newOwedAmount);
            balances.get(paidUserID).put(owedUserID, newLentAmount);            
            
        }
        // Updating CSV
        CSVWriter.writeCSV("Balances.csv");
    }

    static void newUser(String userID) {
        balances.put(userID, new LinkedHashMap<String, Double>());
        for (String user : balances.keySet()) {
            balances.get(user).put(userID, 0.0);
            balances.get(userID).put(user, 0.0);
        }
    }
}

class CSVReader {
    static BufferedReader fileReader = null;

    private static void instantiateReader(String fname) {
        try {
            fileReader = new BufferedReader(new FileReader(fname));
        } catch (FileNotFoundException fileErr) {
            try {
                File balancesFile = new File(fname);
                balancesFile.createNewFile();
                fileReader = new BufferedReader(new FileReader(fname));
            } catch (IOException IOErr) {
                IOErr.printStackTrace();
                // EDIT LATER
            }
        }
    }

    static void readCSV(String fname) throws IOException {
        // Checking if fileReader has been already instantiated or not
        if (fileReader == null) {
            instantiateReader(fname);
        }

        LinkedHashMap<String, LinkedHashMap<String, Double>> balanceMap = new LinkedHashMap<>();

        // Read header line
        String line = fileReader.readLine();

        if (line == null) {
            return;
        } else {
            String header[] = line.split(",");
            for (int i = 2; i < header.length; ++i) {
                new User(header[i]);
            }
        }

        int index = 0;
        ArrayList<User> usersList = User.getUsersList();
        while ((line = fileReader.readLine()) != null) {
            // A row looks like this: <userID>,<userName>,<owedToUserA>,<owedToUserB>,...
            String lineContent[] = line.split(",");
            usersList.get(index).setUserName(lineContent[1]);
            String userID = lineContent[0];

            LinkedHashMap<String, Double> personalBalanceLinkedHashMap = new LinkedHashMap<>();
            for (int i = 0; i < usersList.size(); ++i) {
                personalBalanceLinkedHashMap.put(usersList.get(i).getUserID(), Double.valueOf(lineContent[i + 2]));
            }

            balanceMap.put(userID, personalBalanceLinkedHashMap);

            index++;
        }

        TransactionManager.setBalances(balanceMap);

        fileReader.close();
    }
}

class CSVWriter {
    static PrintWriter fileWriter = null;

    private static void instantiateWriter(String fname) {
        try {
            fileWriter = new PrintWriter(new FileWriter(fname));
        } catch (IOException err) {
            System.out.println("IOException encountered when writing to CSV File");
        }
    }

    static void writeCSV(String fname) {
        // Checking if fileWriter has been already instantiated or not
        if (fileWriter == null) {
            instantiateWriter(fname);
        }

        // Write the header to the CSV file
        Set<String> keys = TransactionManager.balances.keySet();
        fileWriter.printf(",");
        for (String key : keys) {
            fileWriter.printf(",%s", key);
        }
        fileWriter.printf("\n");
        // Write the rest of the content to the CSV file
        for (Map.Entry<String, LinkedHashMap<String, Double>> balanceEntry : TransactionManager.balances.entrySet()) {
            String userID = balanceEntry.getKey();
            String uname = null;
            fileWriter.printf("%s,", userID);

            for (User user : User.getUsersList()) {
                if (userID.equals(user.getUserID())) {
                    uname = user.getUserName();
                    break;
                }
            }
            fileWriter.printf("%s", uname);

            LinkedHashMap<String, Double> userBalanceMap = balanceEntry.getValue();
            for (Map.Entry<String, Double> balance : userBalanceMap.entrySet()) {
                double bal = balance.getValue();
                fileWriter.printf(",%f", bal);
            }

            fileWriter.printf("\n");
        }

        fileWriter.close();
    }
}