import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import javax.imageio.ImageIO;

public class TP {
    public final Robot robot;

    public TP() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            throw new RuntimeException(e);
        }
    }

    void clickL(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    void copy(String text){
        write2ClipBrd(text);
    }

    void delay(int ms) {
        robot.delay(ms);
    }

    //写入字符串到剪贴板
    void write2ClipBrd(String str) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable ts = new StringSelection(str);
        cb.setContents(ts, null);
    }

    //读取剪贴板内容
    String readFromClipBrd() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable ts = cb.getContents(null);
        if (ts != null) {
            if (ts.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String str = (String) ts.getTransferData(DataFlavor.stringFlavor);
                    return str;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e);
                }
            }
        }
        return "empty--";
    }

    void left(){
        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
    }

    void right(){
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.keyRelease(KeyEvent.VK_RIGHT);
    }

    void up(){
        robot.keyPress(KeyEvent.VK_UP);
        robot.keyRelease(KeyEvent.VK_UP);
    }

    void down(){
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);
    }

    void ctrlC(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    void ctrlV(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    void ctrlA(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    void ctrlF(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    void tab(){
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
    }

    void enter(){
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    void backspace(){
        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
    }

    void esc() {
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }

    void home() {
        robot.keyPress(KeyEvent.VK_HOME);
        robot.keyRelease(KeyEvent.VK_HOME);
    }

    void end() {
        robot.keyPress(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_END);
    }

    void delete() {
        robot.keyPress(KeyEvent.VK_DELETE);
        robot.keyRelease(KeyEvent.VK_DELETE);
    }

    void shiftPress() {
        robot.keyPress(KeyEvent.VK_SHIFT);
    }

    void shiftRelease() {
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    void altTab() {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    String getName(String path){
        return getName(path, true);
    }

    String getName(String path, boolean withSuffix){
        int index = path.lastIndexOf("\\");
        int dotIndex = path.lastIndexOf(".");
        if (withSuffix) {
            return path.substring(index+1);
        } else {
            return path.substring(index+1, dotIndex);
        }
    }

    String getUrlName(String path){
        return getUrlName(path, true);
    }

    String getUrlName(String path, boolean withSuffix){
        int index = path.lastIndexOf("/");
        int dotIndex = path.lastIndexOf(".");
        if (withSuffix) {
            return path.substring(index+1);
        } else {
            return path.substring(index+1, dotIndex);
        }
    }

    String readTxt(String path){
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine())!= null) {
                sb.append(line);
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e);
        }

        return sb.toString();
    }

    void writeTxt(String path, String content){
        try {
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e);
        }
    }

    BufferedImage screenShot(String path){
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        
        BufferedImage screenshot = robot.createScreenCapture(screenRect);

        if (path!= null && !path.isEmpty()) {
            try {
                ImageIO.write(screenshot, "png", new File(path));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error: " + e);
            }
        }

        return screenshot;
    }

    BufferedImage screenShot(int x, int y, int w, int h, String path){
        Rectangle screenRect = new Rectangle(x,y,w,h);
        BufferedImage screenshot = robot.createScreenCapture(screenRect);

        if (path!= null && !path.isEmpty()) {
            try {
                ImageIO.write(screenshot, "png", new File(path));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error: " + e);
            }
        }

        return screenshot;
    }
}