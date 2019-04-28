package com.github.mrcaoyc.starter.keygen;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * 浏览 {@link IPKeyStrategy} workerId生成的规则后，感觉对服务器IP后10位（特别是IPV6）数值比较约束.
 * <p>
 * <p>
 * 有以下优化思路：
 * 因为workerId最大限制是2^10，我们生成的workerId只要满足小于最大workerId即可。
 * 1.针对IPV4:
 * ....IP最大 255.255.255.255。而（255+255+255+255) < 1024。
 * ....因此采用IP段数值相加即可生成唯一的workerId，不受IP位限制。
 * 2.针对IPV6:
 * ....IP最大ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 * ....为了保证相加生成出的workerId < 1024,思路是将每个bit位的后6位相加。这样在一定程度上也可以满足workerId不重复的问题。
 * </p>
 * 使用这种IP生成workerId的方法,必须保证IP段相加不能重复
 *
 * @author CaoYongCheng
 */
public class IPSectionStrategy implements WorkIderStrategy {
    @Override
    public long getWorkerId() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
        }
        byte[] ipAddressByteArray = address.getAddress();
        long workerId = 0L;
        switch (ipAddressByteArray.length) {
            case 4:
                for (byte byteNum : ipAddressByteArray) {
                    workerId += byteNum & 0xFF;
                }
                break;
            case 8:
                for (byte byteNum : ipAddressByteArray) {
                    workerId += byteNum & 0B111111;
                }
                break;
            default:
                throw new IllegalStateException("Bad LocalHost InetAddress, please check your network!");
        }
        return workerId;
    }
}
