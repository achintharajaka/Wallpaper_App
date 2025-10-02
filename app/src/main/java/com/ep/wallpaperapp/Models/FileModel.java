package pop.wallpaper.uhd.Models;

import java.io.File;

public class FileModel {
    private File file;
    private String fileName;

    public FileModel(File file) {
        this.file = file;
        this.fileName = file.getName();
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }
}

