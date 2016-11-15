import java.util.Random;

/**
 * A substitution cipher.
 */
public class SubstitutionCipher {

    private final String mCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890()-=,.\'?{}[]_+<>!@#$%^&*:;\" ";
    private String[] mPermutations = null;
    private int mTotalSchemes = -1;


    /**
     * Constructor.
     * @param totalNumSchemes The total number of security schemes.
     */
    public SubstitutionCipher(int totalNumSchemes) {
        mTotalSchemes = totalNumSchemes;
        mPermutations = generatePermutations(mCharset, mTotalSchemes, 5);
    }

    /**
     * Encrypts the given plaintext input.
     * @param plaintext Plaintext to encrypt using substitution.
     * @return Ciphertext.
     */
    public String encrypt(String plaintext, int scheme) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < plaintext.length(); i++) {
            char plainChar = plaintext.charAt(i);

            if(plainChar == '~') {
                System.out.println("Illegal character '~'\nExiting.");
                System.exit(-1);
            }

            int indexOfPlainChar = mCharset.indexOf(plainChar);
            sb.append(mPermutations[scheme].charAt(indexOfPlainChar));
        }

        return sb.toString();
    }

    /**
     * Decrypts the given ciphertext.
     * @param ciphertext Ciphertext to decrypt.
     * @return Plaintext.
     */
    public String decrypt(String ciphertext, int scheme) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < ciphertext.length(); i++) {
            char cipherChar = ciphertext.charAt(i);
            //System.out.println("Decrypt: cipher char is " + cipherChar);
            int indexOfCipherChar = mPermutations[scheme].indexOf(cipherChar);
            sb.append(mCharset.charAt(indexOfCipherChar));
        }

        return sb.toString();
    }


    /**
     * Generates permutations for a given String.
     * @param input String to generate permutations for.
     * @param numPerms The number of permutations to generate.
     * @param seed A seed to control the output.
     * @return A String array containing the resulting permutations.
     */
    private String[] generatePermutations(String input, int numPerms, long seed) {

        int setSize = input.length();
        Random random = new Random(seed);
        int[] charMap = new int[setSize];
        String[] permutations = new String[numPerms];

        //initialize charMap
        for(int i = 0; i < setSize; i++) {
            charMap[i] = i; //an array of integers from 0 to setSize-1, each int is mapped to a character in the character set
        }

        //loop for whole permutation set
        for(int i = 0; i < numPerms; i++) {

            //loop for individual permutation
            for(int j = setSize-1; j > 0; j--) {

                //randomize the charMap by shuffling the elements
                int randomIndex = random.nextInt(j + 1);
                int a = charMap[randomIndex];
                charMap[randomIndex] = charMap[j];
                charMap[j] = a;

                StringBuilder sb = new StringBuilder();

                //map characters from charset according to charMap
                for (int k = 0; k < charMap.length; k++) {
                    sb.append(mCharset.charAt(charMap[k]));
                }

                //add the result to the permutation set
                permutations[i] = sb.toString();
            }


            /*
            for (int charIndex : charMap) {
                System.out.print(charIndex + " ");
            }
            System.out.println();
            */

        }

        /*
        for (String s : permutations) {
            System.out.println(s);
        }
        */


        return permutations;
    }

    /**
     * Diagnostic method. Prints all the generated permutations of the character set.
     */
    public void printPermutations() {

        System.out.println("Generated permutations of input set:");
        for (int i = 0; i < mPermutations.length; i++) {
            String out = String.format("%1d.\t%2s", i, mPermutations[i]);
            System.out.println(out);
        }

    }

}
