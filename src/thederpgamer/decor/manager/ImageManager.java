package thederpgamer.decor.manager;

import api.utils.textures.StarLoaderTexture;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.decor.DerpsDecor;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/16/2021
 */
public class ImageManager {

  private static final ConcurrentHashMap<String, Sprite> imgCache = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, Sprite[]> animatedImgCache = new ConcurrentHashMap<>();
  private static final ConcurrentLinkedQueue<String> downloadingImages = new ConcurrentLinkedQueue<>();

  @Nullable
  public static Sprite getImage(String url) {
    Sprite bufferedImage = null;
    if (!url.endsWith(".gif")) bufferedImage = imgCache.get(url);
    if (bufferedImage == null) fetchImage(url);
    return bufferedImage;
  }

  @Nullable
  public static Sprite[] getAnimatedImage(String url) {
    Sprite[] frames = null;
    if (url.endsWith(".gif")) frames = animatedImgCache.get(url);
    if (frames == null) fetchAnimatedImage(url);
    return frames;
  }

  private static void fetchImage(final String url) {
    if (!downloadingImages.contains(url)) {
      try {
        downloadingImages.add(url);
        final int maxDim = ConfigManager.getMainConfig().getConfigurableInt("max-png-dim", 1024);
        final BufferedImage temp = fromURL(url);
        StarLoaderTexture.runOnGraphicsThread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  if (temp != null) {
                    BufferedImage bufferedImage = scaleImage(temp, maxDim);
                    Sprite sprite =
                        StarLoaderTexture.newSprite(
                            bufferedImage,
                            DerpsDecor.getInstance(),
                            url + "_" + System.currentTimeMillis());
                    sprite.setPositionCenter(false);
                    imgCache.put(url, sprite);
                  }
                } catch (Exception exception) {
                  exception.printStackTrace();
                }
              }
            });
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
    downloadingImages.remove(url);
  }

  private static void fetchAnimatedImage(final String url) {
    if (!downloadingImages.contains(url)) {
      try {
        downloadingImages.add(url);
        final int maxFrames =
            ConfigManager.getMainConfig().getConfigurableInt("max-gif-frames", 80);
        final int maxDim = ConfigManager.getMainConfig().getConfigurableInt("max-gif-dim", 512);
        StarLoaderTexture.runOnGraphicsThread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  final ArrayList<Sprite> frameList = new ArrayList<>();
                  final ImageReader reader = new GIFImageReader(new GIFImageReaderSpi());
                  reader.setInput(
                      ImageIO.createImageInputStream(Objects.requireNonNull(getImageStream(url))));
                  final int count = Math.min(reader.getNumImages(true), maxFrames);
                  for (int frameIndex = 0; frameIndex < count; frameIndex++) {
                    try {
                      final BufferedImage temp = reader.read(frameIndex);
                      try {
                        BufferedImage bufferedImage = scaleImage(temp, maxDim);
                        Sprite sprite =
                            StarLoaderTexture.newSprite(
                                bufferedImage,
                                DerpsDecor.getInstance(),
                                url + "_" + frameIndex + "_" + System.currentTimeMillis());
                        sprite.setPositionCenter(false);
                        frameList.add(sprite);
                      } catch (Exception ignored) {
                      }
                    } catch (IndexOutOfBoundsException | IOException exception) {
                      exception.printStackTrace();
                      break;
                    }
                  }

                  if (!frameList.isEmpty()) {
                    Sprite[] frames = new Sprite[frameList.size()];
                    for (int i = 0; i < frames.length; i++) frames[i] = frameList.get(i);
                    animatedImgCache.put(url, frames);
                  }
                } catch (Exception exception) {
                  exception.printStackTrace();
                }
              }
            });
      } catch (Exception ignored) {
      }
    }
    downloadingImages.remove(url);
  }

  private static BufferedImage fromURL(final String u) {
    final BufferedImage[] image = {null};
    new Runnable() {
      @Override
      public void run() {
        try {
          URL url = new URL(u);
          URLConnection urlConnection = url.openConnection();
          urlConnection.setRequestProperty("User-Agent", "NING/1.0");
          InputStream stream = urlConnection.getInputStream();
          image[0] = ImageIO.read(stream);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    }.run();
    int i = 15;
    while (i > 0) {
      if (image[0] != null) return image[0];
      else i--;
    }
    return null;
  }

  private static InputStream getImageStream(String u) {
    try {
      URL url = new URL(u);
      URLConnection urlConnection = url.openConnection();
      urlConnection.setRequestProperty("User-Agent", "NING/1.0");
      return urlConnection.getInputStream();
    } catch (IOException ignored) {
    }
    return null;
  }

  private static BufferedImage scaleImage(BufferedImage image, int maxDim) {
    try {
      BufferedImage resized =
          new BufferedImage(
              Math.min(maxDim, image.getWidth()),
              Math.min(maxDim, image.getHeight()),
              image.getType());
      Graphics2D g = resized.createGraphics();
      g.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(
          image,
          0,
          0,
          Math.min(maxDim, image.getWidth()),
          Math.min(maxDim, image.getHeight()),
          0,
          0,
          image.getWidth(),
          image.getHeight(),
          null);
      g.dispose();
      return resized;
    } catch (Exception ignored) {
    }
    return null;
  }
}
