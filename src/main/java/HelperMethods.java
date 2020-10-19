import org.bson.Document;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelperMethods {

    public static final String letters = "abcdefghijklmnopqrstuvwxyz";
    public static final String digits = "0123456789";
    public static final String symbols = "!@#$%^&*";

    public static int PasswordStrength(String pass){
        int letter = 0, digit = 0, symbol = 0; // verify password contains each
        for (int i = 0; i < pass.length(); i++) {
            if (in(pass.charAt(i), letters) && letter == 0){ // assure password contains a letter
                letter = 1;
            }else if (in(pass.charAt(i), symbols) && symbol == 0){ // assure password contains a symbol
                symbol = 1;
            }else if (in(pass.charAt(i), digits) && digit == 0){ // assure password contains a digit
                digit = 1;
            }
            if (digit == 1 && letter == 1 && symbol == 1){
                break; // password is strong enough, no need to continue check
            }
        }

        if (pass.length() >= 8 && pass.length() <= 16){
            return 10 * (digit + letter + symbol) + 2;
        }else {
            return 10 * (digit + letter + symbol);
        }
    }

    /**
     * MongoDB matches a java.util.Date. Convert such objects to the modern java.util.localDateTime
     * @param dateToConvert Date object
     * @return the Date object as the modern implementation of date and time in java
     */
    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime getStartDate(String start){
        LocalDateTime startDate;
        if (start.equals("Last Week")){ // set the start date to a week ago
            startDate = LocalDateTime.now().minusWeeks(1);
        } else if (start.equals("Last Month")){ // set the start date to a month ago
            startDate = LocalDateTime.now().minusMonths(1);
        }  else if (start.equals("Last Year")){ // set the start date to a year ago
            startDate = LocalDateTime.now().minusYears(1);
        } else { // in the default case, show all
            startDate = LocalDateTime.MIN.toLocalDate().atStartOfDay(); // get the earliest date possible
        }

        return startDate;
    }

    public static void generateCSV(ArrayList<Document> actions) throws Exception{
        File csvOutputFile = new File("report-"+LocalDateTime.now().toString()+".csv");
        double balance = 0.0; // calculate the balance of all actions
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("Type,Date,Category,Sum"); // write the header line
            for (Document doc : actions){
                double sign = (doc.get("type").toString().equals("outcome")) ? -1 : 1; // determine the sign of current sum
                balance += (Double)doc.get("sum") * sign; // take into account the current action
                pw.println(convertToCSV(doc)); // add the current action to the file
            }
            pw.println("Total Balance,,," + balance);
        }
    }


    private static boolean in(char c, String str){
        for (int i = 0; i < str.length(); i++) { // search for the received char in the string
            if (str.charAt(i) == c){
                return true; // return true if char was found
            }
        }

        return false; // if execution arrived here, it means the char is not in the String
    }

    private static String convertToCSV(Document data) { // return the received document in a matching CSV format
        return data.get("type") + "," + data.get("date") + "," + data.get("category") + "," + data.get("sum");
    }


}