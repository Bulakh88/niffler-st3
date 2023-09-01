package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.CreateUserExtension;
import guru.qa.niffler.jupiter.DBUser;
import guru.qa.niffler.jupiter.Dao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(CreateUserExtension.class)
public class UserCreateTest extends BaseWebTest {

    @Dao
    private AuthUserDAO authUserDAO;
    @Dao
    private UserDataUserDAO userDataUserDAO;

    @DBUser(username = "tim_11", password = "12345")
    @Test
    void dbUserExtensionBeforeTestGetUserWebTest1(UserEntity user) {
        final AtomicReference<String> usernameFromGUI = new AtomicReference<>();
        final SelenideElement usernameFromGUIElement = $(".avatar-container");

        step(format("Залогиниться под юзером %s", user.getUsername()), () -> {
            Selenide.open("http://127.0.0.1:3000/main");
            $("a[href*='redirect']").click();
            $("input[name='username']").setValue(user.getUsername());
            $("input[name='password']").setValue(user.getPassword());
            $("button[type='submit']").click();
            $(".main-content__section-stats").should(visible);
        });

        step("Перейти на страницу профиля", () -> {
            $(byAttribute("href", "/profile")).click();
            usernameFromGUIElement.shouldBe(visible);
            usernameFromGUI.set(usernameFromGUIElement.getText());
        });

        step("Имя на странице профиля должно соответствовать логину", () -> {
            assertEquals(user.getUsername(), usernameFromGUI.get());
        });

    }
}