package uns.ac.rs.exception;

public class NotificationNotFoundException extends GenericException{

    public NotificationNotFoundException() {
        super("Notification does not exist");
    }

    @Override
    public int getErrorCode() {
        return 404;
    }
}
