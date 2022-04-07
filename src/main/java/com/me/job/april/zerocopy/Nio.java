package com.me.job.april.zerocopy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Nio零拷贝
 */
public class Nio {
    public static void main(String[] args) throws Exception {
        String destinationFile = "dest.txt";
        FileChannel outputChannel = new FileOutputStream(destinationFile).getChannel();
        // 文件大小为300MB
        String filename = "src.txt";
        //得到一个文件channel
        FileChannel inputChannel = new FileInputStream(filename).getChannel();
        //准备发送
        long startTime = System.currentTimeMillis();
        // 在linux下，一次transferTo 就可以完成传输
        // transferTo 底层使用到零拷贝
        // long transferCount = fileChannel.transferTo(0, fileChannel.size(), outputChannel);

        //在windows下，一次transferTo 只能发送8m, 就需要分段传输文件
        long total = 0;
        // 读取位置
        long position = 0;
        while (true) {
            long transferCount = inputChannel.transferTo(position, inputChannel.size(), outputChannel);
            if (transferCount > 0) {
                position = position + transferCount;
                total = total + transferCount;
            } else {
                break;
            }
        }

        System.out.println("发送的总的字节数 =" + total + " 耗时:" + (System.currentTimeMillis() - startTime));
        //关闭
        inputChannel.close();
        outputChannel.close();
    }
}
