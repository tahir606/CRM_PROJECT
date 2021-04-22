package objects;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class EmailProperty {
    private SimpleIntegerProperty email_No;
    private SimpleStringProperty timestamp, from_Address, subject, email_Body, lock_time, solve_Time,duration,average,user_name,remarks,typeOfWork;
    private boolean isEmailTypeSent = false;
    private List<EmailProperty> relatedEmails = new ArrayList<>();

    public EmailProperty() {
        email_No = new SimpleIntegerProperty();
        timestamp = new SimpleStringProperty();
        subject = new SimpleStringProperty();
        email_Body = new SimpleStringProperty();
        lock_time = new SimpleStringProperty();
        solve_Time = new SimpleStringProperty();
        from_Address = new SimpleStringProperty();
        duration =new SimpleStringProperty();
        average = new SimpleStringProperty();
        user_name = new SimpleStringProperty();
        remarks = new SimpleStringProperty();
        typeOfWork = new SimpleStringProperty();
    }

    public String getTypeOfWork() {
        return typeOfWork.get();
    }

    public SimpleStringProperty typeOfWorkProperty() {
        return typeOfWork;
    }

    public void setTypeOfWork(String typeOfWork) {
        this.typeOfWork.set(typeOfWork);
    }

    public String getRemarks() {
        return remarks.get();
    }

    public SimpleStringProperty remarksProperty() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks.set(remarks);
    }

    public String getAverage() {
        return average.get();
    }

    public SimpleStringProperty averageProperty() {
        return average;
    }

    public void setAverage(String average) {
        this.average.set(average);
    }

    public String getUser_name() {
        return user_name.get();
    }

    public SimpleStringProperty user_nameProperty() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name.set(user_name);
    }

    public String getDuration() {
        return duration.get();
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getFrom_Address() {
        return from_Address.get();
    }

    public SimpleStringProperty from_AddressProperty() {
        return from_Address;
    }

    public void setFrom_Address(String from_Address) {
        this.from_Address.set(from_Address);
    }

    public int getEmail_No() {
        return email_No.get();
    }

    public SimpleIntegerProperty email_NoProperty() {
        return email_No;
    }

    public void setEmail_No(int email_No) {
        this.email_No.set(email_No);
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public SimpleStringProperty timestampProperty() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp.set(timestamp);
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }


    public String getEmail_Body() {
        return email_Body.get();
    }

    public SimpleStringProperty email_BodyProperty() {
        return email_Body;
    }

    public void setEmail_Body(String email_Body) {
        this.email_Body.set(email_Body);
    }

    public String getLock_time() {
        return lock_time.get();
    }

    public SimpleStringProperty lock_timeProperty() {
        return lock_time;
    }

    public void setLock_time(String lock_time) {
        this.lock_time.set(lock_time);
    }

    public String getSolve_Time() {
        return solve_Time.get();
    }

    public SimpleStringProperty solve_TimeProperty() {
        return solve_Time;
    }

    public void setSolve_Time(String solve_Time) {
        this.solve_Time.set(solve_Time);
    }





    @Override
    public String toString() {

//      return from_Address[0].toString();
        String e = email_No + " - " ;

        if (!isEmailTypeSent) {
            e = e + from_Address;
        }
        e = e + "\n" +
                getTimestamp() + "\n" +
                ((subject) +
                ((relatedEmails.size() > 0) ? "\nAttached @: " + relatedEmails.size() : ""));
        return e;
    }

}
