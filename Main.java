import java.io.*;
import java.util.*;

class Main {
    public static void main(String args[]) throws IOException {
        System.out.println("Hello");

        CSVReader.readCSV();

        System.out.println(TransactionManager.balances.get("UserA").get("UserC"));
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
        CSVWriter.writeCSV('Balances.csv');
    }

    // define constructors

    private void addUser() {
        usersList.add(this);
    }

    // removeUser

}

class Split {

    // define appropriate variables
    private HashMap<String, Double> userSplit;
    private String paidUserID;

    HashMap<String, Double> getSplit() {
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
    static HashMap<String, HashMap<String, Double>> balances;

    // addTransaction(Split) //update "balances"

    static void setBalances(HashMap<String, HashMap<String, Double>> balances) {
        TransactionManager.balances = balances;
    }

    static void updateBalances(Split sp) {
        HashMap<String, Double> split = sp.getSplit();
        String paidUserID = sp.getPaidUserID();
        for (Map.Entry<String, Double> splitEntry : split.entrySet()) {
            // splitEntry is a HashMap received from Split with key owedUserID and value the
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
        balances.put(userID, new HashMap<String, Double>());
        for (String user : balances.keySet()) {
            balances.get(user).put(userID, 0.0);
            balances.get(userID).put(user, 0.0);
        }
    }
}

class CSVReader {
    static BufferedReader fileReader = null;

    private static void instantiateReader() {
        try {
            fileReader = new BufferedReader(new FileReader("./Balances.csv"));
        } catch (FileNotFoundException fileErr) {
            try {
                File balancesFile = new File("./Balances.csv");
                balancesFile.createNewFile();
                fileReader = new BufferedReader(new FileReader("./Balances.csv"));
            } catch (IOException IOErr) {
                IOErr.printStackTrace();
                // EDIT LATER
            }
        }
    }

    static void readCSV() throws IOException {
        // Checking if fileReader has been already instantiated or not
        if (fileReader == null) {
            instantiateReader();
        }

        HashMap<String, HashMap<String, Double>> balanceMap = new HashMap<>();

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

            HashMap<String, Double> personalBalanceHashMap = new HashMap<>();
            for (int i = 0; i < usersList.size(); ++i) {
                personalBalanceHashMap.put(usersList.get(i).getUserID(), Double.valueOf(lineContent[i + 2]));
            }

            balanceMap.put(userID, personalBalanceHashMap);
        }

        TransactionManager.setBalances(balanceMap);

        fileReader.close();
    }
}

class CSVWriter {

}