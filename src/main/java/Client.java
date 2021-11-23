import java.util.Scanner;

public class Client {
    String selfName, peerName, groupName;
    // gather user info & chat mode
    public int loginIn(){
        System.out.print("\n***Welcome to Chat Room!***\n" +
                         "Please select mode:\n" +
                         "1. One-to-One\n" +
                         "2. Group\n");
        boolean isValidMode = false;
        int mode = -1;
        Scanner scanner = new Scanner(System.in);
        while (!isValidMode){
            mode = scanner.nextInt();
            if(mode == 1 || mode == 2){
                isValidMode = true;
            } else {
                System.out.println("Error: Invalid Mode.\n" +
                                   "Please enter 1 or 2.");
            }
        }
        System.out.print("Please Enter your name: ");
        selfName = getName();
        if (mode == 1){
            System.out.print("Please Enter your peer's name: ");
            peerName = getName();
        }
        else if (mode == 2){
            System.out.print("Please Enter Group name: ");
            groupName = getName();
        }
        return mode;
    }

    /*Utility Function*/
    public static String getName(){
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        return name;
    }

    public static void main(String[] args) throws Exception {
        // creat client
        Client client = new Client();
        // gather user info & chat mode
        int mode = client.loginIn();
        // chat init
        Chat chat = new Chat(client.selfName, client.peerName, client.groupName, mode);
        // start chatting
        chat.startChat();
    }

}

