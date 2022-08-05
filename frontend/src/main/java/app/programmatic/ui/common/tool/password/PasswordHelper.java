package app.programmatic.ui.common.tool.password;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class PasswordHelper {
    private static final String[] GENERATED_PASSWORD_CHARS = new String[]{"abcdefghijklmnopqrstuvwxyz","ABCDEFGHIJKLMNOPQRSTUVWXYZ","1234567890", "!@#$;^:&?"};
    private static final int GENERATED_PASSWORD_SIZE = 10;
    private static final String CHARSET = "UTF-8";
    private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-512";

    public static String encryptPassword(String password) {
        if (password == null) {
            return null;
        }

        try {
            byte[] bytes = password.getBytes(CHARSET);
            MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
            md.reset();
            bytes = md.digest(bytes);
            return new String((new Base64()).encode(bytes), CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generatePassword() {
        ThreadLocalRandom r = ThreadLocalRandom.current();

        ArrayList<Character> sequence = new ArrayList<>(GENERATED_PASSWORD_SIZE);

        // add one from each set
        for (String set : GENERATED_PASSWORD_CHARS) {
            sequence.add(set.charAt(r.nextInt(set.length())));
        }

        // fill rest from the random sets
        for (int i = GENERATED_PASSWORD_CHARS.length; i < GENERATED_PASSWORD_SIZE; i++) {
            String set = GENERATED_PASSWORD_CHARS[r.nextInt(GENERATED_PASSWORD_CHARS.length)];
            sequence.add(set.charAt(r.nextInt(set.length())));
        }

        Collections.shuffle(sequence);
        StringBuilder builder = new StringBuilder(GENERATED_PASSWORD_SIZE);
        sequence.forEach( c -> builder.append(c) );
        return builder.toString();
    }
}
