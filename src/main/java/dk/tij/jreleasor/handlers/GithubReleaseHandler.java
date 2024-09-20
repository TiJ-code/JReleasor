package dk.tij.jreleasor.handlers;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.utils.ReleaseGame;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class GithubReleaseHandler extends Thread {

    private final JReleasor jReleasor;

    public GithubReleaseHandler() {
        this.jReleasor = JReleasor.instance;
    }

    @Override
    public void run() {
        try {
            while (JReleasor.instance.isRunning()) {
                OkHttpClient client = new OkHttpClient();
                for (ReleaseGame game : jReleasor.getReleaseGames()) {
                    Request request = new Request.Builder()
                            .url(game.getRelease_url())
                            .build();

                    Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    assert response.body() != null;
                    String html = response.body().string();

                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("h1.d-inline.mr-3");
                    for (Element element : elements) {
                        String onlineVersion = element.text().substring(1);
                        if (!game.getVersion().equals(element.text().substring(1))) {
                            System.out.println("NEW VERSION of " + game.getName());
                            JReleasor.instance.getReleaseNotificationHandler().CreateNotification(game, onlineVersion);
                        }
                    }
                }
                sleep(5 * 60 * 1000);
            }
        } catch (InterruptedException interruptedException) {
            System.err.println(interruptedException.getMessage());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
