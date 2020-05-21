package si.uni_lj.fe.tnuv.hpm;

public class racuni {

    private String imeMeseca;
    private String cenaRacuna;
    private String zapadlost;
    private String razclenjenRacun;

    public racuni(String ze_placano) {
        this.ze_placano = ze_placano;
    }

    public String getZe_placano() {
        return ze_placano;
    }

    public void setZe_placano(String ze_placano) {
        this.ze_placano = ze_placano;
    }

    private String ze_placano;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public racuni(String imeMeseca, String cenaRacuna, String zapadlost, String razclenjenRacun, String ze_placano) {
        this.imeMeseca = imeMeseca;
        this.cenaRacuna = cenaRacuna;
        this.zapadlost = zapadlost;
        this.razclenjenRacun = razclenjenRacun;
        this.ze_placano = ze_placano;
        this.expandable = false;
    }

    public String getImeMeseca() {
        return imeMeseca;
    }

    public void setImeMeseca(String imeMeseca) {
        this.imeMeseca = imeMeseca;
    }

    public String getVersion() {
        return cenaRacuna;
    }

    public void setVersion(String version) {
        this.cenaRacuna = version;
    }

    public String getApiLevel() {
        return zapadlost;
    }

    public void setApiLevel(String apiLevel) {
        this.zapadlost = apiLevel;
    }

    public String getDescription() {
        return razclenjenRacun;
    }

    public void setDescription(String description) {
        this.razclenjenRacun = description;
    }

    @Override
    public String toString() {
        return "racuni{" +
                "imeMeseca='" + imeMeseca + '\'' +
                ", cenaRacuna='" + cenaRacuna + '\'' +
                ", zapadlost='" + zapadlost + '\'' +
                ", razclenjenRacun='" + razclenjenRacun + '\'' +
                ", zePlacano='" + ze_placano + '\'' +
                '}';
    }
}
