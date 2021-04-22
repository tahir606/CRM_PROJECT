package JCode;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CommonTasks {

    public CommonTasks() {
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static String getTimeFormatted(String timeStamp) {
        // Note, MM is months, not mm
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

        Date date = null;
        try {
            date = inputFormat.parse(timeStamp);
        } catch (ParseException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println(e);
            return null;
        }
        String outputText = outputFormat.format(date);
        return outputText;
    }

    public static String getDateFormatted(String timeStamp) {
        // Note, MM is months, not mm
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(timeStamp);
        } catch (ParseException e) {
            return "";
        } catch (NullPointerException e) {
            return "";
        }
        String outputText = outputFormat.format(date);
        return outputText;
    }

    public static String getDateFormattedOnly(String timeStamp) {
        // Note, MM is months, not mm
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(timeStamp);
        } catch (ParseException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
        String outputText = outputFormat.format(date);
        return outputText;
    }

    public static String getAge(String timeStamp) {

        // Note, MM is months, not mm
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        Date date = null;
        try {
            date = inputFormat.parse(timeStamp);
        } catch (ParseException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        } catch (NullPointerException e) {
            return "";
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        //Add one to month {0 - 11}
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate birthdate = LocalDate.of(year, month, day);
        LocalDate now = LocalDate.now();

        return String.valueOf(Period.between(birthdate, now).getYears());

    }

    /**
     * Get a diff between two dates
     *
     * @param d1       the oldest date
     * @param d2       the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static String getDateDiff(String d1, String d2, TimeUnit timeUnit) {

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = inputFormat.parse(d1);
            date2 = inputFormat.parse(d2);
        } catch (ParseException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        } catch (NullPointerException e) {
            return "";
        }

        long diffInMillies = date2.getTime() - date1.getTime();
        long diffInMin = timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return timeConvert(diffInMin);
    }

    //Convert Time in minutes to:
    private static String timeConvert(long time) {
        // Days + Hours + Min
//        return time/24/60 + " Day(s) " + time/60%24 + " Hour(s) " + time%60 + " Min(s) ";
        // Hours + Min
        return time / 60 + " hr " + time % 60 + " min";
    }

    public static LocalDate createLocalDate(String timeStamp) {

//        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inputFormat.parse(timeStamp);
        } catch (ParseException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }

        Calendar calendar = new GregorianCalendar();
        if (date == null)
            return null;
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        //Add one to month {0 - 11}
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate localDate = LocalDate.of(year, month, day);

        return localDate;
    }

    public static LocalTime createLocalTime(String timeStamp) {
        LocalTime localTime = LocalTime.of(Integer.parseInt(timeStamp.split(":")[0]), Integer.parseInt(timeStamp.split(":")[1]));
        return localTime;
    }

    public static void inflateDialog(String title, URL path) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            trayHelper tray = new trayHelper();
            tray.createIcon(stage);
            Platform.setImplicitExit(true);
            stage.show();
        } catch (IOException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }

    public static void inflateDialog(String title, String path) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CommonTasks.class.getResource(path));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            trayHelper tray = new trayHelper();
            tray.createIcon(stage);
            Platform.setImplicitExit(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadInPane(BorderPane pane, String path) {
        try {
            pane.setCenter(
                    FXMLLoader.load(
                            CommonTasks.class.getClassLoader().getResource(path)));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static String getTimeDuration(String locktime, String solvtime) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date date1 = null;
        Date date2 = null;
        long diff = 0;
        String str;
        if (solvtime == null || locktime == null) {
            str = "";
        } else {
            date1 = inputFormat.parse(locktime);
            date2 = inputFormat.parse(solvtime);
            diff = date2.getTime() - date1.getTime();
            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            int diffHours = (int) (diff / (60 * 60 * 1000));
            int hours = diffHours - (24 * diffDays);

            int diffMin = (int) (diff / (60 * 1000));
            int min = diffMin - (diffHours * 60);
            str = (diffDays + " Days " + hours + " Hours " + min + " Minutes ");
        }
        return str;

    }

}
