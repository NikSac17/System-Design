import java.util.*;

/*
Functional Requirement-

- Users should be able to withdraw money, check balance, print passbook.
- Receipt of operation performed
- Admin should be able to add money

*/

enum Operation{
    WithdrawMoney, CheckBalance, PrintPassBook
}

class BankAccount{
    String accountNo;
    double balance;

    public BankAccount(String accountNo, double balance){
        this.accountNo=accountNo;
        this.balance=balance;
    }

    void addBalance(double balance){
        this.balance+=balance;
    }

    boolean withDrawBalance(double balance){
        if(this.balance<balance){
            System.out.println("Low Balance...");
            return false;
        }
        else{
            System.out.println("Available Balance is : "+this.balance);
            this.balance-=balance;
        }
        return true;
    }
}

class Card{
    String cardNo;
    String cvv;
    String userName;
    String pin;
    Date expiryDate;

    public Card(String cardNo, String cvv, String userName, String pin){
        this.cardNo=cardNo;
        this.cvv=cvv;
        this.userName=userName;
        this.pin=pin;
    }

    boolean validatePin(String pin){
        return this.pin.equals(pin);
    }
}

class User{
    String userName;
    BankAccount account;
    Card card;

    public User(String userName, BankAccount account, Card card){
        this.userName=userName;
        this.account=account;
        this.card=card;
    }
}

class ATM{
    static ATM instance;
    Map<Integer,Integer> enumeration=new HashMap<>();
    State currState;
    State idleState;
    State hasCardInsertedState;
    State selectOperationState;
    State dispenseState;
    State depositState;
    double balance;

    ATM(){
        this.currState=new IdleState();
        this.balance=10000000;
    }

    static ATM getInstance(){
        if(instance==null) instance=new ATM();
        return instance;
    }

    State getCurrState(){
        return currState;
    }

    double getATMBalance(){
        return balance;
    }

    void setState(State state){
        this.currState=state;
    }
}

interface State{
    void insertCard(User user);
    void authenticateUser(User user, String pin);
    void performOperation(User user, Operation operation, double money);
    void ejectCard(User user);
}

class IdleState implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card inserting...");
        ATM.getInstance().setState(new HasCardInsertedState());
        System.out.println("Card inserted...");
    }

    @Override
    public void authenticateUser(User user, String pin){
        System.out.println("Please insert card first");
    }

    @Override
    public void performOperation(User user, Operation operation, double money){
        System.out.println("Please insert card first");
    }

    @Override
    public void ejectCard(User user){
        System.out.println("Please insert card first");
    }
}

class HasCardInsertedState implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card Already Inserted");
    }

    @Override
    public void authenticateUser(User user, String pin){
        if(user.card.validatePin(pin)){
            System.out.println("Please select kind of operation");
            System.out.println(Operation.PrintPassBook);
            System.out.println(Operation.CheckBalance);
            System.out.println(Operation.WithdrawMoney);

            ATM.getInstance().setState(new SelectOperationState());
        }
        else{
            System.out.println("Wrong Pin Entered");
        }
    }

    @Override
    public void performOperation(User user, Operation operation, double money){
        System.out.println("Authenticate First");
    }

    @Override
    public void ejectCard(User user){
        System.out.println("Card Ejecting");
        ATM.getInstance().setState(new IdleState());
    }
}

class SelectOperationState implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card already inserted");
    }

    @Override
    public void authenticateUser(User user, String pin){
        System.out.println("User already authenticated");
    }

    @Override
    public void performOperation(User user, Operation operation, double money){
        switch (operation){
            case CheckBalance: {
                System.out.println("Money in "+user.userName+" account is : "+user.account.balance);
                return;
            }
            case WithdrawMoney:{
                ATM.getInstance().setState(new DispenseState());
                ATM.getInstance().getCurrState().performOperation(user,operation,money);
                return;
            }
            case PrintPassBook:{
                ATM.getInstance().setState(new PrintPassBook());
                ATM.getInstance().getCurrState().performOperation(user,operation,money);
                return;
            }
            default: throw new IllegalArgumentException("Invalid Operation");
        }
    }

    @Override
    public void ejectCard(User user){
        System.out.println("Card Ejecting");
        ATM.getInstance().setState(new IdleState());
    }
}

class DispenseState implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card already inserted");
    }

    @Override
    public void authenticateUser(User user, String pin){
        System.out.println("User already authenticated");
    }

    @Override
    public void performOperation(User user, Operation operation, double money){
        if(ATM.getInstance().getATMBalance()>=money && user.account.withDrawBalance(money)){
            System.out.println("Remaining money left: "+user.account.balance);
        }
        else{
            System.out.println("Not enough money");
        }
    }

    @Override
    public void ejectCard(User user){
        System.out.println("Card Ejecting");
        ATM.getInstance().setState(new IdleState());
    }
}

class PrintPassBook implements State{
    @Override
    public void insertCard(User user){
        System.out.println("Card already inserted");
    }

    @Override
    public void authenticateUser(User user, String pin){
        System.out.println("User already authenticated");
    }

    @Override
    public void performOperation(User user, Operation operation, double money){
        System.out.println("Printing Passbook...");
        System.out.println(user.userName+" : "+user.account.balance);
    }

    @Override
    public void ejectCard(User user){
        System.out.println("Card Ejecting");
        ATM.getInstance().setState(new IdleState());
    }
}

class ATMService{
    ATM atm;

    public ATMService(){
        atm=ATM.getInstance();
    }

    void insertCard(User user){
        ATM.getInstance().getCurrState().insertCard(user);
    }

    void authenticateUser(User user, String pin){
        ATM.getInstance().getCurrState().authenticateUser(user,pin);
    }

    void performOperation(User user, Operation operation, double money){
        ATM.getInstance().getCurrState().performOperation(user,operation,money);
    }

    void ejectCard(User user){
        ATM.getInstance().getCurrState().ejectCard(user);
    }
}

public class Main {
    public static void main(String[] args) {

        BankAccount bankAccount1=new BankAccount(UUID.randomUUID().toString(),23456);
        Card card1=new Card(UUID.randomUUID().toString(),"1234","Mukul","567");
        User u1=new User("Mukul",bankAccount1,card1);

        ATMService service=new ATMService();
        service.insertCard(u1);
        service.authenticateUser(u1,"567");
        service.performOperation(u1,Operation.WithdrawMoney,345);
        service.ejectCard(u1);
        service.authenticateUser(u1,"313");
    }
}