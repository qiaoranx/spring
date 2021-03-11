package com.example.demo.context;

import com.example.demo.annotation.GPAutowired;
import com.example.demo.annotation.GPController;
import com.example.demo.annotation.GPService;
import com.example.demo.aspect.GPAdevisedSupport;
import com.example.demo.aspect.GPAopConfig;
import com.example.demo.aspect.GPAopProxy;
import com.example.demo.aspect.GPJdkDynamicAopProxy;
import com.example.demo.core.GPBeanDefinition;
import com.example.demo.core.GPBeanFactory;
import com.example.demo.core.GPBeanWrapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private String[] configLocations;

    private GPBeanDefinitionReader reader;
    /**
     * 单例的ioc容器缓存
     */
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //通用的ioc容器
    private Map<String, GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, GPBeanWrapper>();

    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() throws Exception {
        //定位配置文件
        reader = new GPBeanDefinitionReader(this.configLocations);
        //加载配置文件，扫描相关的类封装成beanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册,把配置信息放到容器里
        doRegisterBeanDefinition(beanDefinitions);
        //把不是延时加载的类提前初始化
        doAutowired();
    }

    /**
     * 只处理非延时加载的情况
     */
    private void doAutowired() {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) throws Exception {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + "exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //容器初始化完毕,此时对象还没有创建，value只是存放了beanDefinition
    }

    /**
     * DI 的入口
     *
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) {

        GPBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        //生成通知事件 为对象初始化事件设置一种回调机制
        GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();

        Object instance = instantiateBean(beanDefinition);
        if (null == instance) {
            return null;
        }

        //在实例初始化前提供回调入口
        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);

        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        //在实例初始化后调用一次
        beanPostProcessor.postProcessAfterInitialization(instance, beanName);

        populateBean(beanName, instance);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();

        if (!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowired.class)) {
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();

            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //真正将beanDefinition转化为bean实例
    private Object instantiateBean(GPBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                //加入aop配置
                GPAdevisedSupport config = instantionAopConfig();
                config.setTargetClass(clazz);
                config.setTarget(instance);

                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private GPAopProxy createProxy(GPAdevisedSupport config) {
        Class targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new GPJdkDynamicAopProxy(config);
        }
        return null;
    }

    /**
     * 读取 properties的配置
     *
     * @return
     * @throws Exception
     */
    private GPAdevisedSupport instantionAopConfig() throws Exception {
        GPAopConfig config = new GPAopConfig();
        config.setPointCut(reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));

        return new GPAdevisedSupport(config);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
