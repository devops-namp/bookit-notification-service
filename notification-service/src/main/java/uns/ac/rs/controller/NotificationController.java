package uns.ac.rs.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import uns.ac.rs.entity.Notification;
import uns.ac.rs.service.NotificationService;

import java.util.List;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationController {

    @Inject
    NotificationService notificationService;

    @Incoming("notification-queue")
    public Response sendNotification(Notification notification) {
        System.out.println("STIGLA NOTIFIKACIJA");
        notificationService.sendNotification(notification);
        return Response.ok().build();
    }

    @POST
    @Path("markAsRead/{id}")
    @RolesAllowed({"HOST", "GUEST" })
    public Response markAsRead(@PathParam("id") String id) {
        notificationService.markAsRead(id);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"HOST", "GUEST" })
    public Response getByUser(@PathParam("id") String recipientId) {
        List<Notification> unreadNotifications = notificationService.findUnreadByRecipientId(recipientId);
        System.out.println(unreadNotifications.size());
        return Response.ok(unreadNotifications).build();
    }
}