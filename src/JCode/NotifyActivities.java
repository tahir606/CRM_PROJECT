package JCode;

import JCode.mysql.mySqlConn;
import objects.Email;
import objects.Event;
import objects.Task;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;

public class NotifyActivities {

    private mySqlConn sql;
    private trayHelper tray;
    private emailControl email;

    private final static String TASK_CAPTION = "Task Alert";
    private final static String EVENT_CAPTION = "Event Alert";

    public NotifyActivities() {
        sql = new mySqlConn();
        tray = new trayHelper();
        email = new emailControl();
    }

    public void startNotifying() {
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
        String currentDate = CommonTasks.getCurrentTimeStamp().split("\\s+")[0];
        //1- Notify tasks
        notifyTasks(currentDate);
        //2- Notify Events
        notifyEvents(currentDate);
    }

    private void notifyTasks(String currentDate) {
        //Get only todays task and ones that havent already been notified
        List<Task> openList = sql.getAllTasks(" AND TS_DDATE = '" + currentDate + "' AND NOTIFIED = 0 ");
        for (Task task : openList) {
            //1- Alert on desktop
            String body = "Subject: " + task.getSubject() + "\n";
            sendDesktopNotification(TASK_CAPTION, body);
            //2- Alert on Email
            body = body + "Description: " + task.getDesc() + "\n" +
                    "Created By: " + task.getCreatedBy() + "\n" +
                    "Created On: " + task.getCreatedOn() + "\n";
            sendEmailNotification(TASK_CAPTION, body, task.getCreatedByCode());
            //3- Mark task as notified
            sql.markNotified(task);
        }
    }

    private void notifyEvents(String currentDate) {
        //Get only todays events and ones that havent already been notified
        List<Event> openList = sql.getAllEvents(" AND ES_FROM >= '" + currentDate + "' AND '" + currentDate + "' <= ES_TO AND NOTIFIED = 0");
        for (Event event : openList) {
                //1- Alert on desktop
                String body = "Title: " + event.getTitle() + "\n";
                sendDesktopNotification(EVENT_CAPTION, body);
                //2- Alert on Email
                body = body + "Description: " + event.getDesc() + "\n" +
                        "Created By: " + event.getCreatedBy() + "\n" +
                        "Created On: " + event.getCreatedOn() + "\n";
                sendEmailNotification(EVENT_CAPTION, body, event.getCreatedByCode());
                //3- Mark task as notified
                sql.markNotified(event);
        }
    }

    private void sendDesktopNotification(String caption, String body) {
        tray.displayNotification(caption, body);
    }

    private void sendEmailNotification(String subject, String body, int createdBy) {
        String toAddress = sql.getUserDetails(createdBy).getEmail();
        Email e = new Email();
        e.setSubject(subject);
        e.setBody(body);
        try {
            e.setToAddress(new Address[]{new InternetAddress(toAddress)});
        } catch (AddressException e1) {
            e1.printStackTrace();
        }
        email.sendEmail(e, null);
    }


}
