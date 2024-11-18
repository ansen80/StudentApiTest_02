import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class StudentApiTest_02 {

    @BeforeEach
    void setup() {
        RestAssured.baseURI  = "http://localhoct:8080";
    }

    @Test
    void getStudentById_ShouldReturnStudent() {
        // Добавление студента
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 1, \"name\": \"Андрей\", \"marks\": [4, 5]}")
                .post("/student");

        // Проверка, что студент существует
        RestAssured.given()
                .get("/student/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Андрей"))
                .body("marks", contains(4, 5));
    }

    @Test
    void getStudentById_ShouldReturn404_WhenNotFound() {
        RestAssured.given()
                .get("/student/55")
                .then()
                .statusCode(404);
    }

    @Test
    void postStudent_ShouldAddStudent_WhenNew() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 2, \"name\": \"Алиса\", \"marks\": [4, 5]}")
                .post("/student");

        assertEquals(201, response.statusCode());
    }

    @Test
    void postStudent_ShouldUpdateStudent_WhenExists() {
        // Добавляем студента
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 3, \"name\": \"Вова\", \"marks\": [4]}")
                .post("/student");

        // Обновляем имя
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 3, \"name\": \"Сергей\", \"marks\": [4]}")
                .post("/student")
                .then()
                .statusCode(201)
                .body("name", equalTo("Сергей"));
    }

    @Test
    void postStudent_ShouldAddStudent_WhenIdNull() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": null, \"name\": \"Ева\", \"marks\": [4]}")
                .post("/student");

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getInt("id"));
    }

    @Test
    void postStudent_ShouldReturn400_WhenNameNotProvided() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 5, \"marks\": [5]}")
                .post("/student")
                .then()
                .statusCode(400);
    }

    @Test
    void deleteStudent_ShouldReturn200_WhenExists() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 6, \"name\": \"Иван\", \"marks\": []}")
                .post("/student");

        // Удаляем студента
        RestAssured.given()
                .delete("/student/6")
                .then()
                .statusCode(200);
    }

    @Test
    void deleteStudent_ShouldReturn404_WhenNotFound() {
        RestAssured.given()
                .delete("/student/55")
                .then()
                .statusCode(404);
    }

    @Test
    void getTopStudent_ShouldReturnEmpty_WhenNoStudents() {
        Response response = RestAssured.given()
                .get("/topStudent");

        assertEquals(200, response.statusCode());
        assertTrue(response.asString().isEmpty());
    }

    @Test
    void getTopStudent_ShouldReturnEmpty_WhenNoMarks() {
        // Добавляем студента без оценок
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 7, \"name\": \"Борис\", \"marks\": []}")
                .post("/student");

        Response response = RestAssured.given()
                .get("/topStudent");

        assertEquals(200, response.statusCode());
        assertTrue(response.asString().isEmpty());
    }

    @Test
    void getTopStudent_ShouldReturnStudent_WhenHighestAverage() {
        // Добавляем студентов
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 8, \"name\": \"Виктор\", \"marks\": [4, 5]}")
                .post("/student");
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 9, \"name\": \"Макс\", \"marks\": [5, 5]}")
                .post("/student");

        RestAssured.given()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body("id", equalTo(8))
                .body("name", equalTo("Виктор"));
    }

    @Test
    void getTopStudent_ShouldReturnMultipleStudents_WhenSameMax() {
        // Добавляем студентов
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 10, \"name\": \"Алиса\", \"marks\": [5]}")
                .post("/student");
        RestAssured.given()
                .contentType("application/json")
                .body("{\"id\": 11, \"name\": \"Николай\", \"marks\": [5]}")
                .post("/student");

        RestAssured.given()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body("id", hasItems(10, 11));
    }
}