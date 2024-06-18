package uns.ac.rs.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
//import uns.ac.rs.controller.NotificationWebSocket;
import uns.ac.rs.controller.NotificationWebSocket;
import uns.ac.rs.entity.Notification;
import uns.ac.rs.repository.NotificationRepository;

import java.util.List;

@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    NotificationWebSocket notificationWebSocket;

    public void sendNotification(Notification notification) {
        notificationRepository.persist(notification);
        notificationWebSocket.sendNotification(notification.getRecipientId(), notification.toJson());
    }

    public void markAsRead(String id) {
    notificationRepository.markAsRead(id);
    }

    public List<Notification> findUnreadByRecipientId(String recipientId) {
        return notificationRepository.findUnreadByRecipientId(recipientId);
    }

}
