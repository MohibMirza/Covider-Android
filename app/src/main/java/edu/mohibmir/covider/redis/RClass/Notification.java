package edu.mohibmir.covider.redis.RClass;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;

public class Notification {

    private String message;
    private String date;
    private List<String> userIds;
    private List<String> classIds;


    public Notification(String message, List<String> userIds, List<String> classIds) {
        this.message = message;
        this.userIds = userIds;
        this.classIds = classIds;
    }

    public void send() {
        for(String userId : userIds) {
            User user = new User(userId);
            user.addNotification(message);
        }

        for(String classId : classIds) {
            Class class_ = new Class(classId);
            class_.addNotification(message);
        }
    }
}
