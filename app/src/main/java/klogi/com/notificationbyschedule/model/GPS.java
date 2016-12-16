package klogi.com.notificationbyschedule.model;

/**
 * Created by alaeddine on 08/11/16.
 */
public class GPS {
    String longitude;
    String latitude;
    String releveHoraire;

    public GPS(String longitude, String latitude, String releveHoraire) {
        this.longitude = longitude;
        this.latitude = latitude;
    this.releveHoraire=releveHoraire;
    }

    public GPS() {

    }


    public String getReleveHoraire() {
        return releveHoraire;
    }

    public void setReleveHoraire(String releveHoraire) {
        this.releveHoraire = releveHoraire;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
