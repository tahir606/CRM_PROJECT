package JCode;

import objects.ESetting;
import objects.Network;
import objects.Users;

import java.io.*;
import java.util.ArrayList;

public class FileHelper {

    private static final String FADD = "C:/Users/" + System.getProperty("user.name") + "/Bits/CRM/";
//    private static final String FADD = "../Bits/CRM/";
    private static final String FADD_ROOT = "settings/";
    public static final String FADD_DOCS = "docs/";

    //File Names
    private static final String DASHBOARD_PANELS = "dashPanels",
            SPLITPANE_DIVIDERS = "splitDividers",
            DASH_FILTERS = "dashFilters.txt",
            TICKET_NUMBER = "ticketNumber.txt",
            GENERAL_NUMBER = "generalNumber.txt";

    public FileHelper() {

    }

    public void makeFolders() {
        new Thread(() -> {
            if (!new File(FADD + "text.txt").exists()) {
                File file = new File(FADD);
                file.mkdirs();
            }
        }).start();
    }

    public void checkFolders() {
        File directoryAdmin = new File(FADD);
        if (!directoryAdmin.exists()) {
            directoryAdmin.mkdir();
        }

        //If settings folder does not exist create
        File directory = new File(FADD_ROOT);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public Network getNetworkDetails() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader = new InputStreamReader(
                    new FileInputStream(
                            new File(FADD + "network.txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();
            String[] t = text.split("\\^");
            return new Network(t[0], Integer.parseInt(t[1]));
        } catch (FileNotFoundException e) {
            System.out.println("Exception Network File Not Found : Line 63");
            System.out.println(e);
            return null;
        } catch (IOException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        } catch (NullPointerException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return null;
        } finally {
            try {
                isReader.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return null;
    }

    public boolean WriteNetwork(Network network) {

        String Details = network.getHost() + "^" + network.getPort();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new File(FADD + "network.txt"));

            writer.write(Details);

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }

    }


    public boolean WriteUserDetails(Users user, ArrayList<Users.uRights> rights) {

        String Details = user.getUCODE() + "*" + user.getFNAME() + "*" + user.getUNAME() + "*" + user.isEmail() + "^";

        for (Users.uRights right : rights) {
            Details = Details + right.getRCODE() + "*";
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new File(FADD + "uDets.txt"));

            writer.write(Details);

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }

    }

    public Users ReadUserDetails() {
        String text = "";
        InputStreamReader isReader = null;
        Users user = new Users();
        try {

            isReader =
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(FADD + "uDets.txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            if (text == null) {
                return null;
            }

            String[] t = text.split("\\^");
            String[] u = t[0].split("\\*");

            user.setUCODE(Integer.parseInt(u[0]));
            user.setFNAME(u[1]);
            user.setUNAME(u[2]);
            user.setEmailBool(Boolean.parseBoolean(u[3]));

            user.setRights(t[1].split("\\*"));

            return user;
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return null;
        } finally {
//            try {
//                isReader.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }
    }

    public void DeleteUserDetails() {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new File(FADD + "uDets.txt"));

            writer.write("");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public boolean WriteLastEmailNumber(int email_type, int emno) {

        PrintWriter writer = null;
        try {
            switch (email_type) {
                case 1: {
                    writer = new PrintWriter(
                            new File(FADD_ROOT + TICKET_NUMBER));
                    break;
                }
                case 2: {
                    writer = new PrintWriter(
                            new File(FADD_ROOT + GENERAL_NUMBER));
                    break;
                }
            }

            writer.write(String.valueOf(emno));

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }

    }

    public int ReadLastEmailNumber(int email_type) {
        String text = "";
        InputStreamReader isReader = null;
        try {
            switch (email_type) {
                case 1: {
                    isReader =
                            new InputStreamReader(
                                    new FileInputStream(
                                            new File(FADD_ROOT + TICKET_NUMBER)));
                    break;
                }
                case 2: {
                    isReader =
                            new InputStreamReader(
                                    new FileInputStream(
                                            new File(FADD_ROOT + GENERAL_NUMBER)));
                    break;
                }
            }

            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            if (text == null) {
                return 0;
            }

            return Integer.parseInt(text);
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return 0;
        } finally {
//            try {
//                isReader.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }
    }


    //-----------------Filters----------------
    public boolean WriteFilter(String where) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + "filters.txt"));

            writer.write(where);

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public String ReadFilter() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader =
                    new InputStreamReader(new FileInputStream(new File(FADD + "filters.txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            if (text == null) {
                return null;
            }

            return text;
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return null;
        } finally {
            try {
                isReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //-----------------Email Settings----------------
    public boolean WriteESettings(ESetting es) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + "eSet.txt"));

            writer.write(es.getHost() + "^" + es.getEmail() + "^" + es.getPass() + "^" + es.getFspath() + "^" + es
                    .isAuto() + "^" + es.isDisc());

            WriteAutoDisc(1, es);
            WriteAutoDisc(2, es);

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public ESetting ReadESettings() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader =
                    new InputStreamReader(new FileInputStream(new File(FADD + "eSet.txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            if (text == null) {
                return null;
            }

            String[] ar = text.split("\\^");

            ESetting es = new ESetting(ar[0], ar[1], ar[2], ar[3], Boolean.parseBoolean(ar[4]), Boolean.parseBoolean
                    (ar[5]));

            es.setAutotext(ReadAutoDisc(1).getAutotext());
            es.setDisctext(ReadAutoDisc(2).getDisctext());
            return es;
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return null;
        } finally {
            try {
                isReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean DeleteESettings() {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + "eSet.txt"));

            writer.write("");

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    //-----------------Auto/Disc Settings----------------
    public boolean WriteAutoDisc(int c, ESetting es) {

        String f = "";

        if (c == 1)
            f = "auto";
        else if (c == 2)
            f = "disc";


        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + f + ".txt"));

            if (c == 1)
                writer.write(es.getAutotext());
            else if (c == 2)
                writer.write(es.getDisctext());

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public ESetting ReadAutoDisc(int c) {

        String f = "";

        if (c == 1)
            f = "auto";
        else if (c == 2)
            f = "disc";

        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader =
                    new InputStreamReader(new FileInputStream(new File(FADD + f + ".txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            ESetting e = new ESetting();

            if (c == 1)
                e.setAutotext(text);
            else if (c == 2)
                e.setDisctext(text);

            return e;
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return null;
        } finally {
            try {
                isReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean DeleteAutoDisc(int c) {

        String f = "";

        if (c == 1)
            f = "auto";
        else if (c == 2)
            f = "disc";

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + f + ".txt"));

            writer.write("");

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    //-----------------Dashboard Panels----------------
    public boolean writeDashboardPanels(String[] dets) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + DASHBOARD_PANELS + ".txt"));

            for (String d : dets) {
                writer.write(d + "^");
            }

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public String[] readDashboardPanels() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader =
                    new InputStreamReader(new FileInputStream(new File(FADD + DASHBOARD_PANELS + ".txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            if (text == null) {
                return null;
            }

            String[] ar = text.split("\\^");

            return ar;
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return new String[]{"null", "null", "null", "null"};
        } finally {
            try {
                isReader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean deleteDashboardPanel() {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + DASHBOARD_PANELS + ".txt"));

            writer.write("");

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    //-----------------Split_panes divider----------------
    public boolean writeSplitPaneDiveders(String[] dividers) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + SPLITPANE_DIVIDERS + ".txt"));

            for (String d : dividers) {
                writer.write(d + "^");
            }

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public String[] readSplitPaneDividers() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader =
                    new InputStreamReader(new FileInputStream(new File(FADD + SPLITPANE_DIVIDERS + ".txt")));
            BufferedReader br = new BufferedReader(isReader);

            text = br.readLine();

            if (text == null) {
                return null;
            }

            String[] ar = text.split("\\^");

            return ar;
        } catch (Exception e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
            return new String[]{"0.5", "0.5", "0.5"};
        } finally {
            try {
                isReader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean deleteSplitPaneDividers() {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD + SPLITPANE_DIVIDERS + ".txt"));

            writer.write("");

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public boolean writeDashFilters(String filters) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new File(FADD_ROOT + DASH_FILTERS));

            writer.write(filters);

            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
    }

    public String readDashFilters() {
        String text = "";
        InputStreamReader isReader = null;
        try {
            isReader =
                    new InputStreamReader(new FileInputStream(new File(FADD_ROOT + DASH_FILTERS)));
            BufferedReader br = new BufferedReader(isReader);
            text = br.readLine();
            if (text == null) {
                return null;
            }
            return text;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Dash Filter Returning Empty : ");
            writeDashFilters("");
            return "";
        } finally {
            try {
                isReader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }

    public static void createDirectoryIfDoesNotExist(String path) {
        File directory = new File(path);
        if (!directory.exists()){
            directory.mkdir();
        }
    }

//    public static void saveToDocuments(String path) {
//
//        createDirectoryIfDoesNotExist(FADD_DOCS);
//
//        File source = new File(path);
//        File dest = new File(FADD_DOCS);
//        try {
//            FileUtils.copyDirectory(source, dest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public boolean deleteSplitPaneDividers() {
//
//        PrintWriter writer = null;
//
//        try {
//            writer = new PrintWriter(
//                    new File(FADD + SPLITPANE_DIVIDERS + ".txt"));
//
//            writer.write("");
//
//            return true;
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return false;
//        } finally {
//            writer.close();
//        }
//    }

}
