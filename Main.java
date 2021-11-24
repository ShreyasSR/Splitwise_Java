import java.io.*;
import java.util.*;

class Main {
    public static void main(String args[]) throws IOException {
        System.out.println("Hello");

        CSVReader.readCSV("balances.csv");

        System.out.println(TransactionManager.balances.get("UserA").get("UserC"));

        for (User user : User.getUsersList()) {
            System.out.println("UserID : " + user.getUserID() + " ; UserName : " + user.getUserName());
        }

        CSVWriter.writeCSV("balances.csv");
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
        return balance;
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

    static void newUser(String userName) {
        String userID = generateUserID();
        User userObj = new User(userID);
        userObj.setBalance(0.0);
        userObj.setUserName(userName);
        usersList.add(userObj);

        TransactionManager.newUser(userID);
        CSVWriter.writeCSV("Balances.csv");
    }

    // define constructors

    private void addUser() {
        usersList.add(this);
    }

    // removeUser

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
        if (type.equalsIgnoreCase("exact")) {
            for (int i = 0; i < owedUsers.size(); i++) {
                userSplit.put(owedUsers.get(i), -values.get(i));
            }
        } else if (type.equalsIgnoreCase("percent")) {
            for (int i = 0; i < owedUsers.size(); i++) {
                userSplit.put(owedUsers.get(i), -amount * values.get(i) / 100);
            }

        } else if (type.equalsIgnoreCase("equal")) {
            for (int i = 0; i < owedUsers.size(); i++) {
                userSplit.put(owedUsers.get(i), -amount / owedUsers.size());
            }
        } else {
            System.out.println("Invalid split type, no changes made !");
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
            double newOwedAmount = balances.get(owedUserID).get(paidUserID) - owedAmount;
            double newLentAmount = -newOwedAmount;

            balances.get(owedUserID).put(paidUserID, newOwedAmount);
            balances.get(paidUserID).put(owedUserID, newLentAmount);
        }
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
                fileWriter.printf(",%f", balance.getValue());
            }

            fileWriter.printf("\n");
        }

        fileWriter.close();
    }
}