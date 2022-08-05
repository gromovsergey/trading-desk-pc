package app.programmatic.ui.creative.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreativeCopy {
    public static String getNewName(String oldName) {
        String patternString = "[0-9]{4}-[0-9]{2}-[0-9]{2}(_|T)[0-9]{2}.*$";
        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss_SS"));
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(oldName);
        if (matcher.find()) {
            return oldName.replaceAll(patternString, dateNow) + (int) (Math.random() * 100);
        } else {
            return oldName + " " + dateNow + (int) (Math.random() * 100);
        }
    }
}
