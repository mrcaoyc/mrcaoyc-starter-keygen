package com.github.mrcaoyc.starter.keygen;

/**
 * 获取机器Id的策略
 *
 * @author CaoYongCheng
 */
public interface WorkIderStrategy {
    /**
     * 获取机器Id
     *
     * @return 机器Id
     */
    long getWorkerId();
}
