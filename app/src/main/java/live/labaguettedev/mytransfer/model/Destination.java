package live.labaguettedev.mytransfer.model;

public class Destination {

    private final String ip;
    private final String name;
    private final String type;

    public Destination(String ip, String name, String type) {
        this.ip = ip;
        this.name = name;
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
