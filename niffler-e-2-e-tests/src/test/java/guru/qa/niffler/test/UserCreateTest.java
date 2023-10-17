package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.CreateUserDBExtension;
import guru.qa.niffler.jupiter.DBUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;
import static java.lang.String.format;

@ExtendWith(CreateUserDBExtension.class)
public class UserCreateTest extends BaseWebTest {

    @DBUser(username = "tim_12", password = "12345")
    @Test
    void dbUserExtensionBeforeTestGetUserWebTest1(UserEntity user) {

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
            $(".avatar-container").shouldBe(visible);
        });

        step("Имя на странице профиля должно соответствовать логину", () -> {
            $(".avatar-container").shouldHave(text(user.getUsername()));
        });

    }
}