package com.github.mrcaoyc.starter.keygen.autoconfigure;

import com.github.mrcaoyc.starter.keygen.GenericKeyGenerator;
import com.github.mrcaoyc.starter.keygen.KeyGenerator;
import com.github.mrcaoyc.starter.keygen.WorkIderStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;

/**
 * @author CaoYongCheng
 */
@Configuration
@EnableConfigurationProperties({GeneratorProperties.class})
@ConditionalOnClass(KeyGenerator.class)
@ConditionalOnProperty(prefix = "key-generator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GeneratorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(KeyGenerator.class)
    public KeyGenerator keyGenerator(GeneratorProperties generatorProperties) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(generatorProperties.getYear(), generatorProperties.getMonth() - 1, generatorProperties.getDay());
        WorkIderStrategy workIderStrategy;
        try {
            if (generatorProperties.getWorkerId() == null) {
                workIderStrategy = (WorkIderStrategy) Class.forName(generatorProperties.getWorkerIdStrategy()).newInstance();
            } else {
                workIderStrategy = generatorProperties::getWorkerId;
            }
        } catch (Exception e) {
            throw new RuntimeException("Id生成器的机器策略" +
                    "初始化失败.", e.getCause());
        }
        return new GenericKeyGenerator(workIderStrategy, calendar, generatorProperties.getWorkerIdBits());
    }
}
