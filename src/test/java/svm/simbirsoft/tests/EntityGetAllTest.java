package svm.simbirsoft.tests;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import svm.simbirsoft.helpers.BaseRequests;
import svm.simbirsoft.models.Entity;

import java.util.List;

@Epic("Управление сущностями")
@Feature("Получение списка сущностей")
public class EntityGetAllTest {
    private static final String TEST_TITLE_1 = "Текст  заголовка сущности 1";
    private static final String TEST_TITLE_2 = "Текст  заголовка сущности 2";
    private static final String TEST_INFO = "Информация";
    private static final int TEST_NUMBER = 100;
    private static final List<Integer> TEST_NUMBERS = List.of(1, 2, 3);

    private static final int PAGE1 = 1;
    private static final int PAGE2 = 2;
    private static final int EXPECTEDPERPAGE = 1;

    private String entityId1;
    private String entityId2;
    private SoftAssert softAssert;

    @BeforeMethod
    @Step("Подготовка тестовых данных")
    public void createTestData() {
        softAssert = new SoftAssert();

        // (verified = true)
        Entity entity1 = Entity.builder()
                .title(TEST_TITLE_1)
                .verified(true)
                .addition(Entity.Addition.builder()
                        .additional_info(TEST_INFO)
                        .additional_number(TEST_NUMBER)
                        .build())
                .important_numbers(TEST_NUMBERS)
                .build();

        Response response1 = BaseRequests.post("/api/create", entity1);
        entityId1 = response1.asString();
        softAssert.assertEquals(response1.getStatusCode(), 200);

        // (verified = false)
        Entity entity2 = Entity.builder()
                .title(TEST_TITLE_2)
                .verified(false)
                .addition(Entity.Addition.builder()
                        .additional_info(TEST_INFO)
                        .additional_number(TEST_NUMBER + 1)
                        .build())
                .important_numbers(List.of(4, 5, 6))
                .build();

        Response response2 = BaseRequests.post("/api/create", entity2);
        entityId2 = response2.asString();
        softAssert.assertEquals(response2.getStatusCode(), 200);

        softAssert.assertAll();
    }

    @Test(description = "Тест: Получение всех сущностей", threadPoolSize = 9, invocationCount = 1)
    @Story("Получение всех сущностей")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тест проверяет получение полного списка сущностей")
    public void testGetAllEntities() {
        Response response = BaseRequests.get("/api/getall");
        softAssert.assertEquals(response.getStatusCode(), 200,
                "Неверный статус код при получении всех сущностей");

        List<Entity> entities = response.jsonPath().getList("entity", Entity.class);
        softAssert.assertFalse(entities.isEmpty(), "Список сущностей не должен быть пустым");

        softAssert.assertTrue(entities.stream().anyMatch(e -> e.getId().equals(entityId1)),
                "Первая тестовая сущность не найдена в ответе");
        softAssert.assertTrue(entities.stream().anyMatch(e -> e.getId().equals(entityId2)),
                "Вторая тестовая сущность не найдена в ответе");

        softAssert.assertAll();
    }

    @Test(description = "Тест:  Фильтрация сущностей по параметру verified", threadPoolSize = 9, invocationCount = 1)
    @Story("Фильтрация по verified")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тест проверяет фильтрацию сущностей по параметру verified")
    public void testGetAllFromVerified() {
        Response response = BaseRequests.get("/api/getall?verified=true");
        softAssert.assertEquals(response.getStatusCode(), 200,
                "Неверный статус код при получении всех сущностей по параметру verified");

        List<Entity> entities = response.jsonPath().getList("entity", Entity.class);

        // verified=true
        entities.forEach(entity ->
                softAssert.assertTrue(entity.isVerified(),
                        "Все сущности должны иметь verified=true при фильтрации"));

        softAssert.assertTrue(entities.stream().anyMatch(e -> e.getId().equals(entityId1)),
                "Тестовая сущность с verified=true не найдена");

        softAssert.assertAll();
    }

