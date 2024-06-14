package uns.ac.rs.repository;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uns.ac.rs.entity.Notification;
import uns.ac.rs.exception.NotificationNotFoundException;
import uns.ac.rs.resources.MongoDBResource;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(value =  MongoDBResource.class, restrictToAnnotatedClass = true)
public class NotificationRepositoryTest {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    MongoClient mongoClient;

    @BeforeEach
    public void setUp() {
        // Ensure the collection exists before any transactional operations
        MongoDatabase database = mongoClient.getDatabase("testdatabase");
        if (!database.listCollectionNames().into(new ArrayList<>()).contains("Notification")) {
            database.createCollection("Notification");
        }

        notificationRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testMarkAsRead() {
        Notification notification = new Notification();
        notification.setId(new ObjectId());
        notification.setRecipientId("recipient123");
        notification.setRead(false);

        notificationRepository.persist(notification);

        notificationRepository.markAsRead(notification.getId().toHexString());

        Notification updatedNotification = notificationRepository.findById(notification.getId());
        assertNotNull(updatedNotification);
        assertTrue(updatedNotification.isRead());
    }

    @Test
    @Transactional
    public void testMarkAsReadThrowsException() {
        String invalidId = new ObjectId().toHexString();

        assertThrows(NotificationNotFoundException.class, () -> {
            notificationRepository.markAsRead(invalidId);
        });
    }

    @Test
    @Transactional
    public void testFindUnreadByRecipientId() {
        Notification notification = new Notification();
        ObjectId id = new ObjectId();
        notification.setId(id);
        notification.setRecipientId("recipient123");
        notification.setRead(false);

        notificationRepository.persist(notification);

        List<Notification> unreadNotifications = notificationRepository.findUnreadByRecipientId("recipient123");
        assertNotNull(unreadNotifications);
        assertEquals(1, unreadNotifications.size());
        assertEquals(id, unreadNotifications.get(0).getId());
    }

    @Test
    @Transactional
    public void testListAll() {
        Notification notification1 = new Notification();
        notification1.setId(new ObjectId());
        notificationRepository.persist(notification1);

        Notification notification2 = new Notification();
        notification2.setId(new ObjectId());
        notificationRepository.persist(notification2);

        List<Notification> notifications = notificationRepository.listAll();
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
    }

    @Test
    @Transactional
    public void testDelete() {
        Notification notification = new Notification();
        ObjectId id = new ObjectId();
        notification.setId(id);
        notificationRepository.persist(notification);

        notificationRepository.delete(notification);
        Notification foundNotification = notificationRepository.findById(id);
        assertNull(foundNotification);
    }
}
