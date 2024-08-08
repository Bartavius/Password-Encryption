import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

//===================================================== DATA COLLECTION ======================================================================================

        // set-up to take user information
        JTextField first_name = new JTextField();
        JTextField last_name = new JTextField();
        JTextField year = new JTextField();
        JTextField quote = new JTextField();
        JTextField phone = new JTextField();

        // creating window to take user information
        Object[] userInput = {"First Name:", first_name, "Last Name:", last_name, "Year of Graduation (YYYY):", year,
                "Your Favorite Quote / Message (Do not include punctuation):", quote, "Your phone number:", phone};
        int submit = JOptionPane.showConfirmDialog(null, userInput, "Log-in Registration",
                JOptionPane.OK_CANCEL_OPTION);

        // checking input validity
        if (first_name.getText().isEmpty() || last_name.getText().isEmpty() || year.getText().isEmpty() || quote.getText().isEmpty() ||
                phone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all the boxes.");
            System.exit(0);
        } else if (year.getText().length() != 4) {
            JOptionPane.showMessageDialog(null,"Please enter in a 4-digit year.");
            System.exit(0);
        } else if (phone.getText().length() < 10) {
            JOptionPane.showMessageDialog(null,"Your phone number must be at least 10-digits.");
            System.exit(0);
        }

        // creating email
        String email = new String(first_name.getText().toLowerCase().charAt(0) + last_name.getText().toLowerCase()
        + year.getText().substring(2,4) + "@taboracademy.org");

        // creating password
        String password = new String();
        password += quote.getText().charAt(0);
        // getting first letter of each word in quote, then add last 4 digit of phone number
        for (int i = 0; i < quote.getText().length(); ++i) {
            if (quote.getText().charAt(i) == ' ' && i+1 < quote.getText().length()) {
                password += quote.getText().charAt(i+1);
            }
        } password += phone.getText().substring(phone.getText().length()-4,phone.getText().length());

        // creating encryption key
        Random randint = new Random();
        String key = Integer.toString(randint.nextInt(1000000,999999999));


//===================================================== LOG-IN ==============================================================================================

        if (submit == JOptionPane.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null,"You have chosen to not register your account.");
            System.exit(0);
        }

        // building a new display box to return newly generated values
        // display log-in information and prompt user for message;
        JTextField original_msg = new JTextField();

        String userInfo = "Email:\n" + email + "\n\nPassword:\n" + password + "\n\nEncryption/Decryption Key:\n" + key + "\n";
        Object[] secure_msg = {"Your account has been created! Below is your log-in information:\n\n" + userInfo
                , "\nEnter in a pleasant message you would like to secure:", original_msg};
        int submit_msg = JOptionPane.showConfirmDialog(null, secure_msg, "Registration Successful!",
                JOptionPane.OK_CANCEL_OPTION);

        // run encryption
        int maxX = 1000;
        int maxY = 1000;
        String msg = caesar(original_msg.getText(), Integer.parseInt(key));
        File encrypt_file = encrypt(msg, key, maxX, maxY);

        // Log-in
        JTextField user_email = new JTextField();
        JTextField user_password = new JTextField();
        JTextField user_key = new JTextField();
        Object[] login = {"Email Address:", user_email, "Password:", user_password, "Encryption/Decryption Key:", user_key};
        int log = JOptionPane.showConfirmDialog(null,login,"Log-in",JOptionPane.OK_CANCEL_OPTION);

        // checking whether or not credentials is right
        boolean login_status = false;
        while (login_status == false) {

            if (user_email.getText().equalsIgnoreCase(email) && user_password.getText().equals(password)) {
                login_status = true;
            }
            else if (log == JOptionPane.CANCEL_OPTION) {
                System.exit(0);
            }
            else {
                Object[] loginFail = {"Incorrect email or password!\n\nEmail Address:", user_email, "Password:", user_password,
                        "Encryption/Decryption Key:", user_key};
                log = JOptionPane.showConfirmDialog(null, loginFail, "Log-in", JOptionPane.OK_CANCEL_OPTION);
            }
        }
        key = user_key.getText();
        // if correct credentials, begin decrypting
        msg = caesar(decrypt(encrypt_file, key, maxX, maxY),Integer.parseInt(key)*-1);
        JOptionPane.showMessageDialog(null,"Your message is: " + msg);
    }