    @Test(description = "Тест:  Фильтрация сущностей по параметру title", threadPoolSize = 9, invocationCount = 1)
    @Story("Фильтрация по title")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тест проверяет фильтрацию сущностей по параметру title")
    public void testGetAllFromTitle() {
        Response response = BaseRequests.get("/api/getall?title=" + TEST_TITLE_1);
        softAssert.assertEquals(response.getStatusCode(), 200,
                "Неверный статус код при получении всех сущностей по параметру title");

        List<Entity> entities = response.jsonPath().getList("entity", Entity.class);
        softAssert.assertFalse(entities.isEmpty(), "Список сущностей не должен быть пустым");

        entities.forEach(entity ->
                softAssert.assertTrue(entity.getTitle().contains(TEST_TITLE_1),
                        "Все сущности должны содержать искомый заголовок"));

        softAssert.assertTrue(entities.stream().anyMatch(e -> e.getId().equals(entityId1)),
                "тестовая сущность c заголовком" + TEST_TITLE_1 + "не найдена");

        softAssert.assertAll();
    }

    @Test(description = "Тест: проверка пагинации", threadPoolSize = 9, invocationCount = 1)
    @Story("Пагинация")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тест проверяет работу пагинации при получении списка сущностей")
    public void testGetAllFromPage() {
        Response page1Response = BaseRequests.get("/api/getall?page=" + PAGE1 + "&perPage=" + EXPECTEDPERPAGE);
        softAssert.assertEquals(page1Response.getStatusCode(), 200,
                "Неверный статус код для страницы 1");

        List<Entity> page1Entities = page1Response.jsonPath().getList("entity", Entity.class);
        softAssert.assertEquals(page1Entities.size(), EXPECTEDPERPAGE,
                "Неверное количество сущностей на странице 1");

        Response page2Response = BaseRequests.get("/api/getall?page=" + PAGE2 + "&perPage=" + EXPECTEDPERPAGE);
        softAssert.assertEquals(page2Response.getStatusCode(), 200,
                "Неверный статус код для страницы 2");

        List<Entity> page2Entities = page2Response.jsonPath().getList("entity", Entity.class);
        softAssert.assertEquals(page2Entities.size(), EXPECTEDPERPAGE,
                "Неверное количество сущностей на странице 2");

        // Проверка пагинации
        if (!page1Entities.isEmpty() && !page2Entities.isEmpty()) {
            softAssert.assertNotEquals(page1Entities.get(0).getId(), page2Entities.get(0).getId(),
                    "Сущности на разных страницах должны быть разными");
        }

        softAssert.assertAll();
    }

    @Test(description = "Тест: Проверка количества элементов", threadPoolSize = 9, invocationCount = 1)
    @Story("Количество элементов на странице")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тест проверяет параметр perPage для управления количеством элементов на странице")
    public void testGetAllFromPerPage() {
        Response response = BaseRequests.get("/api/getall?page=" + PAGE1 + "&perPage=" + EXPECTEDPERPAGE);
        softAssert.assertEquals(response.getStatusCode(), 200,
                "Неверный статус код при получении всех сущностей по параметру perPage");

        List<Entity> entities = response.jsonPath().getList("entity", Entity.class);
        softAssert.assertEquals(entities.size(), EXPECTEDPERPAGE,
                "Количество сущностей должно соответствовать параметру perPage");

        softAssert.assertAll();
    }

    @AfterMethod
    @Step("Очистка тестовых данных")
    public void cleanUp() {
        if (entityId1 != null) {
            Response deleteResponse1 = BaseRequests.delete("/api/delete/" + entityId1);
            softAssert.assertEquals(deleteResponse1.getStatusCode(), 204,
                    "Сущность не была удалена (неверный статус код)");
        }

        if (entityId2 != null) {
            Response deleteResponse2 = BaseRequests.delete("/api/delete/" + entityId2);
            softAssert.assertEquals(deleteResponse2.getStatusCode(), 204,
                    "Сущность не была удалена (неверный статус код)");
        }

        softAssert.assertAll();
    }
}