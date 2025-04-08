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
@Feature("Получение сущности")
public class EntityGetTest {
    private static final String TEST_TITLE = "Текст заголовка сущности";
    private static final String TEST_ADDITIONAL_INFO = "Информация";
    private static final int TEST_ADDITIONAL_NUMBER = 456;
    private static final List<Integer> TEST_IMPORTANT_NUMBERS = List.of(4, 5, 6);
    private static final boolean TEST_VERIFIED = false;

    private String createdEntityId;
    private SoftAssert softAssert;

    @BeforeMethod
    @Step("Создание тестовой сущности")
    public void createTestEntity() {
        Entity requestEntity = Entity.builder()
                .addition(Entity.Addition.builder()
                        .additional_info(TEST_ADDITIONAL_INFO)
                        .additional_number(TEST_ADDITIONAL_NUMBER)
                        .build())
                .important_numbers(TEST_IMPORTANT_NUMBERS)
                .title(TEST_TITLE)
                .verified(TEST_VERIFIED)
                .build();

        Response createResponse = BaseRequests.post("/api/create", requestEntity);
        Assert.assertEquals(createResponse.getStatusCode(), 200,
                "Сущность не была создана (неверный статус код)");

        createdEntityId = createResponse.asString();
    }

    @Test(description = "Тест: Получение сущности", threadPoolSize = 9, invocationCount = 1)
    @Story("Получение сущности по ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Тест проверяет корректность получения данных сущности по её идентификатору")
    public void testGetEntity() {
        softAssert = new SoftAssert();
        Response getResponse = BaseRequests.get("/api/get/" + createdEntityId);

        Assert.assertEquals(getResponse.getStatusCode(), 200,
                "Неверный статус код при запросе несуществующей сущности");

        Entity responseEntity = getResponse.as(Entity.class);

        softAssert.assertEquals(responseEntity.getId(), createdEntityId,
                "ID полученной сущности не совпадает с ожидаемым");
        softAssert.assertEquals(responseEntity.getTitle(), TEST_TITLE,
                "Заголовок сущности не совпадает с ожидаемым");
        softAssert.assertEquals(responseEntity.isVerified(), TEST_VERIFIED,
                "Поле verified не совпадает с ожидаемым");

        Entity.Addition addition = responseEntity.getAddition();
        softAssert.assertNotNull(addition, "Addition не должен быть null");
        softAssert.assertEquals(addition.getAdditional_info(), TEST_ADDITIONAL_INFO,
                "additional_info не совпадает с ожидаемым");
        softAssert.assertEquals(addition.getAdditional_number(), TEST_ADDITIONAL_NUMBER,
                "additional_number не совпадает с ожидаемым");
        softAssert.assertNotNull(addition.getId(),
                "ID в Addition не должен быть null");

        softAssert.assertEquals(responseEntity.getImportant_numbers(), TEST_IMPORTANT_NUMBERS,
                "important_numbers не совпадают с ожидаемыми");

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
