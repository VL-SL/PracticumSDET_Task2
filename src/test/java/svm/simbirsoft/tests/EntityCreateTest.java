package svm.simbirsoft.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import svm.simbirsoft.helpers.BaseRequests;
import svm.simbirsoft.pojo.Entity;

import java.util.List;

@Epic("Управление сущностями")
@Feature("Создание сущностей")
public class EntityCreateTest {
    private String createdEntityId;

    @Test (description = "Тест: Создание сущности", threadPoolSize = 5, invocationCount = 1)
    @Story("Создание новой сущности")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Тест проверяет успешное создание новой сущности через API")
    public void testCreateEntity() {
        Entity requestEntity = Entity.builder()
                .addition(Entity.Addition.builder()
                        .additional_info("Информация")
                        .additional_number(123)
                        .build())
                .important_numbers(List.of(1, 2, 3))
                .title("Текст заголовка сущности")
                .verified(true)
                .build();

        Response response = BaseRequests.post("/api/create", requestEntity);

        Assert.assertEquals(response.getStatusCode(), 200, "Сущность была не добавлена (неверный статус код)" );

        createdEntityId = response.asString();
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