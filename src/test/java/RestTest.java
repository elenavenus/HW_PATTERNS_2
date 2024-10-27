import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

public class RestTest {

    /* Сценарии:
       - Корректный вход
       - Неправильный логин
       - Неправильный пароль
       - Отсутствие пароля
       - Отстуствие логина
       - Заблокированный пользователь
    * */
    @BeforeEach
    public void openPage(){
        open("http://localhost:9999");
    }

    @Test
    public void shouldCorrectAuth() {
        DataGenerator.RegistrationDto dto = DataGenerator.Registration.getRegisteredUser("active");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin());
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("h2").should(Condition.visible).shouldHave(Condition.exactText("  Личный кабинет"));
    }

    @Test
    public void shouldValidateLogin() {
        DataGenerator.RegistrationDto dto = DataGenerator.Registration.getRegisteredUser("active");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin() + "123afsdfs123");
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=error-notification] div.notification__content").should(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void shouldValidatePassword() {
        DataGenerator.RegistrationDto dto = DataGenerator.Registration.getRegisteredUser("active");
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(dto.getLogin());
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(dto.getPassword() + "123afsdfs123");
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=error-notification] div.notification__content").should(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void shouldLoginNotEmpty() {
        SelenideElement form = $("form");
        SelenideElement passwordInput = form.$("[data-test-id=password] input");
        passwordInput.setValue(DataGenerator.generatePassword());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=login] span.input__sub")
                .should(Condition.visible)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldPasswordNotEmpty() {
        SelenideElement form = $("form");
        SelenideElement loginInput = form.$("[data-test-id=login] input");
        loginInput.setValue(DataGenerator.generateLogin());
        SelenideElement authButton = form.$("button");
        authButton.click();
        $("[data-test-id=password] span.input__sub")
                .should(Condition.visible)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldBlockAuth() {
        DataGenerator.RegistrationDto blockedDto = DataGenerator.Registration.getRegisteredUser("blocked");
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
