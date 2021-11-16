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

    //Define getters
    double getBalance(){
        return balance;
    }
    String getUserID(){
        return userID;
    }
    String getUserName(){
        return userName;
    }

    //Define setters

    //define constructors

    // private void addUser(){
    //     //
    // }

    //removeUser


}

class Split {
    //define appropriate variables
    // define two constructors: one for equal, and other for exact and percent

}

class TransactionManager {

    //store all balances
    static HashMap<String,HashMap<String,Double>> balances; 

    // addTransaction(Split) //update "balances"
}

class CSVReader{
    // import the csv file having the balances

    //static method to read CSV and create the balances hashmap
    
}

class CSVWriter{
    //CSVWriter

    //update CSV file (addition and deletion happens in hashmap)

}