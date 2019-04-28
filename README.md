#### 功能
使用雪花算法生成主键

#### 引入
指定maven仓库地址：
```
maven { url "https://raw.github.com/mrcaoyc/maven-repo/releases/" }
```
gradle 导包
```
implementation 'com.github.mrcaoyc:mrcaoyc-starter-keygen:1.0'
```

引入包后，会自动向IOC容器注入`KeyGenerator`的实现类，可参见`GeneratorAutoConfiguration`。

如果不想使用主键生成器，可以直接移除该jar包，或在配置文件中将`enabled`设置为`false`,例如：

```
key-generator:
  enabled: false  # 默认值为true
```


#### 配置属性介绍

属性前缀`key-generator`

- enabled：是否启用主键生成器，默认值：true
- year：开始年份，用于计算相对时间戳用。默认值：2019
- month：开始月份，用于计算相对时间戳用，范围（1-12）。默认值：1
- day：开始日：用于计算相对时间戳用，范围（1-31）。默认值：1
- workerIdStrategy：获取机器Id策略，类的全类名，默认使用`IPSectionStrategy`，目前已内置了几种常用的策略`IPKeyStrategy`、`IPSectionStrategy`、`HostNameStrategy`。
- workerId：手动配置机器Id，如果设置了`workerId`，`workerIdStrategy`将会失效。


#### 如何自定义WorkerIdStrategy
如果内置的策略不能满足需求，就需要自定义策略。只需要两步就可以完成。

步骤1：实现WorkerIdStrategy
```
package com.github.mrcaoyc;

import com.github.mrcaoyc.starter.keygen.WorkIderStrategy;

/**
 * @author CaoYongCheng
 */
public class CustomWorkerIdStrategy implements WorkIderStrategy {
    @Override
    public long getWorkerId() {
        return 3;
    }
}
```

步骤2：配置workerIdStrategy

```
key-generator:
  worker-id-strategy: com.github.mrcaoyc.CustomWorkerIdStrategy
```

这样就完成了自定义机器Id的策略。