package tim.bts.inforazia.notify;

public class Data {

    private String userLokasi;
    private String icon;
    private String body;
    private String kota;
    private String sented;

    public Data(){

    }

    public Data(String userLokasi, String icon, String body, String kota, String sented) {
        this.userLokasi = userLokasi;
        this.icon = icon;
        this.body = body;
        this.kota = kota;
        this.sented = sented;
    }

    public String getUser() {
        return userLokasi;
    }

    public void setUser(String user) {
        this.userLokasi = user;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return kota;
    }

    public void setTitle(String title) {
        this.kota = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
