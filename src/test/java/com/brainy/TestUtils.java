package com.brainy;

import java.util.Random;
import java.util.UUID;

import com.brainy.model.entity.User;

public class TestUtils {

    private static final int NUMBER_OF_ENGLISH_CHARACTERS = 26;

    // Arbitrary values, doesn't have any special meaning
    private static final int MAX_RANDOM_FILENAME_LENGTH = 12;
    private static final int MAX_RANDOM_FILE_CONTENT_LENGTH = 200;

    public static User generateRandomUser() {
        String username = generateUniqueUsername();
    
        return new User(
                username,
                "testPassword123",
                username + "@test.com",
                "firstName_" + username,
                "lastName_" + username);
    }

    public static String generateUniqueUsername() {
        return UUID.randomUUID().toString();
    }

    public static String generateRandomFilename() {
        return generateStringOfMaxLength(MAX_RANDOM_FILENAME_LENGTH);
    }

    public static String generateRandomFileContent() {
        return generateStringOfMaxLength(MAX_RANDOM_FILE_CONTENT_LENGTH);
    }

    public static String generateStringOfMaxLength(int maxLength) {
        Random random = new Random();
        int length = random.nextInt(maxLength) + 1;  // +1 to not allow zero
        String result = "";

        for (int i = 0; i < length; ++i) {
            char nextCharacter = (char)
            ('a' + random.nextInt(NUMBER_OF_ENGLISH_CHARACTERS));

            boolean isUpperCase = random.nextBoolean();

            if (isUpperCase)
                nextCharacter = Character.toUpperCase(nextCharacter);

            result = result + nextCharacter;
        }

        return result;
    }
}
