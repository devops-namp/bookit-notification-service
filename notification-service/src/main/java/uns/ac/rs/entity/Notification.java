package uns.ac.rs.entity;


import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@MongoEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private ObjectId id;
    private String text;
    private String recipientId;
    private boolean read;
    private LocalDateTime time;

    public Notification(NotificationEvent event) {
        this.text = event.getText();
        this.recipientId = event.getRecipientId();
        this.read = false;
        this.time = LocalDateTime.now();
    }

    public String toJson() {
        String timeString = (time != null) ? time.toString() : "";
        return "{" +
                "\"id\": \"" + id.toHexString() + "\"," +
                "\"text\": \"" + text + "\"," +
                "\"recipientId\": \"" + recipientId + "\"," +
                "\"read\": " + read + "," +
                "\"time\": \"" + timeString + "\"" +
                "}";
    }

}
