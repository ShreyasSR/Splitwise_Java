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
    HashMap<String,Double> userSplit;

    Split(String paidUserID, double amount, ArrayList<String> owedUsers, String type, ArrayList<Double> values){
        if(type.equalsIgnoreCase("exact")){
            for(int i=0;i<owedUsers.size();i++){
                userSplit.put(owedUsers.get(i),values.get(i));
            }
        }
        else if(type.equalsIgnoreCase("percent")){
             for(int i=0;i<owedUsers.size();i++){
                userSplit.put(owedUsers.get(i),amount*values.get(i)/100);
            }

        }
        else if(type.equalsIgnoreCase("equal")){  
            for(int i=0;i<owedUsers.size();i++){
                userSplit.put(owedUsers.get(i),amount/owedUsers.size());
            }
        }
        else {
            System.out.println("Invalid split type, no changes made !");
        }
    }

}

class TransactionManager {

    //store all balances
    static HashMap<String,HashMap<String,Double>> balances;



    // addTransaction(Split) //update "balances"

    // add function to settle expenses
}

class CSVReader{
    // import the csv file having the balances

    //static method to read CSV and create the balances hashmap
    
}

class CSVWriter{
    //CSVWriter

    //update CSV file (addition and deletion happens in hashmap)

}