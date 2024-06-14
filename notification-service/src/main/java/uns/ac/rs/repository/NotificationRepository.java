package uns.ac.rs.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import uns.ac.rs.entity.Notification;
import uns.ac.rs.exception.NotificationNotFoundException;

import java.util.List;

@ApplicationScoped
public class NotificationRepository implements PanacheMongoRepository<Notification> {

    public void markAsRead(String id) {
        Notification notification = findByIdOptional(new ObjectId(id))
                .orElseThrow(NotificationNotFoundException::new);

        notification.setRead(true);
        update(notification);
    }

    public List<Notification> findUnreadByRecipientId(String recipientId) {
        return find("recipientId = ?1 and read = ?2", recipientId, false).list();
    }
}
