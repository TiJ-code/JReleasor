package dk.tij.jreleasor.utils;

public class ReleaseGame {

    private String name;
    private String release_url;

    public ReleaseGame(String name, String release_url) {
        this.name = name;
        this.release_url = release_url;
    }

    public String getName() {
        return name;
    }

    public String getRelease_url() {
        return release_url;
    }

}
