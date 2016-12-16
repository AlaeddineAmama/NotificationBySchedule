package klogi.com.notificationbyschedule.model;



/**
 * Created by alaeddine on 12/04/16.
 */
public class Users {
    private int id;
    private String login;
    private String password;
    private String hourWork;
    private String freq;
    private String dateFin;
    private String modification;
    private String acquisition;

    public Users() {
    }

    public Users(int id, String login, String password, String hourWork, String freq, String dateFin, String modification) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.hourWork = hourWork;
        this.freq = freq;
        this.dateFin = dateFin;
        this.modification = modification;
    }

    public Users(String login, String password, String hourWork, String freq, String dateFin, String modification) {
        this.login = login;
        this.password = password;
        this.hourWork = hourWork;
        this.freq = freq;
        this.dateFin = dateFin;
        this.modification = modification;
    }

    public Users(String login, String password, String hourWork, String freq, String dateFin, String modification, String acquisition) {
        this.login = login;
        this.password = password;
        this.hourWork = hourWork;
        this.freq = freq;
        this.dateFin = dateFin;
        this.modification = modification;
        this.acquisition = acquisition;
    }

    public Users(int id, String login, String password, String hourWork, String freq, String dateFin, String modification, String acquisition) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.hourWork = hourWork;
        this.freq = freq;
        this.dateFin = dateFin;
        this.modification = modification;
        this.acquisition = acquisition;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", hourWork='" + hourWork + '\'' +
                ", freq='" + freq + '\'' +
                ", dateFin='" + dateFin + '\'' +
                ", modification='" + modification + '\'' +
                ", acquisition='" + acquisition + '\'' +
                '}';
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHourWork() {
        return hourWork;
    }

    public void setHourWork(String hourWork) {
        this.hourWork = hourWork;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(String acquisition) {
        this.acquisition = acquisition;
    }
}

