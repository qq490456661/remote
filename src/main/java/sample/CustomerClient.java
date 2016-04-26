package sample;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.zip.ZipInputStream;

/**
 * 客户端
 * Created by linjunjie(490456661@qq.com) on 2016/4/3.
 */
public class CustomerClient {


    public static void main(String []args){
        JFrame jFrame = new JFrame("客户端");
        jFrame.setSize(800,550);
        JLabel image_label = new JLabel();
        jFrame.add(image_label);
        jFrame.setVisible(true);
        jFrame.setAlwaysOnTop(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Socket serverSocket = null;
        BufferedImage image = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        ZipInputStream zipInputStream = null;
        long start_time = System.currentTimeMillis();
        try {
            //58.45.181.121

            serverSocket = new Socket("127.0.0.1",8888);
            dataInputStream = new DataInputStream(serverSocket.getInputStream());
            dataOutputStream = new DataOutputStream(serverSocket.getOutputStream());
            int count = 0;
            int len = 0;
            byte[] b = null;
            while(true) {
                while((len = dataInputStream.readInt()) <= 0){
                    System.out.println("正在等待服务端发送数据"+len);
                    Thread.sleep(500);
                }
                b = new byte[len];
                dataInputStream.readFully(b);//完全读出来
                System.out.println(len);
                image = ImageIO.read(new ByteArrayInputStream(b));
                image_label.setIcon(new ImageIcon(image));
                System.out.println(++count+"张图片 -->长度:" + len/1000.0 +"kb"+"  "+(System.currentTimeMillis()-start_time)/1000+"秒");
                dataOutputStream.writeInt(100);
                dataOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
                try {

                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

}
