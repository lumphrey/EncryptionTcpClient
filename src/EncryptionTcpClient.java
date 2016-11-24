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

    private static SubstitutionCipher mSubCipher;
    private static TranspositionCipher mTransCipher;
    private static int mTotalSchemes; //total number of security schemes ("N" as defined by the assignment)
    private static int mScheme; //the scheme to use

    //for use in Diffie-Hellman key exchange
    private static int mG = 5; //base
    private static int mP = 23; //modulus, must be prime
    private static int mLocalSecret = 9; //TODO: randomize
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

        initializeCiphers();
        mScheme = mSecretKey % mTotalSchemes; //calculate which scheme to use

        System.out.println("Using security scheme " + mScheme + "\n\n\n");

        Scanner scanner = new Scanner(System.in);
        String userInput;

        //user input loop
        while(true) {

            System.out.print("Enter message to encrypt and send to server: ");
            userInput = scanner.nextLine();
            sendEncryptedMessage(userInput);

            if(userInput.equals("quit")) {
                //TODO send quit message to server
                break;
            }

        }


    }


    /**
     * Instantiates the ciphers.
     */
    private static void initializeCiphers() {
        mSubCipher = new SubstitutionCipher(mTotalSchemes);
        mTransCipher = new TranspositionCipher();
        if(mDebug) {
            mSubCipher.printPermutations();
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

    /**
     * Encrypts the given String using 2-stage (substitution, transposition) encryption before sending it to the server.
     * @param msg String to encrypt.
     */
    private static void sendEncryptedMessage(String msg) {
        String subCipherText;
        String transCipherText;

        subCipherText = mSubCipher.encrypt(msg, mScheme);
        System.out.println("Substitution cipher generated: " + subCipherText + "\nApplying transposition...");

        transCipherText = mTransCipher.encrypt(subCipherText, mScheme);

        System.out.println("Final ciphertext generated: " + transCipherText);

        try {
            mToServer.writeBytes(transCipherText + "\n");
            System.out.println("Sent to server.");
        }
        catch (IOException e) {
            System.out.println("Error sending encrypted message to server.");
            e.printStackTrace();
            System.exit(-1);
        }

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
