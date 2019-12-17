package tim.bts.inforazia.model;

public class Users_model {

    private String userId;
    private String namaUser;
    private String emailUser;
    private String photoUser;
    private String notif;
    private String lokasiNotif;

    public Users_model()
    {

    }

    public Users_model(String userId, String namaUser, String emailUser, String photoUser, String notif, String lokasiNotif) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.emailUser = emailUser;
        this.photoUser = photoUser;
        this.notif = notif;
        this.lokasiNotif = lokasiNotif;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPhotoUser() {
        return photoUser;
    }

    public void setPhotoUser(String photoUser) {
        this.photoUser = photoUser;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }

    public String getLokasiNotif() {
        return lokasiNotif;
    }

    public void setLokasiNotif(String lokasiNotif) {
        this.lokasiNotif = lokasiNotif;
    }
}
