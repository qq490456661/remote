package sample;

import net.coobird.thumbnailator.Thumbnails;
import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 服务端
 * Created by linjunjie(490456661@qq.com) on 2016/4/2.
 */
public class Screen {


    public static void main(String[]args){

        int WIDTH=0,HEIGHT=0;
        Robot robot = null;
        BufferedImage image = null;
        Dimension dimension = null;
        try {
            //获取屏幕大小
            dimension = Toolkit.getDefaultToolkit().getScreenSize();
            WIDTH=(int)dimension.getWidth();
            HEIGHT=(int)dimension.getHeight();
            robot = new Robot();
        }catch(Exception e){
            e.printStackTrace();
        }
        Socket socket = null;
        try {
            //-------------创建服务器端口
            ServerSocket serverSocket = new ServerSocket(8888);
            //阻断
            socket = serverSocket.accept();
            //初始化变量
            boolean writeToOs = false;
            ByteArrayOutputStream byteArrayOutputStream = null;//压缩前
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            int count = 0; //统计一秒中帧数
            byte[] b = null;
            int len = 0;
            int now_width = WIDTH/3*2, now_height = HEIGHT/3*2;
            int millis = 0;
            Long testStartTime = System.currentTimeMillis();//开始时间
            Long testEndTime = testStartTime;//结束时间
            while(true) {
                testEndTime = System.currentTimeMillis();
                if((testEndTime - testStartTime)/1000 >= 1){
                    System.out.println("1秒钟生成的图片数量:"+count);
                    testStartTime = System.currentTimeMillis();
                    count = 0;
                }
                image = robot.createScreenCapture(new Rectangle(0, 0, (int) WIDTH, (int) HEIGHT));
                image = Thumbnails.of(image).size(now_width,now_height).outputFormat("jpeg").outputQuality(0.1).asBufferedImage();
                byteArrayOutputStream  = new ByteArrayOutputStream();

                writeToOs = ImageIO.write(image, "jpg", byteArrayOutputStream);
                if (writeToOs) {
                    b = byteArrayOutputStream.toByteArray();
                    System.out.println("大小:" + b.length/1000.0 +"kb");
                    dataOutputStream.writeInt(b.length);
                    dataOutputStream.write(b);
                    dataOutputStream.flush();
                }
                while((len = dataInputStream.readInt()) != 100){
                    System.out.println("等待客户端响应"+len);
                    Thread.sleep(500);
                    millis += 500;
                }
                count ++ ;//生成了一张图片
                millis = 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(socket!=null&&!socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
