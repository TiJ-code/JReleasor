package dk.tij.jreleasor.utils;

public class ReleaseGame {

    private String name;
    private String release_url;
    private String version;
    private String thumbnail_url;

    public ReleaseGame(String name, String release_url, String version, String thumbnail_url) {
        this.name = name;
        this.release_url = release_url;
        this.version = version;
        this.thumbnail_url = thumbnail_url;
    }

    public String getName() {
        return name;
    }

    public String getRelease_url() {
        return release_url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getThumbnailUrl() {
        return thumbnail_url;
    }
}
