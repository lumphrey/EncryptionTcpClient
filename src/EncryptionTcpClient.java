import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 *
 */
public class EncryptionTcpClient {

    private static boolean mDebug = true;

    private static int mPort;
    private static InetAddress mDestAddr;

    private static int mTotalSchemes;

    //for use in Diffie-Hellman key exchange
    private static int mG = 5; //base
    private static int mP = 23; //modulus
    private static int mLocalSecret = 15; //TODO: randomize
    private static int mSecretKey;

    //socket
    private static Socket mSocket = null;

    //stream readers and writers
    private static BufferedReader mReader = null;
    private static DataOutputStream mToServer = null;

    public static void main(String[] args) throws Exception {

        extractArgs(args);
        connect();
        initializeStreams();

        System.out.println("Connection established with " + mDestAddr.toString());
        Thread.sleep(2000);

        //send "special number" to server
        int clientNum = (int) (Math.pow(mG, mLocalSecret) % mP);
        System.out.println("Sending integer " + clientNum + " to server for key calculation...");
        sendUnencryptedMessage(Integer.toString(clientNum) + "\n");


        //wait for "special number" from server
        int serverNum = -1;
        try {
            serverNum = Integer.parseInt(waitForUnencryptedMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Expected integer from server.");
            System.exit(-1);
        }
        System.out.println("Received integer " + serverNum + " from server. Calculating secret...");


        //calculate secret key
        mSecretKey = calculateSecret(serverNum);
        System.out.println("Calculated secret: " + mSecretKey);


        Scanner scanner = new Scanner(System.in);
        String userInput;

        while(true) {
            System.out.print("Enter message to encrypt and send to server: ");
            userInput = scanner.nextLine();

        }
    }


    /**
     * Extracts command line arguments and assigns them to global variables.
     * @param args Argument array.
     */
    private static void extractArgs(String[] args) {

        if(args.length < 3) {
            System.out.println("Incorrect number of parameters. Required: destination host, port, total number of security schemes.");
            System.exit(-1);
        }

        try {
            mDestAddr = InetAddress.getByName(args[0]);
        }
        catch (UnknownHostException e) {
            System.out.println("Unknown host.");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            mPort = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            System.out.println("Error parsing integer from first argument.");
            System.exit(-1);
        }

        try {
            mTotalSchemes = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e) {
            System.out.println("Error parsing integer from second argument.");
            System.exit(-1);
        }
    }


    /**
     * Connects to the server specified by the command line parameter.
     */
    private static void connect() {

        try {
            mSocket = new Socket(mDestAddr, mPort);
        }
        catch (IOException e) {
            System.out.println("Error connecting to " + mDestAddr.toString());
            e.printStackTrace();
            System.exit(-1);
        }

    }


    /**
     * Initializes IO streams.
     */
    private static void initializeStreams() {

        try {
            mToServer = new DataOutputStream(mSocket.getOutputStream());
        }
        catch (IOException e) {
            System.out.println("Error initializing output stream.");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        }
        catch (IOException e) {
            System.out.println("Error initializing input stream.");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Sends a String to the server.
     * @param msg String to send.
     */
    private static void sendUnencryptedMessage(String msg) {

        try {
            mToServer.writeBytes(msg);
            if(mDebug)
                System.out.println("Message " + msg + " sent to server.");
        }
        catch (IOException e) {
            System.out.println("Error writing message to server.");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private static void sendEncryptedMessage(String msg) {

    }

    private static String encrypt(String msg) {
        String encrypted = "";

        return encrypted;
    }


    /**
     * Blocks until a message is read from the input stream.
     * @return A String with no line-termination characters.
     */
    private static String waitForUnencryptedMessage() {
        String msg = null;
        while(true) {
            try {
                msg = mReader.readLine();
            }
            catch (IOException e) {
                System.out.println("Error reading input stream.");
                e.printStackTrace();
                System.exit(-1);
            }

            if(msg != null) {
                break;
            }
        }

        return msg;
    }

    /**
     * Calculates a secret key given a number from the server.
     * @param serverNum Provided by the server.
     * @return A secret key (integer).
     */
    private static int calculateSecret(int serverNum) {

        return (int) (Math.pow(serverNum, mLocalSecret) % mP);
    }
}
