import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private DataGenerator() {
    }

    private static void sendRequest(RegistrationDto user) {
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(user) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    public static String generateLogin() {
        Faker faker = new Faker(new Locale("eng"));
        return faker.name().username() + faker.numerify("###");
    }

    public static String generatePassword() {
        Faker faker = new Faker(new Locale("eng"));
        return faker.letterify("??????") + faker.numerify("######");
    }


    public static class Registration {

        public static RegistrationDto getUser(String status) {
            String login = generateLogin();
            String password = generatePassword();
            return new RegistrationDto(login, password, status);
        }

        public static RegistrationDto getRegisteredUser(String status) {
            RegistrationDto user = getUser(status);
            sendRequest(user);
            return user;
        }
    }

    @Value
    public static class RegistrationDto {
        String login;
        String password;
        String status;
    }


}
