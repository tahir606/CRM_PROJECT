package objects;

import JCode.CommonTasks;

public class Note {
    
    private int code, createdBy, contact, client,emailNo;
    private String text, createdOn, createdByName, contactName;
    
    public Note() {
    }



    @Override
    public String toString() {
        return "" + text + "        " + createdOn + "       " + createdByName;
    }

    public int getEmailNo() {
        return emailNo;
    }

    public void setEmailNo(int emailNo) {
        this.emailNo = emailNo;
    }

    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    public int getContact() {
        return contact;
    }
    
    public void setContact(int contact) {
        this.contact = contact;
    }
    
    public int getClient() {
        return client;
    }
    
    public void setClient(int client) {
        this.client = client;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(String createdOn) {
        this.createdOn = CommonTasks.getTimeFormatted(createdOn);
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
