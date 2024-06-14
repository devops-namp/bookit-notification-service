package uns.ac.rs.controller;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uns.ac.rs.entity.Notification;
import uns.ac.rs.repository.NotificationRepository;
import uns.ac.rs.resources.MongoDBResource;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(value =  MongoDBResource.class, restrictToAnnotatedClass = true)
public class NotificationControllerTest {

    @Inject
    NotificationRepository notificationRepository;

    @BeforeEach
    public void initTestData() {
        notificationRepository.deleteAll();

        Notification notification1 = new Notification();
        notification1.setId(new ObjectId());
        notification1.setRecipientId("user1");
        notification1.setText("Test message 1");
        notification1.setRead(false);
        notification1.setTime(LocalDateTime.now());

        Notification notification2 = new Notification();
        notification2.setId(new ObjectId());
        notification2.setRecipientId("user2");
        notification2.setText("Test message 2");
        notification2.setRead(false);
        notification2.setTime(LocalDateTime.now());

        notificationRepository.persist(notification1, notification2);
    }

    @Test
    @TestSecurity(user = "user1", roles = {"GUEST"})
    public void testMarkAsRead() {
        List<Notification> notifications = notificationRepository.findUnreadByRecipientId("user1");
        assertEquals(1, notifications.size());

        Notification notification = notifications.get(0);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParam("id", notification.getId().toString())
                .when().post("/notifications/markAsRead/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        Notification updatedNotification = notificationRepository.findById(notification.getId());
        assert updatedNotification.isRead();
    }

    @Test
    @TestSecurity(user = "user1", roles = {"GUEST"})
    public void testGetByUser() {
        RestAssured.given()
                .pathParam("id", "user1")
                .when().get("/notifications/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("$.size()", is(1))
                .body("[0].text", equalTo("Test message 1"));
    }
}
