import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

@Slf4j
public class RestTest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private static DataGenerator.RegistrationDto dto = DataGenerator.Registration.generateDto(true);
    private static DataGenerator.RegistrationDto blockedDto = DataGenerator.Registration.generateDto(false);

    @BeforeAll
    static void setUpAll() {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(dto) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(blockedDto) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    /* Сценарии:
       - Корректный вход
       - Неправильный логин
       - Неправильный пароль
       - Отсутствие пароля
       - Отстуствие логина
       - Заблокированный пользователь
    * */
    @Test
    public void shouldCorrectAuth() {
        open("http://localhost:9999");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin());
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $$("h2").filter(Condition.visible).get(0).shouldHave(Condition.exactText("  Личный кабинет"));
    }

    @Test
    public void shouldValidateLogin() {
        open("http://localhost:9999");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin() + "123afsdfs123");
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=error-notification] div.notification__content").should(Condition.appear)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void shouldValidatePassword() {
        open("http://localhost:9999");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin());
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword() + "123afsdfs123");
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=error-notification] div.notification__content").should(Condition.appear)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void shouldLoginNotEmpty() {
        open("http://localhost:9999");
        SelenideElement form = $("form");
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=login] span.input__sub")
                .should(Condition.appear)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldPasswordNotEmpty() {
        open("http://localhost:9999");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=password] span.input__sub")
                .should(Condition.appear)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldBlockAuth() {
        open("http://localhost:9999");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(blockedDto.getLogin());
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(blockedDto.getPassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=error-notification] div.notification__content").should(Condition.appear)
                .shouldHave(Condition.text("Пользователь заблокирован"));
    }

}
