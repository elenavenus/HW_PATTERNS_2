import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }

    public static String generateLogin() {
        Faker faker = new Faker(new Locale("eng"));
        return faker.name().username() + faker.numerify("###");
    }

    public static String generatePassword() {
        Faker faker = new Faker(new Locale("eng"));
        return faker.letterify("??????") + faker.numerify("######");
    }

    public static String generateStatus(boolean isActive) {
        if (isActive) {
            return "active";
        } else {
            return "blocked";
        }
    }

    public static class Registration {
        public static RegistrationDto generateDto(boolean isActive) {
            String login = generateLogin();
            String password = generatePassword();
            String status = generateStatus(isActive);
            return new RegistrationDto(login, password, status);
        }
    }

    @Value
    public static class RegistrationDto {
        String login;
        String password;
        String status;
    }


}
