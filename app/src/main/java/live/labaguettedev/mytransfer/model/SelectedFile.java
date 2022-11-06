package live.labaguettedev.mytransfer.model;


public class SelectedFile {

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
