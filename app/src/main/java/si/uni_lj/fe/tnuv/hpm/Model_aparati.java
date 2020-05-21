package si.uni_lj.fe.tnuv.hpm;

import java.sql.Time;

public class Model_aparati {
    private int image;
    private String title;
    private String value;

    public Model_aparati(int image, String title, String value) {
        this.image = image;
        this.title = title;
        this.value = value;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
