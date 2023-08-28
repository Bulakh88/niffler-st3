package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;
import static io.qameta.allure.Allure.step;

public class InvitationReceivedWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = INVITATION_RECEIVED) UserJson userForTest) {
        open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("104")
    void invitationReceivedShouldBeDisplayedInAllPeopleTable() {
        step("Открыть страницу \"All people\"", () ->
                $("[data-tooltip-id='people']").click());

        step("Количество кнопок \"Submit invitation\" должно равняться 1", () ->
                $(".people-content").$("table")
                        .shouldBe(visible)
                        .$("tbody")
                        .$$("[data-tooltip-id='submit-invitation']")
                        .shouldHave(size(1))
        );

        step("Количество кнопок \"Decline invitation\" должно равняться 1", () ->
                $(".people-content").$("table")
                        .shouldBe(visible)
                        .$("tbody")
                        .$$("[data-tooltip-id='decline-invitation']")
                        .shouldHave(size(1))
        );
    }

    @Test
    @AllureId("105")
    void invitationReceivedShouldBeDisplayedInFriendsTable() {
        step("Открыть страницу \"Friends\"", () ->
                $("[data-tooltip-id='friends']").click());

        step("Количество кнопок \"Submit invitation\" должно равняться 1", () ->
                $(".people-content").$("table")
                        .shouldBe(visible)
                        .$("tbody")
                        .$$("[data-tooltip-id='submit-invitation']")
                        .shouldHave(size(1))
        );

        step("Количество кнопок \"Decline invitation\" должно равняться 1", () ->
                $(".people-content").$("table").shouldBe(visible)
                        .$("tbody")
                        .$$("[data-tooltip-id='decline-invitation']")
                        .shouldHave(size(1))
        );
    }
}
