package com.github.mrcaoyc.starter.keygen.autoconfigure;

import com.github.mrcaoyc.starter.keygen.IPSectionStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author CaoYongCheng
 */
@ConfigurationProperties(prefix = "key-generator")
public class GeneratorProperties {
    /**
     * 工作Id实现侧率
     */
    private String workerIdStrategy = IPSectionStrategy.class.getName();

    /**
     * 系统初始年
     */
    private short year = 2019;

    /**
     * 系统初始月，从1开始
     */
    private byte month = 1;

    /**
     * 系统初始日
     */
    private byte day = 1;

    /**
     * 是否启用Id生成器
     */
    private boolean enabled = true;

    /**
     * 指定workId,如果指定了具体的workerId，则workerIdStrategy将不生效
     */
    private Long workerId;

    /**
     * 机器位，由于已有1符号位+41时间戳，所有还剩下64-1-41=22位
     * 22=机器位+序列化，所有机器位应当小于22
     */
    private byte workerIdBits = 10;

    public String getWorkerIdStrategy() {
        return workerIdStrategy;
    }

    public void setWorkerIdStrategy(String workerIdStrategy) {
        this.workerIdStrategy = workerIdStrategy;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public byte getMonth() {
        return month;
    }

    public void setMonth(byte month) {
        this.month = month;
    }

    public byte getDay() {
        return day;
    }

    public void setDay(byte day) {
        this.day = day;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public byte getWorkerIdBits() {
        return workerIdBits;
    }

    public void setWorkerIdBits(byte workerIdBits) {
        this.workerIdBits = workerIdBits;
    }
}
