package com.bytecamp.util;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangke
 * @description: GenerateIDUtil
 * @date 2019-08-24 23:42
 */
public class GenerateIDUtil {
    /**
     * 13位时间戳 + 3位seq + 1位机器id + 10位pid
     */
    private final long SEQUENCE_LEN = 3;
    private final long MACHINE_LEN = 1;
    private final long PID_LEN = 10;
    /**
     * 每一部分的最大值
     */
    private final long MAX_MACHINE_NUM = (long) (Math.pow(10, MACHINE_LEN));
    private final long MAX_SEQUENCE = (long) (Math.pow(10, SEQUENCE_LEN));

    private long machineId;
    private long sequence = 0L;
    private long lastStmp = -1L;

    /**
     * 单例模式
     */
    private volatile static GenerateIDUtil instance;


    public static GenerateIDUtil getInstance(long machineId) {
        if (instance == null) {
            synchronized (GenerateIDUtil.class) {
                if (instance == null) {
                    instance = new GenerateIDUtil(machineId);
                }
            }
        }
        return instance;
    }

    private GenerateIDUtil(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.machineId = machineId;
    }

    public synchronized String nextId(long pid) {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = sequence + 1;
            //同一毫秒的序列数已经达到最大
            if (sequence == MAX_SEQUENCE + 1L) {
                sequence = 0L;
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }
        lastStmp = currStmp;
        //13位时间戳 + 3位seq + 1位机器id + 10位pid
        StringBuffer buf = new StringBuffer((int) (13 + SEQUENCE_LEN + MACHINE_LEN + PID_LEN));
        buf.append(String.format("%013d", currStmp));
        buf.append(String.format("%03d", sequence));
        buf.append(String.format("%01d", machineId));
        buf.append(String.format("%010d", pid));
        return buf.toString();
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        GenerateIDUtil snowFlake = GenerateIDUtil.getInstance(0);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                String id = snowFlake.nextId(9876543210L);
                System.out.println(id);
                long now_time = Long.parseLong(id.substring(0, 13));
                System.out.println(dateformat.format(now_time));  //解出时间
                long pid = Long.parseLong(id.substring((int) (13 + snowFlake.SEQUENCE_LEN + snowFlake.MACHINE_LEN)));
                System.out.println(pid);                          //解出pid
            });
        }
    }

}
