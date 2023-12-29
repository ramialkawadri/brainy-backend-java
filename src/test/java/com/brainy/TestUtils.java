package com.brainy;

import java.util.Random;

import com.brainy.model.entity.User;

public class TestUtils {

	private static final int NUMBER_OF_ENGLISH_CHARACTERS = 26;

	// Arbitrary values, doesn't have any special meaning
	private static final int MAX_RANDOM_FILENAME_LENGTH = 12;
	private static final int MAX_RANDOM_USERNAME_LENGTH = 20;
	private static final int MIN_RANDOM_USERNAME_LENGTH = 3;
	private static final int MAX_JSON_PROPERTY_LENGTH = 10;
	private static final int MAX_JSON_VALUE_LENGTH = 10;
	private static final int MAX_NUMBER_OF_PROPERTIES_IN_JSON = 10;

	public static User generateRandomUser() {
		String username = generateRandomUsername();

		return new User(username, "testPassword123", username + "@test.com",
				"firstName_" + username, "lastName_" + username);
	}

	public static String generateRandomUsername() {
		return generateStringOfMaxLength(MAX_RANDOM_USERNAME_LENGTH, MIN_RANDOM_USERNAME_LENGTH,
				false);
	}

	public static String generateRandomFilename() {
		return generateStringOfMaxLength(MAX_RANDOM_FILENAME_LENGTH);
	}

	public static String generateRandomFileContent() {
		Random random = new Random();
		int numberOfProperties = random.nextInt(MAX_NUMBER_OF_PROPERTIES_IN_JSON);
		String fileContent = "{";

		for (int i = 0; i < numberOfProperties; i++) {
			if (i > 0)
				fileContent += ",";

			fileContent += '"' + generateStringOfMaxLength(MAX_JSON_PROPERTY_LENGTH) + "\": " + '"'
					+ generateStringOfMaxLength(MAX_JSON_VALUE_LENGTH) + '"';
		}

		return fileContent + "}";
	}

	public static String generateStringOfMaxLength(int maxLength) {
		return generateStringOfMaxLength(maxLength, true);
	}

	public static String generateStringOfMaxLength(int maxLength, boolean includeUppercase) {
		return generateStringOfMaxLength(maxLength, 1, includeUppercase);
	}

	public static String generateStringOfMaxLength(int maxLength, int minLength,
			boolean includeUppercase) {
		Random random = new Random();
		int length = random.nextInt(maxLength - minLength) + minLength; // +1 to not allow zero
		String result = "";

		for (int i = 0; i < length; ++i) {
			char nextCharacter = (char) ('a' + random.nextInt(NUMBER_OF_ENGLISH_CHARACTERS));

			boolean isUpperCase = random.nextBoolean();

			if (isUpperCase && includeUppercase)
				nextCharacter = Character.toUpperCase(nextCharacter);

			result = result + nextCharacter;
		}

		return result;
	}
}
