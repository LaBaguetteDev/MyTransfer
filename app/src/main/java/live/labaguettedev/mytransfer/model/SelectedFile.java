package live.labaguettedev.mytransfer.model;


import java.io.Serializable;

public class SelectedFile implements Serializable {

    private String path;
    private String name;
    private double size;

    public SelectedFile(String path, String name, double size) {
        this.path = path;
        this.name = name;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public double getSize() {
        return size;
    }


}
