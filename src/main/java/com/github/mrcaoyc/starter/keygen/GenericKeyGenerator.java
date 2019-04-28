package com.github.mrcaoyc.starter.keygen;

import org.springframework.util.Assert;

import java.util.Calendar;

/**
 * @author CaoYongCheng
 * <p>
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 0000000000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
public class GenericKeyGenerator implements KeyGenerator {

    /**
     * 机器ID和序列共占22位
     */
    private static final byte WORKER_ID_AND_SEQUENCE_BITS = 22;

    /**
     * 机器ID向左移位数
     */
    private byte workerIdShift;

    /**
     * 支持的最大机器id
     */
    private int maxWorkerId;


    /**
     * 时间截向左移22位(10+12)
     */
    private static final long TIMESTAMP_LEFT_SHIFT = WORKER_ID_AND_SEQUENCE_BITS;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private byte sequenceMask;

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 开始时间截 (2015-01-01)
     */
    private long epoch;


    public GenericKeyGenerator(WorkIderStrategy workIderStrategy, Calendar begin, byte workerIdBits) {
        initBits(workerIdBits);
        initWorkerId(workIderStrategy);
        initEpoch(begin);
    }


    /**
     * 获取新的ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    @Override
    public synchronized Long generateKey() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - epoch) << TIMESTAMP_LEFT_SHIFT)
                | (workerId << workerIdShift)
                | sequence;
    }


    /**
     * 初始化机器Id
     */
    private void initWorkerId(WorkIderStrategy workIderStrategy) {
        long workerId = workIderStrategy.getWorkerId();
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    /**
     * 初始化开始时间搓
     */
    private void initEpoch(Calendar begin) {
        epoch = begin.getTimeInMillis();
    }

    /**
     * 初始化各段的位数和最大值
     */
    private void initBits(byte workerIdBits) {
        Assert.state(workerIdBits < WORKER_ID_AND_SEQUENCE_BITS, "workerIdBits  has to be less than 22");
        maxWorkerId = ~(-1 << workerIdBits);
        byte sequenceBits = (byte) (WORKER_ID_AND_SEQUENCE_BITS - workerIdBits);
        workerIdShift = sequenceBits;
        sequenceMask = (byte) ~(-1 << sequenceBits);
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
