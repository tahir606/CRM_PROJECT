package JCode;


import Email.EmailDashController;
import JCode.mysql.EmailQueries;
import JCode.mysql.mySqlConn;
import JSockets.JServer;
import objects.Document;
import objects.ESetting;
import objects.Email;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class emailControl {

//    private static String HOST = "burhanisolutions.com.pk";
//    private static String EMAIL_ID = "sales@burhanisolutions.com.pk";
//    private static String EMAIL_PASS = "burhanisales";

//    private static String F_ADD = "C:/Users/" + System.getProperty("user.name") + "/Bits/CRM/Files/";
//    private static String FADD_FILE = "\\\\192.168.100.110\\g$\\Bits\\CRM\\";

    private static String F_ADD = "";
    private static mySqlConn sqlConn;
    private final static String TRUNCATED = "truncate";
    private static ESetting ESETTING;
    private FileHelper fHelper;
    private static String replacementKeyword;
    private static List<String> white_list, blackListKeyword;
    public static boolean EmailsNotSent = false;


    public emailControl() {
        fHelper = new FileHelper();
        sqlConn = new mySqlConn();
        ESETTING = sqlConn.getEmailSettings();
        white_list = sqlConn.getWhiteBlackListDomains(1);
        blackListKeyword = sqlConn.getBlackListKeyword();
        replacementKeyword = sqlConn.getReplacementKeyword();
//
//        blackListKey = String.join(",", blackListKeyword);
//        System.out.println("ok :"+blackListKey);
        if (ESETTING != null) {
            F_ADD = ESETTING.getFspath();
//            System.out.println(F_ADD);
            File f = new FileDev(F_ADD);
            f.mkdirs();
        }
    }

    public void receiveEmail() {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        trayHelper help = new trayHelper();
        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            //store.connect("imap.gmail.com", "tahirshakir606@gmail.com", "king786786");
//            System.out.println(ESETTING.getHost() + " " + ESETTING.getEmail() + " " + ESETTING.getPass());
            store.connect(ESETTING.getHost(), ESETTING.getEmail(), ESETTING.getPass());

            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);      //Read_Write Is Necessary For Marking Emails as Read.
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message messages[] = inbox.search(ft);

            String result;
            int i = 0;

            StoreData(messages);

            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            help.displayNotification("Error", "Mail Connect Exception");
        } catch (MessagingException e) {
            e.printStackTrace();
            help.displayNotification("Error", "Mail Connect Exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void StoreData(Message messages[]) {
        try {
            int i = -1;
            for (Message message : messages) {
                i++;
                storefile(message);
                message.setFlag(Flags.Flag.SEEN, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static String ATTACH = "";

    private static String storefile(Message message) throws Exception {
        Email email = new Email();

        String result = "";
        String SUBJECT;
        ATTACH = "";

        Address[] fromAddress = message.getFrom();
        Address[] toAddress = message.getRecipients(Message.RecipientType.TO);
        Address[] ccAddress = message.getRecipients(Message.RecipientType.CC);


        SUBJECT = message.getSubject();
        if (SUBJECT.equals(""))
            SUBJECT = "No Subject";

        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            result = parseMultipart((MimeMultipart) message.getContent(), fromAddress);
        }

        System.out.println(message.getSentDate());
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss.S");
        try {
            System.out.println(dt.format(message.getSentDate()));
        } catch (Exception e) {
            System.out.println(e); //Because some emails stop when reaching here
        }

        //-------------------------------------Creating Email--------------------------------
        email.setMsgNo(message.getMessageNumber());
        try {
            email.setTimestamp(dt.format(message.getSentDate()).substring(1));
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        email.setFromAddress(fromAddress);
        email.setToAddress(toAddress);
        email.setCcAddress(ccAddress);
        email.setSubject(SUBJECT);
        email.setBody(result);
        email.setAttch(ATTACH);
        email.setSolvFlag('N');
        email.setLockd(0);
        email.setFreze(false);
        System.out.println("store file: " + email);
        int tix = 1;    //2 means ticket 1 means general

        for (String t : white_list) {
            for (Address e : email.getFromAddress()) {
                if (e.toString().contains(t)) {
                    tix = 2;
                    System.out.println("Exists " + t);
                    break;
                }
            }
        }

        if (tix == 2) {
            sqlConn.insertEmail(email, message);
        } else if (tix == 1)
            sqlConn.insertEmailGeneral(email);

        trayHelper th = new trayHelper();
        th.displayNotification("New Email", "Email Received From: " + fromAddress[0].toString());

        EmailDashController.loadEmailsStatic();

        JServer.broadcastMessages(fromAddress[0].toString());   //Notify all client sockets

        return result;
    }

    private static String parseMultipart(MimeMultipart mimeMultipart, Address[] fromAddress) throws MessagingException, IOException {

        String result = "";

        int numberofparts = mimeMultipart.getCount();

        for (int partcounts = 0; partcounts < numberofparts; partcounts++) {
            MimeBodyPart part = (MimeBodyPart) mimeMultipart.getBodyPart(partcounts);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
                //Add Files in a folder created specifically for that email
                String folderName = "";
                try {
                    folderName = fromAddress[0].toString().trim().split("<")[1].replace(">", "");
                } catch (Exception e) {
                    folderName = "Others";
                }
                FileHelper.createDirectoryIfDoesNotExist(ESETTING.getFspath() + "\\" + folderName + "\\");
                String filename = ESETTING.getFspath() + "\\" + folderName + "\\" + part.getFileName();
                ATTACH += filename + "^";  //AttachFiles string is to be inserted into Database
                part.saveFile(filename);
            } else if (part.isMimeType("text/plain")) {
                result = result + "\n" + part.getContent();
//                break; // without break same text appears twice in my tests
            } else if (part.isMimeType("text/html")) {
                String html = (String) part.getContent();
                result = html;
//                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
//                System.out.println("After Parsing: " + result);
            } else if (part.isMimeType("multipart/*")) {
                result = parseMultipart((MimeMultipart) part.getContent(), fromAddress);
            } else {
                System.out.println("Else Part");
                System.out.println(part.getDisposition());
                System.out.println(part.getContent().toString());
            }
        }

        return result;
    }

    public static void sendEmail(Email email, Message messageReply) {

        String perDisc;

        if (email.getDisclaimer() == null) {
            email.setDisclaimer("");
        }

        if (sqlConn == null) {
            sqlConn = new mySqlConn();
        }

        if (ESETTING == null) {
            ESETTING = sqlConn.getEmailSettings();
        }

        if (ESETTING.isDisc())
            perDisc = email.getDisclaimer() + "\n" + ESETTING.getDisctext();
        else
            perDisc = email.getDisclaimer();

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", ESETTING.getHost());
        props.put("mail.smtp.port", "26");

        InternetAddress ia = null;
        try {
//            ia = new InternetAddress(ESETTING.getEmail());
            System.out.println(ESETTING.getGenerated_reply_email());
            ia = new InternetAddress(ESETTING.getGenerated_reply_email());
        } catch (AddressException e) {
            e.printStackTrace();
        }

        email.setFromAddress(new Address[]{ia});

        InternetAddress emailAddr;
        try {
            emailAddr = new InternetAddress(email.getFromAddress()[0].toString());
            emailAddr.validate();
        } catch (AddressException ex) {
            System.out.println("Invalid Email ID");
        }

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ESETTING.getEmail(), ESETTING.getPass());
                    }
                });
//        replacing keyword
        truncateBlacklistedKeywords(email);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(ESETTING.getGenerated_reply_email()));

            // Set Subject: header field

