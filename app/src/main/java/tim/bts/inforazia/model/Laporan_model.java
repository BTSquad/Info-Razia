package tim.bts.inforazia.model;

public class Laporan_model {

    private String idUpload;

    public Laporan_model()
    {

    }

    public Laporan_model(String idUpload) {
        this.idUpload = idUpload;
    }

    public String getIdUpload() {
        return idUpload;
    }

    public void setIdUpload(String idUpload) {
        this.idUpload = idUpload;
    }
}
