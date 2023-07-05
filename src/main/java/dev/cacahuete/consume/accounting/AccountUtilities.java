package dev.cacahuete.consume.accounting;

import java.util.Random;

public class AccountUtilities {
    static final int ACCOUNT_ID_CELL_COUNT = 5;
    static final int ACCOUNT_ID_CELL_NUMBER_COUNT = 4;
    static final String TRANSACTION_ID_CHAR_TABLE = "abcdefghijklmnopqrstuvwxyz_#-";
    public static final int ACCOUNT_ID_MAX_LENGTH = ACCOUNT_ID_CELL_COUNT * (ACCOUNT_ID_CELL_NUMBER_COUNT + 1);

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);

            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String generateWalletId() {
        String finalId = "";

        Random rng = new Random();
        for (int i = 0; i < ACCOUNT_ID_CELL_COUNT; i++) {
            for (int n = 0; n < ACCOUNT_ID_CELL_NUMBER_COUNT; n++) {
                finalId += rng.nextInt(10);
            }

            finalId += " ";
        }

        return finalId.trim();
    }

    public static String generateTransactionId() {
        String finalId = "";

        Random rng = new Random();
        for (int i = 0; i < 20; i++) {
            finalId += TRANSACTION_ID_CHAR_TABLE.charAt(rng.nextInt(TRANSACTION_ID_CHAR_TABLE.length()));
        }

        return finalId;
    }

    public static long superBadHash(String value) {
        long hash = -32767;

        for (int i = 0; i < value.length(); i++) {
            hash *= (int)value.charAt(i) * (i+1) + i << 4;
        }

        return hash << (value.length() / 4);
    }
}
