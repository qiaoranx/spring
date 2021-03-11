package com.example.demo.core;

import lombok.Data;

/**
 * 保存配置文件中的信息
 * 相当于保存在内存的配置
 */
@Data
public class GPBeanDefinition {

    private String beanClassName; //原生bean的全限定类名

    //标记是否延迟加载
    private boolean lazyInit=false;

    //保存beanName,在IOC容器中的key
    private String factoryBeanName;


}