//===================================================== CAESAR ===========================================================================================

    // This is Caesar Cipher but with everything on a regular keyboard; doesn't keep capitalization
    static String caesar(String msg, int key) {
        String word = new String();
        for (int i = 0; i < msg.length(); i++) {
            int ord = msg.charAt(i);
            int ecrkey = key % 95; // simplifying key

            if (ord >= 32 && 126 >= ord) {

                // If it needs to reset to the beginning (positive key)
                if (ord + ecrkey > 126) {
                    ord = (ord + ecrkey) % 126 + 31;
                }
                // If it needs to reset to the end (negative key)
                else if (ord + ecrkey < 32) {
                    ord += 95 + ecrkey;
                }
                // If it doesn't need to reset
                else {
                    ord += ecrkey;
                }
            }
            // Adding characters into the new word, then printing out the new encoded message
            word += (char) ord;
        }
        return word;
    }

//===================================================== ENCRYPTION ===========================================================================================

    // Encrypting ciphered message into file - sequence is basically each number in key signifies different
    // math equations and places each letter in a message at those indexes
    static File encrypt(String msg, String key, int maxX, int maxY) {

        String ecrfile = "encryption.txt";
        File encrypt_file = new File(ecrfile);
        Random randint = new Random();

        List<Integer> indexes = new ArrayList<>();
        indexes.add((int) Math.floorDiv(Integer.parseInt(key),1067)); // creating a pseudo random start

        // applying equations to the corresponding number of encryption key
        for (int q = 0; q < msg.length()-1; q++) {
            int final_element = indexes.get(indexes.size() - 1);
            int in = Character.getNumericValue(key.charAt(q % key.length()));
            switch (in) {
                case 1:
                    indexes.add((final_element * 57 + 754) % (maxX*maxY));
                    break;
                case 2:
                    indexes.add((final_element + 5599) % (maxX*maxY));
                    break;
                case 3:
                    indexes.add((int) Math.floorDiv(final_element,151) % (maxX*maxY));
                    break;
                case 4:
                    indexes.add((final_element * 327 - 53) % (maxX*maxY));
                    break;
                case 5:
                    indexes.add((int) Math.floorDiv(final_element,33) * 2 % (maxX*maxY));
                    break;
                case 6:
                    indexes.add((final_element + 1576) % (maxX*maxY));
                    break;
                case 7:
                    indexes.add(final_element * 172 % (maxX*maxY));
                    break;
                case 8:
                    indexes.add(((int) Math.floorDiv(final_element,171) + 543) % (maxX*maxY));
                    break;
                case 9:
                    indexes.add((final_element + 3532) % (maxX*maxY));
                    break;
                case 0:
                    indexes.add(final_element * 341 % (maxX*maxY));
                    break;
                default:
                    System.out.println("An error occurred.");
            }
        }
        // check for duplicates, if there's any then +69 the index
        for (int i = 0; i < indexes.size(); i++) {
            for (int f = 0; f < indexes.size(); f++) {
                if (i != f && indexes.get(i).equals(indexes.get(f))) {
                    indexes.set(i, (indexes.get(i) + 69) % (maxX*maxY));
                    f = -1;
                }
            }
        }
        Collections.sort(indexes);

        // actually writing into the file once it receives the indexes
        int currentIndex = 0;
        int currentChar = 0;
        int currentArray = 0;

        try {
            // quickly writes gibberish on the first line to indicate how long the message is
            FileWriter editor = new FileWriter(ecrfile);
            for (int i = 0; i < msg.length() * 15; i++) {
                editor.write((char)randint.nextInt(32,127));
            } editor.write(msg.length() + "\n");

            // filling the IOFile with gibberish but also checking for certain indexes
            for (int y = 0; y < maxY; y++) {
                for (int x = 0; x < maxX; x++) {
                    if (currentArray < msg.length() && currentIndex == indexes.get(currentArray)) {
                        editor.write(msg.charAt(currentChar));
                        currentArray += 1;
                        currentChar += 1;
                    }
                    else {
                        editor.write((char) randint.nextInt(32,127));
                    } currentIndex += 1;
                } editor.write("\n");
            } editor.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return encrypt_file;
    }

//===================================================== DECRYPTION ===========================================================================================
    // basically replicating the encryption, but just reading
    static String decrypt(File encrypt_file,String key, int maxX, int maxY) {

        int currentArray = 0;
        int currentY = 0;

        try {
            Scanner reader = new Scanner(encrypt_file);
            Scanner nLine = new Scanner(reader.nextLine());
            String line = nLine.nextLine();

            // read first line to see length of message
            int msgLength = line.length() / 15;
            String dcrMessage = new String();

            // copy-paste from encrypt to search for the same indexes and store them in an array list
            List<Integer> indexes = new ArrayList<>();
            indexes.add((int) Math.floorDiv(Integer.parseInt(key),1067));

            for (int q = 0; q < msgLength-1; q++) {
                int final_element = indexes.get(indexes.size() - 1);
                int in = Character.getNumericValue(key.charAt(q % key.length()));
                switch (in) {
                    case 1:
                        indexes.add((final_element * 57 + 754) % (maxX*maxY));
                        break;
                    case 2:
                        indexes.add((final_element + 5599) % (maxX*maxY));
                        break;
                    case 3:
                        indexes.add((int) Math.floorDiv(final_element,151) % (maxX*maxY));
                        break;
                    case 4:
                        indexes.add((final_element * 327 - 53) % (maxX*maxY));
                        break;
                    case 5:
                        indexes.add((int) Math.floorDiv(final_element,33) * 2 % (maxX*maxY));
                        break;
                    case 6:
                        indexes.add((final_element + 1576) % (maxX*maxY));
                        break;
                    case 7:
                        indexes.add(final_element * 172 % (maxX*maxY));
                        break;
                    case 8:
                        indexes.add(((int) Math.floorDiv(final_element,171) + 543) % (maxX*maxY));
                        break;
                    case 9:
                        indexes.add((final_element + 3532) % (maxX*maxY));
                        break;
                    case 0:
                        indexes.add(final_element * 341 % (maxX*maxY));
                        break;
                }
            }
            // check for duplicates, if there's any then +69 the index
            for (int i = 0; i < indexes.size(); i++) {
                for (int f = 0; f < indexes.size(); f++) {
                    if (i != f && indexes.get(i).equals(indexes.get(f))) {
                        indexes.set(i, (indexes.get(i) + 69) % (maxX*maxY));
                        f = -1;
                    }
                }
            }
            Collections.sort(indexes);

            // reading the IOFile, checking if the current "Y-coordinate" has any indexes
            while (reader.hasNextLine()) {
                if (currentArray < indexes.size()) {
                    nLine = new Scanner(reader.nextLine());
                    line = nLine.nextLine();
                    while (currentY == (int) Math.floorDiv(indexes.get(currentArray), maxX)) {
                        dcrMessage += line.charAt(indexes.get(currentArray) % maxX);
                        currentArray += 1;
                        if (currentArray == indexes.size()) {
                            break;
                        }
                    } currentY += 1;
                }
                else {
                    break;
                }
            }
            return dcrMessage;
        }

        catch (IOException e) {
            e.printStackTrace();
            return "ERROR 404";
        }
    }
}

