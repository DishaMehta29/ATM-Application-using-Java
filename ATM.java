import java.util.Scanner;

public class ATM {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Bank theBank = new Bank("Bank of Drausin");

        // add  a user, which also creates savings account

        User aUser = theBank.addUser("John", "Doe", "1234");

        // add a checking account 
        Account newAccount = new Account("Checking", aUser, theBank);
        aUser.addAccount(newAccount);
        theBank.addAccount(newAccount);

        User curUser;
        while(true)
        {
            // stay in log in prompt until successful login
            curUser = ATM.mainMenuPrompt(theBank, sc);
            // stay in main menu until user quits
            ATM.printUserMenu(curUser,sc);
        }
    }
    public static User mainMenuPrompt( Bank theBank, Scanner sc){
        String userID;
        String pin;
        User authUser;

        do {
            System.out.printf("\n\nWelcome to %s\n\n",theBank.getName());
            System.out.print("Enter user ID:");
            userID =sc.nextLine();
            System.out.print("Enter PIN: ");
            pin = sc.nextLine();

            // try to get the user object corresponding to the ID and pin combo
            authUser = theBank.userLogin(userID, pin);
            if (authUser == null){
                System.out.println("Incorrect user ID/pin combination." + "PLease try again");

            }
        }while(authUser == null); // continuing looping until successfull login

        return authUser;
    }
    public static void printUserMenu(User theUser, Scanner sc)
    {
        // print summary of user accounts
        theUser.printAccountsSummary();
        int choice;
        do {
            System.out.printf("Welcome %s, what would you like to do?\n", theUser.getFirstName());
            System.out.println("  1) Show account transaction history");
            System.out.println("  2) Withdrawal");
            System.out.println("  3) Deposit");
            System.out.println("  4) Transfer");
            System.out.println("  5) Quit");
            System.out.println();
            choice = sc.nextInt();

            if (choice < 1 || choice >5){
                System.out.println("Invalid Choice. Please choose 1-5");

            } 

            //process the choice

            switch (choice)
            {
                case 1:
                    ATM.showTransHistory(theUser, sc);
                    break;
                case 2:
                    ATM.withdrawlFunds(theUser, sc);
                    break;
                case 3: 
                    ATM.depositFunds(theUser, sc);
                    break;
                case 4: 
                    ATM.transferFunds(theUser, sc);
                    break;
                case 5:
                    // gobble up rest of previous input
                    sc.nextLine();
                    break;
                // redisplay this menu if user wants to quit    
            }
            if (choice !=5){
                ATM.printUserMenu(theUser, sc);
            }
        } while (choice < 1 || choice >5);

        
    }
    public static void showTransHistory(User theUser, Scanner sc){
        int theAcct;
        do {
            System.out.printf("Enter the number (1-%d) of the account\n"+"whose transactions you want to see: ", theUser.numAccounts());
            theAcct = sc.nextInt()-1;
            if(theAcct < 0 || theAcct >=theUser.numAccounts())
            {
                System.out.println("Invalid account. Please try again.");

            }
        }while(theAcct < 0 || theAcct >= theUser.numAccounts());

        // prints transaction history
        theUser.printAcctTransHistory(theAcct);
    }

    public static void transferFunds(User theUser, Scanner sc){
        int fromAcct;
        int toAcct;
        double amount;
        double acctBal;

        do{
            System.out.printf("Enter the number (1-%d) of the account\n"+"to transfer from: ", theUser.numAccounts());
            fromAcct = sc.nextInt()-1;
            if(fromAcct <0 || fromAcct >= theUser.numAccounts()){
                System.out.println("Invalid account. Please try again.");
            }
        }while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        // get the account to transfer to
        do{
            System.out.printf("Enter the number (1-%d) of the account\n"+"to transfer to: ", theUser.numAccounts());
            toAcct = sc.nextInt()-1;
            if(toAcct <0 || toAcct >= theUser.numAccounts()){
                System.out.println("Invalid account. Please try again.");
            }
        }while(toAcct < 0 || toAcct >= theUser.numAccounts());        

        // get the amount to tranfer
        do{
            System.out.printf("Enter the amount to transfer (max $%.02f): $",acctBal );
            amount = sc.nextDouble();
            if (amount < 0){
                System.out.println("Amount must be greater then 0");

            }
            else if(amount > acctBal)
            {
                System.out.printf("Amount must not be greater than\n"+"balance of $%.02f.\n", acctBal);

            }
        }while(amount <0 || amount >acctBal);

        // finally do the transfer
        //subtractiing money
        theUser.addAcctTransaction(fromAcct, -1*amount, String.format("Transfer to account %s",theUser.getAcctUUID(toAcct)));
        //adding money
        theUser.addAcctTransaction(toAcct, amount, String.format("Transfer to account %s",theUser.getAcctUUID(fromAcct)));

    }

    public static void withdrawlFunds(User theUser, Scanner sc){
        int fromAcct;
        double amount;
        double acctBal;
        String memo;

        do{
            System.out.printf("Enter the number (1-%d) of the account\n"+"to withdraw from: ", theUser.numAccounts());
            fromAcct = sc.nextInt()-1;
            if(fromAcct <0 || fromAcct >= theUser.numAccounts()){
                System.out.println("Invalid account. Please try again.");
            }
        }while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);
        // get the amount to tranfer
        do{
            System.out.printf("Enter the amount to withdraw (max $%.02f): $",acctBal );
            amount = sc.nextDouble();
            if (amount < 0){
                System.out.println("Amount must be greater then 0");

            }
            else if(amount > acctBal)
            {
                System.out.printf("Amount must not be greater than\n"+"balance of $%.02f.\n", acctBal);

            }
        }while(amount <0 || amount >acctBal);

        // gobble up rest of previous input
        sc.nextLine();

        //get a memo
        System.out.print("Enter a memo: ");
        memo = sc.nextLine();

        // do the withdrawl
        theUser.addAcctTransaction(fromAcct, -1*amount, memo);
    }

    public static void depositFunds(User theUser, Scanner sc)
    {
        int toAcct;
        double amount;
        double acctBal;
        String memo;

        do{
            System.out.printf("Enter the number (1-%d) of the account\n"+"to deposit in: ", theUser.numAccounts());
            toAcct = sc.nextInt()-1;
            if(toAcct <0 || toAcct >= theUser.numAccounts()){
                System.out.println("Invalid account. Please try again.");
            }
        }while(toAcct < 0 || toAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(toAcct);
        // get the amount to tranfer
        do{
            System.out.printf("Enter the amount to transfer (max $%.02f): $",acctBal );
            amount = sc.nextDouble();
            if (amount < 0){
                System.out.println("Amount must be greater then 0");

            }
        }while(amount <0);

        // gobble up rest of previous input
        sc.nextLine();

        //get a memo
        System.out.print("Enter a memo: ");
        memo = sc.nextLine();

        // do the withdrawl
        theUser.addAcctTransaction(toAcct, amount, memo);
    }
}
