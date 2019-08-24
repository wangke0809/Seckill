package com.bytecamp.util;

import java.text.SimpleDateFormat;

/**
 * @author wangke
 * @description: TODO
 * @date 2019-08-24 23:42
 */
public class GenerateIDUtil {
    /**
     * 起始的时间戳
     */
    private final long START_STMP = 1566662460000L;

    /**
     * 时间戳 9 位 + 3位seq + 1位机器id + 8位pid
     * 序列号占用的位数
     */
    private final long SEQUENCE_BIT = 1;
    /**
     * 机器标识占用的位数
     */
    private final long MACHINE_BIT = 1;
    /**
     * 商品编号占用的位数
     */
    private final long PID_BIT = 32;
    /**
     * 每一部分的最大值
     */
    private final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final long MACHINE_LEFT = PID_BIT;
    private final long SEQ_LEFT = MACHINE_BIT + PID_BIT;
    private final long TIMESTMP_LEFT = SEQUENCE_BIT + MACHINE_BIT + PID_BIT;

    /**
     * 机器标识
     */
    private long machineId;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 上一次时间戳
     */
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

    public synchronized long nextId(long pid) {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;
        //时间戳 + 3位seq + 1位机器id + 8位pid
        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | sequence << SEQ_LEFT                  //序列号部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | pid;                                  //商品Id部分
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
        GenerateIDUtil snowFlake = GenerateIDUtil.getInstance(1);
        GenerateIDUtil snowFlake2 = GenerateIDUtil.getInstance(0);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

        for (int i = 0; i < 10; i++) {
            long id = snowFlake.nextId(3163885158L);
            long id2 = snowFlake2.nextId(3163885158L);
            System.out.println(i);
            System.out.println(id);
            System.out.println(id2);

            long now_time = (id >> snowFlake.TIMESTMP_LEFT) + snowFlake.START_STMP;
            System.out.println(dateformat.format(now_time));  //解出时间
            long pid = id & ((1l << snowFlake.PID_BIT) - 1);
            System.out.println(pid);                          //解出pid
        }
    }
}
