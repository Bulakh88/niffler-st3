package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_SENT;
import static io.qameta.allure.Allure.step;

public class InvitationSentWebTest extends BaseWebTest {

    @Test
    @AllureId("106")
    void invitationSendShouldBeDisplayedInAllPeopleTable0(@User(userType = INVITATION_SENT) UserJson userForTest) {
        open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();

        step("Открыть страницу \"All people\"", () ->
                $("[data-tooltip-id='people']").click());

        step("Количество строк со статусом \"Pending invitation\" должно равняться 1", () ->
                $(".people-content").$("table")
                        .shouldBe(visible)
                        .$("tbody")
                        .$$("td")
                        .filterBy(text("Pending invitation"))
                        .shouldHave(size(1)));
    }


    @Test
    @AllureId("107")
    void invitationSendShouldBeDisplayedInAllPeopleTable1(@User(userType = INVITATION_SENT) UserJson userForTest) {
        open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();

        step("Открыть страницу \"All people\"", () ->
                $("[data-tooltip-id='people']").click());

        step("Количество строк со статусом \"Pending invitation\" должно равняться 1", () ->
                $(".people-content").$("table")
                        .shouldBe(visible)
                        .$("tbody")
                        .$$("td")
                        .filterBy(text("Pending invitation"))
                        .shouldHave(size(1)));
    }
}
