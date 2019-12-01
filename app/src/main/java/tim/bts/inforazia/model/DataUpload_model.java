package tim.bts.inforazia.model;

public class DataUpload_model {

    private String alamat;
    private String deskripsi;
    private String tanggal;
    private String waktu;
    private String mImageUrl;
    private String UID;
    private String namaUser;
    private String photoUrlUser;
    private String idUpload;


    public DataUpload_model() {

    }


    public DataUpload_model(String alamat,
                            String deskripsi,
                            String tanggal,
                            String waktu,
                            String mImageUrl,
                            String UID,
                            String namaUser,
                            String photoUrlUser,
                            String idUpload)
    {
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.mImageUrl = mImageUrl;
        this.UID = UID;
        this.namaUser = namaUser;
        this.photoUrlUser = photoUrlUser;
        this.idUpload = idUpload;
    }


    public String getIdUpload() {
        return idUpload;
    }

    public void setIdUpload(String idUpload) {
        this.idUpload = idUpload;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getPhotoUrlUser() {
        return photoUrlUser;
    }

    public void setPhotoUrlUser(String photoUrlUser) {
        this.photoUrlUser = photoUrlUser;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

}
