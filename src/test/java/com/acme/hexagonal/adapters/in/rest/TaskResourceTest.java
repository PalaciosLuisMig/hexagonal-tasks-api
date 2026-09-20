package com.acme.hexagonal.adapters.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

/**
 * End-to-end test of the hexagonal slice: HTTP -> REST adapter -> use case -> port ->
 * in-memory adapter. The infrastructure (CDI, JPA/H2, JSON) is assembled by Quarkus.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskResourceTest {

    /**
     * The flows below depend on a shared in-memory repository, so we use the same task
     * id across the test methods (created here).
     */
    private static String createdId;

    @Test
    @Order(1)
    void create_task_returns201() {
        createdId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "title": "Build hexagonal demo",
                          "description": "ports, adapters and dependency rules",
                          "dueDate": "2026-10-31"
                        }
                        """)
                .when().post("/tasks")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Build hexagonal demo"))
                .body("status", equalTo("OPEN"))
                .body("dueDate", equalTo("2026-10-31"))
                .extract().path("id");
    }

    @Test
    @Order(2)
    void create_task_withBlankTitle_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        { "title": "   " }
                        """)
                .when().post("/tasks")
                .then()
                .statusCode(400)
                .body("code", equalTo("bad_request"));
    }

    @Test
    @Order(3)
    void list_tasks_returnsCreatedTask() {
        given()
                .when().get("/tasks")
                .then()
                .statusCode(200)
                .body("id", hasSize(1))
                .body("title[0]", equalTo("Build hexagonal demo"));
    }

    @Test
    @Order(4)
    void complete_task_returnsDone() {
        given()
                .when().put("/tasks/{id}/complete", createdId)
                .then()
                .statusCode(200)
                .body("status", equalTo("DONE"));
    }

    @Test
    @Order(5)
    void complete_alreadyDone_returns409() {
        given()
                .when().put("/tasks/{id}/complete", createdId)
                .then()
                .statusCode(409)
                .body("code", equalTo("conflict"));
    }

    @Test
    @Order(6)
    void complete_unknownTask_returns404() {
        given()
                .when().put("/tasks/{id}/complete", "915a0b5f-4546-48d3-8d19-ed27ef436c8e")
                .then()
                .statusCode(404)
                .body("code", equalTo("not_found"));
    }

    @Test
    @Order(7)
    void delete_existingTask_returns204() {
        given()
                .when().delete("/tasks/{id}", createdId)
                .then()
                .statusCode(204);
    }
}