package objects;


import javax.mail.Address;

public class Email {

    private int EmailNo, msgNo, lockd;
    private Address[] toAddress, fromAddress, ccAddress, bccAddress;
    private String subject, timestamp, timeFormatted, body, attch, lockedByName, disclaimer, user;
    private char solvFlag, isAttch;
    private boolean freze;

    public Email() {

    }

    @Override
    public String toString() {

        return EmailNo + "\n" +
                fromAddress[0].toString() + "\n" +
                ((subject.length() > 20) ? subject.substring(0, 20) + "..." : subject);

//        return "Email{\n" +
//                "EmailNo=" + EmailNo +
//                "\n toAddress=" + Arrays.toString(toAddress) +
//                "\n fromAddress=" + Arrays.toString(fromAddress) +
//                "\n ccAddress=" + Arrays.toString(ccAddress) +
//                "\n subject='" + subject + '\'' +
//                "\n timestamp='" + timestamp + '\'' +
//                "\n body='" + body + '\'' +
//                "\n attch='" + attch + '\'' +
//                "\n solvFlag=" + solvFlag +
//                '}';
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
            s = s + "^" + ad;
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
                s = s + "^" + ad;
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

    public String getAttch() {
        return attch;
    }

    public void setAttch(String attch) {
        this.attch = attch;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isFreze() {
        return freze;
    }

    public void setFreze(boolean freze) {
        this.freze = freze;
    }
}
