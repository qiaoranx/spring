package com.example.demo.aspect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方法增强支持类
 */
public class GPAdevisedSupport {

    private Class targetClass;

    private Object target;

    private Pattern pointCutClassPattern;

    /**
     * 目标方法对应的拦截器链
     */
    private transient Map<Method, List<Object>> methodCache;

    private GPAopConfig config;

    public GPAdevisedSupport(GPAopConfig config) {
        this.config = config;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 目标类的每个被增强的方法都对应一个拦截器链
     * @param method 目标方法
     * @param targetClass 目标类
     * @return 拦截器链
     * @throws NoSuchMethodException
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass)
            throws NoSuchMethodException {

        List<Object> cached = methodCache.get(method);
        //缓存未命中？？？？
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    /**
     * 判断目标类是否符合切面规则
     *
     * @return
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    private void parse() {
        String pointCut = config.getPointCut().replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        //切面类的正则
        pointCutClassPattern = Pattern.compile("class" +
                pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));

        methodCache = new HashMap<Method, List<Object>>();
        //切面方法的正则
        Pattern pattern = Pattern.compile(pointCut);

        try {
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            for (Method method : targetClass.getMethods()) {

                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                //满足切面规则的方法，添加到AOP配置中
                if (matcher.matches()) {
                    List<Object> advices = new LinkedList<>();
                    //前置通知
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore().trim()))) {
                        //增强链advices添加GPMethodBeforeAdvice对象，构造器参数分别为切面类方法中的"before"方法，切面类的实例
                        advices.add(new GPMethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()),
                                aspectClass.newInstance()));
                    }
                    //后置通知
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter().trim()))) {
                        advices.add(new GPAfterReturningAdvice(aspectMethods.get(config.getAspectAfter()),
                                aspectClass.newInstance()));
                    }
                    //异常通知
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow().trim()))) {
                        GPAfterThrowingAdvice afterThrowingAdvice = new GPAfterThrowingAdvice(
                                aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectThrowingName());
                        advices.add(afterThrowingAdvice);
                    }
                    /**
                     * methodCache 放入目标类的方法和增强链
                     */
                    methodCache.put(method, advices);

                }

            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }


    }
}
