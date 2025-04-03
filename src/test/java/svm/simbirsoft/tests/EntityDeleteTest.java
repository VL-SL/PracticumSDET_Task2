package svm.simbirsoft.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import svm.simbirsoft.helpers.BaseRequests;
import svm.simbirsoft.pojo.Entity;

import java.util.List;

@Epic("Управление сущностями")
@Feature("Удаление сущностей")
public class EntityDeleteTest {
    private String createdEntityId;

    @BeforeMethod
    @Step("Создание тестовой сущности для удаления")
    public void createTestEntity() {
        Entity requestEntity = Entity.builder()
                .addition(Entity.Addition.builder()
                        .additional_info("Информация")
                        .additional_number(456)
                        .build())
                .important_numbers(List.of(4, 5, 6))
                .title("Текст заголовка сущности")
                .verified(false)
                .build();

        Response createResponse = BaseRequests.post("/api/create", requestEntity);
        Assert.assertEquals(createResponse.getStatusCode(), 200,
                "Сущность не была создана (неверный статус код)");

        createdEntityId = createResponse.asString();
    }

    @Test (description = "Тест: Удаление сущности", threadPoolSize = 5, invocationCount = 1)
    @Story("Удаление сущности")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Тест проверяет успешное удаление сущности по ID")
    public void testSuccessfulDelete() {
        Response deleteResponse = BaseRequests.delete("/api/delete/" + createdEntityId);
        Assert.assertEquals(deleteResponse.getStatusCode(), 204,
                "Сущность не была удалена (неверный статус код)");
    }
}
