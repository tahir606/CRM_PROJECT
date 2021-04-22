package objects;

import java.util.ArrayList;

public class Users {

    private int UCODE, solved, locked;
    private String UNAME;
    private String FNAME, Email, Password, uright;
    private ArrayList<uRights> uRightsList;
    private boolean freeze, isEmail;

    public Users() {
        uRightsList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return FNAME;
    }

    public int getUCODE() {
        return UCODE;
    }

    public void setUCODE(int UCODE) {
        this.UCODE = UCODE;
    }

    public String getUNAME() {
        return UNAME;
    }

    public void setUNAME(String UNAME) {
        this.UNAME = UNAME;
    }

    public String getFNAME() {
        return FNAME;
    }

    public void setFNAME(String FNAME) {
        this.FNAME = FNAME;
    }

    public ArrayList<uRights> getRights() {
        return uRightsList;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setRights(String[] rights) {
        for (int i = 0; i < rights.length; i++) {
            if (rights[i] != "") {
                String[] cn = rights[i].split("-");
                uRights right = new uRights(cn[0], null);
                uRightsList.add(right);
            }
        }
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public boolean isEmail() {
        return isEmail;
    }

    public void setEmailBool(boolean email) {
        isEmail = email;
    }

    public String getUright() {
        return uright;
    }

    public void setUright(String uright) {
        this.uright = uright;
    }

    public ArrayList<uRights> getuRightsList() {
        return uRightsList;
    }

    public void setuRightsList(ArrayList<uRights> uRightsList) {
        this.uRightsList = uRightsList;
    }

    public static class uRights {
        private int RCODE;
        private String RNAME;

        public uRights(int RCODE, String RNAME) {
            this.RCODE = RCODE;
            this.RNAME = RNAME;
        }

        public uRights(String RCODE, String RNAME) { //If RCODE is String
            this.RCODE = Integer.parseInt(RCODE);
            this.RNAME = RNAME;
        }

        public uRights() {
        }



        public int getRCODE() {
            return RCODE;
        }

        public void setRCODE(int RCODE) {
            this.RCODE = RCODE;
        }

        public String getRNAME() {
            return RNAME;
        }

        public void setRNAME(String RNAME) {
            this.RNAME = RNAME;
        }
    }

}
