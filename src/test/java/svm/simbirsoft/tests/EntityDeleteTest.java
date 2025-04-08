package svm.simbirsoft.tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import svm.simbirsoft.helpers.BaseRequests;
import svm.simbirsoft.models.Entity;

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

        createdEntityId = BaseRequests.post("/api/create", requestEntity)
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    @Test(description = "Тест: Удаление сущности", threadPoolSize = 9, invocationCount = 1)
    @Story("Удаление сущности")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Тест проверяет успешное удаление сущности по ID")
    public void testSuccessfulDelete() {
        BaseRequests.delete("/api/delete/" + createdEntityId)
                .then()
                .statusCode(204);
    }
}