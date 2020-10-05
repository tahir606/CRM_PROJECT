package objects;


import javax.mail.Address;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Email {

    private int EmailNo, msgNo, lockd, solvBy, type, emailStoreNo, manual;
    private Address[] toAddress, fromAddress, ccAddress, bccAddress;
    private String subject, timestamp, timeFormatted, body, attch, uploadedDocumentsString, lockedByName, disclaimer, user, solvByName, createdBy, lockTime, solveTime;
    private List<File> attachments;
    private List<Document> documents;
    private List<ContactProperty> relatedContacts;
    private List<ClientProperty> relatedClients;
    private List<Email> relatedEmails = new ArrayList<>();
    private char solvFlag, isAttch;
    private boolean isSent, freze, isEmailTypeSent = false;
    private String rawContent;
    private List<Note> notes;



    public Email() {
    }

    @Override
    public String toString() {

        String e = EmailNo + " - ";

        if (!isEmailTypeSent) {
            e = e + fromAddress[0].toString();
        } else {
            e = e + toAddress[0].toString();
        }

        e = e + "\n" +
                getTimeFormatted() + "\n" +
                ((subject.length() > 20) ? subject.substring(0, 20) + "..." : subject) +
                ((relatedEmails.size() > 0) ? "\nAttached @: " + relatedEmails.size() : "");
        return e;
    }

    public int getEmailNo() {
        return EmailNo;
    }

    public void setEmailNo(int emailNo) {
        EmailNo = emailNo;
    }

    public int getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(int msgNo) {
        this.msgNo = msgNo;
    }

    public Address[] getToAddress() {
        return toAddress;
    }

    public String getToAddressString() {
        String s = "";
        for (Address ad : toAddress) {
            if(ad !=null) // my change
                s = s + "^" + ad;
        }
        return s;
    }

    public void setToAddress(Address[] toAddress) {
        this.toAddress = toAddress;
    }

    public Address[] getFromAddress() {
        return fromAddress;
    }

    public String getFromAddressString() {
        String s = "";
        for (Address ad : fromAddress) {
            if (ad != null)
                s = s + "^" + ad;
        }
        return s;
    }

    //
    public String getFromAddressCommaString() {
        String s = "";
        for (Address ad : fromAddress) {
            if (ad != null)
                s = s + ad + ",";
        }
        return s;
    }

    public void setFromAddress(Address[] fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Address[] getCcAddress() {
        return ccAddress;
    }

    public String getCcAddressString() {
        String s = "";
        try {
            for (Address ad : ccAddress) {
                if (ad != null)
                    s = s + "^" + ad;
            }
        } catch (NullPointerException e) {
            return "";
        }
        return s;
    }

    public String getCcAddressCommaString() {
        String s = "";
        try {
            for (Address ad : ccAddress) {
                if (ad != null)
                    s = s + ad + ",";
            }
        } catch (NullPointerException e) {
            return "";
        }
        return s;
    }

    public void setCcAddress(Address[] ccAddress) {
        this.ccAddress = ccAddress;
    }

    public Address[] getBccAddress() {
        return bccAddress;
    }

    public String getBccAddressString() {
        String s = "";
        try {
            for (Address ad : bccAddress) {
                s = s + "^" + ad;
            }
        } catch (NullPointerException e) {
            return "";
        }
        return s;
    }

    public void setBccAddress(Address[] bccAddress) {
        this.bccAddress = bccAddress;
    }

    public List<ContactProperty> getRelatedContacts() {
        return relatedContacts;
    }

    public void setRelatedContacts(List<ContactProperty> relatedContacts) {
        this.relatedContacts = relatedContacts;
    }

    public List<ClientProperty> getRelatedClients() {
        return relatedClients;
    }

    public void setRelatedClients(List<ClientProperty> relatedClients) {
        this.relatedClients = relatedClients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeFormatted() {
        return timeFormatted;
    }

    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getAttch() {
        return attch;
    }

    public void setAttch(String attch) {
        this.attch = attch;
    }

    public String getUploadedDocumentsString() {
        return uploadedDocumentsString;
    }

    public void setUploadedDocumentsString(String uploadedDocumentsString) {
        this.uploadedDocumentsString = uploadedDocumentsString;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<File> attachments) {
        this.attachments = attachments;
    }

    public char getIsAttch() {
        return isAttch;
    }

    public void setIsAttch(char isAttch) {
        this.isAttch = isAttch;
    }

    public char getSolvFlag() {
        return solvFlag;
    }

    public void setSolvFlag(char solvFlag) {
        this.solvFlag = solvFlag;
    }

    public int getLockd() {
        return lockd;
    }

    public void setLockd(int lockd) {
        this.lockd = lockd;
    }

    public String getLockedByName() {
        return lockedByName;
    }

    public void setLockedByName(String lockedByName) {
        this.lockedByName = lockedByName;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getSolveTime() {
        return solveTime;
    }

    public void setSolveTime(String solveTime) {
        this.solveTime = solveTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getManual() {
        return manual;
    }

    public void setManual(int manual) {
        this.manual = manual;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getSolvBy() {
        return solvBy;
    }

    public void setSolvBy(int solvBy) {
        this.solvBy = solvBy;
    }

    public String getSolvByName() {
        return solvByName;
    }

    public void setSolvByName(String solvByName) {
        this.solvByName = solvByName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEmailStoreNo() {
        return emailStoreNo;
    }

    public void setEmailStoreNo(int emailStoreNo) {
        this.emailStoreNo = emailStoreNo;
    }

    public List<Email> getRelatedEmails() {
        return relatedEmails;
    }

    public void setRelatedEmails(List<Email> relatedEmails) {
        this.relatedEmails = relatedEmails;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isEmailTypeSent() {
        return isEmailTypeSent;
    }

    public void setEmailTypeSent(boolean emailTypeSent) {
        isEmailTypeSent = emailTypeSent;
    }

    public boolean isFreze() {
        return freze;
    }

    public void setFreze(boolean freze) {
        this.freze = freze;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

}
