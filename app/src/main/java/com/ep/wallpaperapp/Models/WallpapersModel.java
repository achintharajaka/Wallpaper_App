package pop.wallpaper.uhd.Models;

import java.io.Serializable;

public class WallpapersModel implements Serializable {
    String id, categoryid, title, image;

    public WallpapersModel() {
    }

    public WallpapersModel(String id, String categoryid, String title, String image) {
        this.id = id;
        this.categoryid = categoryid;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
