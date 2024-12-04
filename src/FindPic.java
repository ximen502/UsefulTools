import java.io.File;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;

/**
 * 主要用于实现在一张大图中查找小图
 */
public class FindPic {
    
    private Rectangle screenRect;
    public final Robot robot;

    public FindPic() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            throw new RuntimeException(e);
        }
    }

    int[] find(String smallPath, String bigPath){
        return find(smallPath, bigPath, 0.85f);
    }

    int[] find(String smallPath, String bigPath, float similar){
        try {
            BufferedImage smallImg = ImageIO.read(new File(smallPath));
            BufferedImage bigImg = ImageIO.read(new File(bigPath));
            int[] result = findImageInFullScreen(smallImg, bigImg, similar);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    int[] find(String smallPath){
        return find(smallPath, 0.85f);
    }

    int[] find(String smallPath, float similar){
        try {
            BufferedImage smallImg = ImageIO.read(new File(smallPath));
            int[] result = findImageInFullScreen(smallImg, similar);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    int[] find(String smallPath, Rectangle rect, float similar) {
        try {
            BufferedImage smallImg = ImageIO.read(new File(smallPath));
            int[] result = findImageInFullScreen(smallImg, rect, similar);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    int[] find(BufferedImage targetImage){
        return find(targetImage, 0.85f);
    }

    int[] find(BufferedImage targetImage, float similar){
        return find(targetImage, similar);
    }

    int[] find(BufferedImage targetImage, BufferedImage bigImage){
        return findImageInFullScreen(targetImage, bigImage, 0.85f);
    }

    int[] find(BufferedImage targetImage, BufferedImage bigImage, float similar){
        return findImageInFullScreen(targetImage, bigImage, similar);
    }

    public int[] findImageInFullScreen(BufferedImage targetImage, BufferedImage bigImage, float similar) {
        //Robot robot = new Robot();
        int targetWidth = targetImage.getWidth();
        int targetHeight = targetImage.getHeight();
        int screenWidth = bigImage.getWidth();
        int screenHeight = bigImage.getHeight();

        for (int y = 0; y < screenHeight - targetHeight; y++) {
            for (int x = 0; x < screenWidth - targetWidth; x++) {
                boolean match = true;
                for (int ty = 0; ty < targetHeight; ty++) {
                    for (int tx = 0; tx < targetWidth; tx++) {
                        int screenPixel = bigImage.getRGB(x + tx, y + ty);
                        int targetPixel = targetImage.getRGB(tx, ty);
                        if (!isSimilarPixel(screenPixel, targetPixel, similar)) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) break;
                }
                if (match) return new int[]{x, y};
            }
        }
        return null;
    }

    private int[] findImageInFullScreen(BufferedImage targetImage, Rectangle rect, float similar) {
        //Robot robot = new Robot();
        BufferedImage screenshot = robot.createScreenCapture(rect);

        int targetWidth = targetImage.getWidth();
        int targetHeight = targetImage.getHeight();
        int screenWidth = screenshot.getWidth();
        int screenHeight = screenshot.getHeight();

        for (int y = 0; y < screenHeight - targetHeight; y++) {
            for (int x = 0; x < screenWidth - targetWidth; x++) {
                boolean match = true;
                for (int ty = 0; ty < targetHeight; ty++) {
                    for (int tx = 0; tx < targetWidth; tx++) {
                        int screenPixel = screenshot.getRGB(x + tx, y + ty);
                        int targetPixel = targetImage.getRGB(tx, ty);
                        if (!isSimilarPixel(screenPixel, targetPixel, similar)) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) break;
                }
                if (match) return new int[]{x, y};
            }
        }
        return null;
    }

    public int[] findImageInFullScreen(BufferedImage targetImage, float similar) {
        //Robot robot = new Robot();
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        if (screenRect == null) {
            screenRect = new Rectangle(screenSize);
        }
        
        // Rectangle screenRect = new Rectangle(558,155,800,630);
        BufferedImage screenshot = robot.createScreenCapture(screenRect);

        int targetWidth = targetImage.getWidth();
        int targetHeight = targetImage.getHeight();
        int screenWidth = screenshot.getWidth();
        int screenHeight = screenshot.getHeight();

        for (int y = 0; y < screenHeight - targetHeight; y++) {
            for (int x = 0; x < screenWidth - targetWidth; x++) {
                boolean match = true;
                for (int ty = 0; ty < targetHeight; ty++) {
                    for (int tx = 0; tx < targetWidth; tx++) {
                        int screenPixel = screenshot.getRGB(x + tx, y + ty);
                        int targetPixel = targetImage.getRGB(tx, ty);
                        if (!isSimilarPixel(screenPixel, targetPixel, similar)) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) break;
                }
                if (match) return new int[]{x, y};
            }
        }
        return null;
    }

    private boolean isSimilarPixel(int pixel1, int pixel2, float similar) {
        int r1 = (pixel1 >> 16) & 0xff;
        int g1 = (pixel1 >> 8) & 0xff;
        int b1 = pixel1 & 0xff;

        int r2 = (pixel2 >> 16) & 0xff;
        int g2 = (pixel2 >> 8) & 0xff;
        int b2 = pixel2 & 0xff;

        int diffR = Math.abs(r1 - r2);
        int diffG = Math.abs(g1 - g2);
        int diffB = Math.abs(b1 - b2);

        return diffR + diffG + diffB < (255 * 3) * (1 - similar);
    }
    
}