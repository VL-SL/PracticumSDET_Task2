package svm.simbirsoft.tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import svm.simbirsoft.helpers.BaseRequests;
import svm.simbirsoft.models.Entity;

import java.util.List;

@Epic("Управление сущностями")
@Feature("Обновление сущностей")
public class EntityUpdateTest {
    private static final String INITIAL_TITLE = "Исходный заголовок";
    private static final String INITIAL_INFO = "Исходная информация";
    private static final int INITIAL_NUMBER = 123;
    private static final List<Integer> INITIAL_NUMBERS = List.of(1, 2, 3);
    private static final boolean INITIAL_VERIFIED = false;

    private static final String UPDATED_TITLE = "Обновленный заголовок";
    private static final String UPDATED_INFO = "Обновленная информация";
    private static final int UPDATED_NUMBER = 456;
    private static final List<Integer> UPDATED_NUMBERS = List.of(4, 5, 6);
    private static final boolean UPDATED_VERIFIED = true;

    private String createdEntityId;
    private SoftAssert softAssert;

    @BeforeMethod
    @Step("Подготовка тестовых данных")
    public void setUp() {
        softAssert = new SoftAssert();

        Entity initialEntity = Entity.builder()
                .addition(Entity.Addition.builder()
                        .additional_info(INITIAL_INFO)
                        .additional_number(INITIAL_NUMBER)
                        .build())
                .important_numbers(INITIAL_NUMBERS)
                .title(INITIAL_TITLE)
                .verified(INITIAL_VERIFIED)
                .build();

        Response createResponse = BaseRequests.post("/api/create", initialEntity);
        Assert.assertEquals(createResponse.getStatusCode(), 200,
                "Не удалось создать сущность для теста");

        createdEntityId = createResponse.asString();
    }

    @Test(description = "Тест: Обновление данных сущности", threadPoolSize = 9, invocationCount = 1)
    @Story("Обновление сущности")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Тест проверяет корректность обновления данных сущности")
    public void testUpdateEntity() {
        Entity updatedEntity = Entity.builder()
                .addition(Entity.Addition.builder()
                        .additional_info(UPDATED_INFO)
                        .additional_number(UPDATED_NUMBER)
                        .build())
                .important_numbers(UPDATED_NUMBERS)
                .title(UPDATED_TITLE)
                .verified(UPDATED_VERIFIED)
                .build();

        Response updateResponse = BaseRequests.patch("/api/patch/" + createdEntityId, updatedEntity);
        Assert.assertEquals(updateResponse.getStatusCode(), 204,
                "Неверный статус код при обновлении сущности");

        Response getResponse = BaseRequests.get("/api/get/" + createdEntityId);
        Entity responseEntity = getResponse.as(Entity.class);

        softAssert.assertEquals(responseEntity.getTitle(), UPDATED_TITLE,
                "Заголовок не был обновлен");
        softAssert.assertEquals(responseEntity.isVerified(), UPDATED_VERIFIED,
                "Статус verified не был обновлен");

        Entity.Addition addition = responseEntity.getAddition();
        softAssert.assertNotNull(addition, "Addition не должен быть null");
        softAssert.assertEquals(addition.getAdditional_info(), UPDATED_INFO,
                "additional_info не был обновлен");
        softAssert.assertEquals(addition.getAdditional_number(), UPDATED_NUMBER,
                "additional_number не был обновлен");

        softAssert.assertEquals(responseEntity.getImportant_numbers(), UPDATED_NUMBERS,
                "important_numbers не были обновлены");

        softAssert.assertAll();
    }

    @AfterMethod
    @Step("Удаление тестовой сущности")
    public void cleanup() {
        if (createdEntityId != null) {
            Response deleteResponse = BaseRequests.delete("/api/delete/" + createdEntityId);
            Assert.assertEquals(deleteResponse.getStatusCode(), 204,
                    "Сущность не была удалена (неверный статус код)");
        }
    }
}