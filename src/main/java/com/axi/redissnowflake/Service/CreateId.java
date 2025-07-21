package com.axi.redissnowflake.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Service
public class CreateId {
    private static final int SIGNED_BIT = 1;
    private static final int TIME_STAMP_BIT = 41;
    private static final int WORK_ID_BIT = 10;
    private static final int SEQUENCE_BIT = 12;

    private static final Long INIT_TIME_STAMP = 1609459200000L;

    private static final Long WORK_ID_MAX = -1L ^ (-1L << WORK_ID_BIT);

    private static final Long SEQUENCE_MAX = -1L ^ (-1L << SEQUENCE_BIT);

    @Autowired
    public StringRedisTemplate stringRedisTemplate;
    public String createId() throws SocketException {
        long currentTimeMillis = System.currentTimeMillis() - INIT_TIME_STAMP;
        long sequence = Long.parseLong(getSequence());
        long workId = getWorkIdFromMac();
        // 位移偏移量（不需要改）
        long WORKER_LEFT = 12; // workId 左移 12 位
        long TIMESTAMP_LEFT = WORKER_LEFT + 10; // 时间戳左移 22 位
        long snowflakeId = (currentTimeMillis << TIMESTAMP_LEFT)
                | (workId << WORKER_LEFT)
                | sequence;
        System.out.println("生成的 Snowflake ID: " + snowflakeId);
        System.out.println("二进制表示: " + Long.toBinaryString(snowflakeId));
        return String.valueOf(snowflakeId);
    }

    public String getSequence(){
        String sequence = "0";

            stringRedisTemplate.opsForValue().increment("sequence:number",1);
            if(Long.parseLong(stringRedisTemplate.opsForValue().get("sequence:number")) > SEQUENCE_MAX){
                stringRedisTemplate.opsForValue().set("sequence:number","0");
            }
            sequence = stringRedisTemplate.opsForValue().get("sequence:number");;
            return sequence;


    }
    public static long getWorkIdFromMac() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac != null) {
                // 使用MAC地址的最后几个字节作为worker id
                long workerId = ((mac[mac.length - 2] & 0xFFL) << 8) | (mac[mac.length - 1] & 0xFFL);
                return workerId % 1024; // 假设worker id的最大值为1023
            }
        }
        throw new RuntimeException("Could not fetch MAC address.");
    }


}
