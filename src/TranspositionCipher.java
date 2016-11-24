import java.util.ArrayList;

/**
 *
 */
public class TranspositionCipher {


    private boolean mDebug = true;

    /**
     * Constructor.
     */
    public TranspositionCipher() {
    }


    @SuppressWarnings("unchecked")
    public String encrypt(String inputString, int scheme) {
        String cipher = "";
        int numColumns = (scheme % 5) + 1;

        numColumns = numColumns % inputString.length() + 1;

        ArrayList<String> rowArray = new ArrayList();

        for(int i = 0; i < inputString.length(); i = i + numColumns) {
            int endIndex = i + numColumns - 1;

            try {
                rowArray.add(inputString.substring(i, endIndex+1));
            }
            catch (IndexOutOfBoundsException e) {
                rowArray.add(inputString.substring(i));
            }
        }


        if(mDebug) {
            System.out.println("-----------------------------\nTransposition encryption array: ");
            for (String row : rowArray) {
                System.out.println(row);
            }
            System.out.println("-----------------------------");
        }

        for(int i = 0; i < numColumns; i++) {
            for (int j = 0; j < rowArray.size(); j++) {
                try {
                    cipher = cipher + rowArray.get(j).charAt(i);
                }
                catch (IndexOutOfBoundsException e) {
                    cipher = cipher + "~";
                }
            }
        }


        return cipher;
    }



    @SuppressWarnings("unchecked")
    public String decrypt(String cipher, int scheme) {

        int numColumns = (scheme % 5) + 1;

        numColumns = (numColumns % cipher.length()) + 1;

        String text = "";

        ArrayList<String> tempArray = new ArrayList();

        for(int i = 0; i < cipher.length(); i = i + numColumns) {
            int endIndex = i + numColumns - 1;

            try {
                tempArray.add(cipher.substring(i, endIndex+1));
            }
            catch (IndexOutOfBoundsException e) {
                tempArray.add(cipher.substring(i));
            }
        }


        int numRows = tempArray.size();
        ArrayList<String> rowArray = new ArrayList();

        //number of rows becomes number of columns, arrange ciphertext accordingly
        for(int i = 0; i < cipher.length(); i = i + numRows) {
            int endIndex = i + numRows - 1;

            try {
                rowArray.add(cipher.substring(i, endIndex+1));
            }
            catch (IndexOutOfBoundsException e) {
                rowArray.add(cipher.substring(i));
            }
        }


        if(mDebug) {
            System.out.println("------------------------------\nTransposition decryption array: ");
            for(String row : rowArray) {
                System.out.println(row);
            }
            System.out.println("------------------------------");
        }

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < rowArray.size(); j++) {

                try {
                    text = text + rowArray.get(j).charAt(i);
                }
                catch (IndexOutOfBoundsException e) {
                    text = text + "~";
                }

            }
        }

        return cleanText(text);

    }


    /**
     * Removes the placeholder "~" symbols from a String
     * @param s The String to clean.
     * @return A String with all "~" characters removed.
     */
    private static String cleanText(String s) {
        return s.replaceAll("~", "");
    }

}
