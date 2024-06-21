package uns.ac.rs.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
//import uns.ac.rs.controller.NotificationWebSocket;
import uns.ac.rs.controller.NotificationWebSocket;
import uns.ac.rs.entity.Notification;
import uns.ac.rs.repository.NotificationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class NotificationServiceTest {

    @InjectMock
    NotificationRepository notificationRepository;

    @InjectMock
    NotificationWebSocket notificationWebSocket;

    @Inject
    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendNotification() throws InterruptedException {
        Notification notification = new Notification();
        notification.setId(new ObjectId());
        notification.setRecipientId("recipient123");

        notificationService.handleNotification(notification);

        verify(notificationRepository).persist(notification);
        verify(notificationWebSocket).sendNotification(eq("recipient123"), anyString());
    }

    @Test
    public void testMarkAsRead() {
        String id = new ObjectId().toHexString();

        doNothing().when(notificationRepository).markAsRead(id);

        notificationService.markAsRead(id);

        verify(notificationRepository).markAsRead(id);
    }

    @Test
    public void testFindUnreadByRecipientId() {
        String recipientId = "recipient123";
        Notification notification = new Notification();
        notification.setId(new ObjectId());
        notification.setRecipientId(recipientId);
        notification.setRead(false);

        when(notificationRepository.findUnreadByRecipientId(recipientId)).thenReturn(List.of(notification));

        List<Notification> unreadNotifications = notificationService.findUnreadByRecipientId(recipientId);

        assertNotNull(unreadNotifications);
        assertEquals(1, unreadNotifications.size());
        assertEquals(notification, unreadNotifications.get(0));

        verify(notificationRepository).findUnreadByRecipientId(recipientId);
    }
}