//            checkBlacklist(email,message);
//            comment for check noor domain
            message.setSubject(email.getSubject());

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            //  Create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            // Now set the actual message
            String b = email.getBody() + "<br><br>" + perDisc;
//            b = b.replace("\n","<br>");
            messageBodyPart.setText(b, "utf-8", "html");
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            if (email == null)
                return;

            String attach = ""; //String to save in the database
            if (email.getAttachments() == null) {
            } else if (!(email.getAttachments().size() < 0)) {
                // Part two is attachment
                for (File f : email.getAttachments()) {
                    BodyPart attachment = new MimeBodyPart();
                    if (f.exists()) {
                        DataSource source = new FileDataSource(f.getAbsolutePath());
                        attachment.setDataHandler(new DataHandler(source));
                        attachment.setFileName(f.getName());
                        attach = attach + f.getAbsolutePath() + "^";    //Concatenating String for Database
                    } else {
                        trayHelper.trayIcon.displayMessage("IOException", "File Not Found", TrayIcon.MessageType.ERROR);
                    }
                    multipart.addBodyPart(attachment);
                }
            }
            email.setAttch(attach);
            String upDocSt = ""; //String to save in the database
            if (email.getDocuments() == null) {
            } else if (!(email.getDocuments().size() < 0)) {
                // Part two is attachment
                for (Document d : email.getDocuments()) {
                    BodyPart attachment = new MimeBodyPart();
                    File f = d.getFile();
                    if (f.exists()) {
                        DataSource source = new FileDataSource(f.getAbsolutePath());
                        attachment.setDataHandler(new DataHandler(source));
                        attachment.setFileName(f.getName());
                        upDocSt = upDocSt + d.getName() + "^";    //Concatenating String for Database
                    } else {
                        trayHelper.trayIcon.displayMessage("IOException", "File Not Found", TrayIcon.MessageType.ERROR);
                    }
                    multipart.addBodyPart(attachment);
                }
            }
            email.setUploadedDocumentsString(upDocSt);

            // Send the complete message parts
            message.setContent(multipart);

            message.saveChanges();

            //message.setText(multipart);
            if (email.getToAddress() == null) { //Just to check if its null
            } else if (email.getToAddress().length > -1) {
                Address[] toAdd = email.getToAddress();
                for (int i = 0; i < toAdd.length; i++) {
                    if (toAdd[i] != null)
                        message.addRecipient(Message.RecipientType.TO, toAdd[i]);
                }
            }
            if (email.getCcAddress() == null) { //Just to check if its null
            } else if (email.getCcAddress().length > -1) {
                Address[] ccAdd = email.getCcAddress();
                for (int i = 0; i < ccAdd.length; i++) {
                    if (ccAdd[i] != null)
                        message.addRecipient(Message.RecipientType.CC, ccAdd[i]);
                }
            }
            if (email.getBccAddress() == null) { //Just to check if its null
            } else if (email.getBccAddress().length > -1) {
                Address[] bccAdd = email.getBccAddress();
                for (int i = 0; i < bccAdd.length; i++) {
                    if (bccAdd[i] != null)
                        message.addRecipient(Message.RecipientType.BCC, bccAdd[i]);
                }
            }

            //Put Message Reply
            if (messageReply != null) {
                message.setReplyTo(messageReply.getReplyTo());
            }

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(Calendar.getInstance().getTime());
            email.setTimestamp(timeStamp);
            new Thread(() -> {
                try {
                    Transport.send(message);
                    System.out.println("Sent E-Mail to: " + email.getToAddress()[0].toString());
//                    if (!email.isSent()) {
                    if (!message.getSubject().contains(EmailQueries.autoReplySubject)) {
                        email.setSent(true);
                        if (EmailDashController.Email_Type == 3) {
                            sqlConn.updateResendEmail(email); //update email_Sent table
                        } else {
                            sqlConn.insertEmailSent(email); //insert email_Sent table
                        }
                    }
//                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                    trayHelper tray = new trayHelper();
                    tray.displayNotification("Error", "Messaging Exception: Email Not Sent");
                    email.setSent(false);
                    sqlConn.insertEmailSent(email); //insert email_Sent table

                }
            }).start();

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    //  replacing keyword
    public static void truncateBlacklistedKeywords(Email email) {
        for (int i = 0; i < blackListKeyword.size(); i++) {
            String value = "(?i)" + blackListKeyword.get(i); // (?i) this key is used for ignoring case
            email.setSubject(email.getSubject().replaceAll(value, replacementKeyword)); // replaceAll will replace keyword
            email.setBody(email.getBody().replaceAll(value, replacementKeyword));
        }

    }

    public static boolean checkConnection() {
        HttpURLConnection connection = null;
        try {
            URL u = new URL("http://www.google.com/");
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            int code = connection.getResponseCode();
//            System.out.println("" + code);
            return true;
            // You can determine on HTTP return code received. 200 is success.
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("Unknown Host Exception");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }


}
