package thederpgamer.decor.manager;

import api.utils.textures.StarLoaderTexture;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.DerpsDecor;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/16/2021
 */
public class ImageManager {

    private final static ConcurrentHashMap<String, Sprite> imgCache = new ConcurrentHashMap<>();
    private final static ConcurrentLinkedQueue<String> downloadingImages = new ConcurrentLinkedQueue<>();

    @Nullable
    public static Sprite getImage(String url) {
        Sprite bufferedImage = ImageManager.imgCache.get(url);
        if (bufferedImage != null) {
            return bufferedImage;
        } else {
            fetchImage(url);
            return null;
        }
    }

    private static void fetchImage(final String url) {
        if (!ImageManager.downloadingImages.contains(url)) {
            new Thread() {
                @Override
                public void run() {
                    ImageManager.downloadingImages.add(url);
                    final BufferedImage bufferedImage = fromURL(url);
                    StarLoaderTexture.runOnGraphicsThread(new Runnable() {
                        @Override
                        public void run() {
                            Sprite sprite = StarLoaderTexture.newSprite(bufferedImage, DerpsDecor.getInstance(), url + System.currentTimeMillis());
                            ImageManager.imgCache.put(url, sprite);
                        }
                    });
                    ImageManager.downloadingImages.remove(url);
                }
            }.start();
        }
    }

    private static BufferedImage fromURL(String u) {
        BufferedImage image = null;
        try {
            URL url = new URL(u);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "NING/1.0");
            InputStream stream = urlConnection.getInputStream();
            image = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
