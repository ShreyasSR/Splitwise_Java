import java.io.*;
import java.util.*;

class Main {
    public static void main(String args[]) {
        System.out.println("Hello");
    }
}

class User {
    private String userID;
    private String userName;
    private double balance;

    static ArrayList<User> users;

    // Define getters
    double getBalance() {
        return balance;
    }

    String getUserID() {
        return userID;
    }

    String getUserName() {
        return userName;
    }

    // Define setters

    // define constructors

    // private void addUser(){
    // //
    // }

    // removeUser

}

class Split {

    //define appropriate variables
    private HashMap<String,Double> userSplit;
    private String paidUserID;

    HashMap<String,Double> getSplit(){
        return userSplit;
    }
    String getPaidUserID(){
        return paidUserID;
    }

    Split(String paidUserID, double amount, ArrayList<String> owedUsers, String type, ArrayList<Double> values){
        this.paidUserID = paidUserID;
        if(type.equalsIgnoreCase("exact")){
            for(int i=0;i<owedUsers.size();i++){
                userSplit.put(owedUsers.get(i),-values.get(i));
            }
        }
        else if(type.equalsIgnoreCase("percent")){
             for(int i=0;i<owedUsers.size();i++){
                userSplit.put(owedUsers.get(i),-amount*values.get(i)/100);
            }

        }
        else if(type.equalsIgnoreCase("equal")){  
            for(int i=0;i<owedUsers.size();i++){
                userSplit.put(owedUsers.get(i),-amount/owedUsers.size());
            }
        }
        else {
            System.out.println("Invalid split type, no changes made !");
        }
    } 

}

class TransactionManager {

    // store all balances
    private static HashMap<String, HashMap<String, Double>> balances;

    // addTransaction(Split) //update "balances"

    static void setBalances(HashMap<String, HashMap<String, Double>> balances) {
        TransactionManager.balances = balances;
    }

    void updateBalances(Split sp){
        HashMap<String,Double> split = sp.getSplit();
        String paidUserID = sp.getPaidUserID();
        for(Map.Entry<String, Double> splitEntry : split.entrySet()){
            // splitEntry is a HashMap received from Split with key owedUserID and value the amount owed
            String owedUserID = splitEntry.getKey();
            double owedAmount = splitEntry.getValue();
            double newOwedAmount = balances.get(owedUserID).get(paidUserID)-owedAmount;
            double newLentAmount = -newOwedAmount;
            
            balances.get(owedUserID).put(paidUserID, newOwedAmount);
            balances.get(paidUserID).put(paidUserID, newLentAmount);
        }
    }
}

class CSVReader {
    // import the csv file having the balances
    static BufferedReader fileReader = null;

    private static void instantiateReader() {
        try {
            fileReader = new BufferedReader(new FileReader("./Balances.csv"));
        } catch (FileNotFoundException fileErr) {
            try {
                File balancesFile = new File("./Balances.csv");
                balancesFile.createNewFile();
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
        }
    }

    // static method to read CSV and create the balances hashmap

}

class CSVWriter {
    // CSVWriter

    // update CSV file (addition and deletion happens in hashmap)

}