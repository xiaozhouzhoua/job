package com.me.job.problem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CpuController {
    /**
     * 死循环
     * */
    @GetMapping("/loop")
    public List<Long> loop() {
        // 不正常数据的情况导致死循环
        String data = "{\"data\":[{\"partnerId\":]";
        return getPartnerIdsFromJson(data);
    }

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    /**
     * 死锁
     * */
    @GetMapping("/deadlock")
    public String deadlock() {
        new Thread(()->{
            synchronized(lock1) {
                try {Thread.sleep(1000);
                } catch (Exception ignored) {
                }
                synchronized(lock2) {
                    System.out.println("Thread1 over");
                }
            }
        }).start();
        new Thread(()->{
            synchronized(lock2) {
                try {Thread.sleep(1000);
                } catch (Exception ignored) {
                }
                synchronized(lock1) {
                    System.out.println("Thread2 over");
                }
            }
        }).start();
        return "deadlock";
    }
    public static List<Long> getPartnerIdsFromJson(String data){
        //{\"data\":[{\"partnerId\":982,\"count\":\"10000\",\"cityId\":\"11\"},{\"partnerId\":983,\"count\":\"10000\",\"cityId\":\"11\"},{\"partnerId\":984,\"count\":\"10000\",\"cityId\":\"11\"}]}
        //上面是正常的数据
        List<Long> partnerIds = new ArrayList<>(2);
        if (data == null || data.length() <= 0) {
            return partnerIds;
        }
        int dataPos = data.indexOf("data");
        if (dataPos < 0) {
            return partnerIds;
        }
        int leftBracket = data.indexOf("[", dataPos);
        int rightBracket= data.indexOf("]", dataPos);
        if (leftBracket < 0 || rightBracket < 0) {
            return partnerIds;
        }
        String partners = data.substring(leftBracket + 1, rightBracket);
        if (partners == null || partners.length() <= 0) {
            return partnerIds;
        }
        while(partners != null && partners.length() > 0) {
            int idPos = partners.indexOf("partnerId");
            if (idPos < 0) {
                break;
            }
            int colonPos = partners.indexOf(":", idPos);
            int commaPos = partners.indexOf(",", idPos);
            if (colonPos < 0 || commaPos < 0) {
                // 如果不加下面这行，continue之后字符串未发生变化，一直重复上面的步骤
                //partners = partners.substring(idPos+"partnerId".length());//1
                continue;
            }
            String pid = partners.substring(colonPos+1, commaPos);
            if (pid == null || pid.length() <= 0) {
                //partners = partners.substring(idPos+"partnerId".length());//2
                continue;
            }
            try{
                partnerIds.add(Long.parseLong(pid));
            } catch (Exception ignored){
            }
            partners = partners.substring(commaPos);
        }
        return partnerIds;
    }
}
