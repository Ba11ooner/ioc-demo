package edu.hitwh.utils;

import edu.hitwh.annotation.Autowired;
import edu.hitwh.annotation.Component;
import edu.hitwh.annotation.ComponentScan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于处理 @Autowired
 */
public class AnnotationConfigApplicationContext {

    //所有扫描到的类
    private List<Class<?>> classes = new ArrayList<>();

    //bean 的类名和实例映射
    // Key 为 Bean 名称
    // Value 为实例化对象
    private Map<String, Object> beanMap = new HashMap<>();

    // 指定某一个带 @Configuration 的配置类，可有可无
    // 如果没有，默认扫描所有的类，在其中查找带 @Configuration 注解的类
    // 作业中要求的是指定一个 configClass，假定就通过该 configClass 指定，可以省不少处理的功夫
    private Class configClass;

    // 构建类时进行包扫描
    // 1.获取带 @Configuration 注解的类 → 此处获取方法为向构造函数中传递一个 configClass 参数
    // 2.获取带 @Configuration 注解的类中 @ComponentScan 中指定的扫描路径
    // 3.获取该扫描路径下的所有带 @Component 注解的类
    // 4.处理带 @Component 注解的类的依赖关系，并进行统一的实例化
    public AnnotationConfigApplicationContext(Class configClass) throws Exception {
        System.out.println("-----------------------------------------------------------------------------------------");
        // 获取 @ComponentScan 注解
        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        // 获取待扫描路径
        String path = componentScan.value();
        // 初始化所有的 Bean
        initBean(path);
        // 遍历 Bean，实现对 Bean 内部 @Autowired 指定属性的处理（即实例化该对象）
        injectDependency();
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    /**
     * 获取 Bean 的默认名称
     * 规则：首字母改小写，其他不变
     * 实例：ClassA → classA
     */
    private String getBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    /**
     * 初始化 Bean : 将所有带 @Component 的类实例化并统一放到 beanMap 中统一管理
     */
    private void initBean(String path) throws Exception {
        System.out.println("initBean");
        //获取扫描路径下的所有 Class
        classes.addAll(GetClassesFromPackage.getClasses(path));
        //筛选出带 @Component 注解的 Class
        for (Class clazz : classes) {
            //带 @Component 注解，实例化后放进容器中
            if (clazz.isAnnotationPresent(Component.class)) {
                Object bean = clazz.newInstance();
                //把实例放入容器中
                Component component = (Component) clazz.getDeclaredAnnotation(Component.class);
                //如果在 @Component 中指定了 BeanName，则用指定的 BeanName
                if (!component.value().isEmpty()) {
                    System.out.println();
                    System.out.println("beanName:" + component.value());
                    System.out.println("className:" + clazz.getSimpleName());
                    beanMap.put(component.value(), bean);
                } else { //否则默认用类名作为 BeanName
                    System.out.println();
                    System.out.println("beanName:" + getBeanName(clazz));
                    System.out.println("className:" + clazz.getSimpleName());
                    beanMap.put(getBeanName(clazz), bean);
                }
            }
        }
        System.out.println();
    }

    /**
     * 注入依赖 : 为 Bean 内部的 Bean 提供实例化对象，相当于实现 @Autowired 的处理逻辑
     */
    private void injectDependency() throws Exception {
        System.out.println("injectDependency");
        //为所有的 Bean 各自注入依赖
        for (Object bean : beanMap.values()) {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    //允许访问
                    field.setAccessible(true);
                    //注意：field.getClass() 得到的是 Field
                    //System.out.println("field.getClass():" + field.getClass().getName());
                    //注意：field.getType() 得到的是 Field 修饰的属性的类型
                    Class fieldClass = field.getType();
                    //System.out.println("field.getType():" + field.getType().getName());
                    Component fieldComponent = (Component) fieldClass.getAnnotation(Component.class);
                    //如果在 @Component 中指定了 BeanName，则用指定的 BeanName
                    if (!fieldComponent.value().isEmpty()) {
                        //获取自定义 BeanName
                        String beanName = fieldComponent.value();
                        System.out.println();
                        System.out.println("@Autowired beanName:" + beanName);
                        //用自定义 BeanName 在 beanMap 获取实例化对象
                        Object dependencyBean = beanMap.get(beanName);
                        //注入，即属性赋值
                        field.set(bean, dependencyBean);
                    } else { //否则默认用类名作为 BeanName
                        //获取默认类名
                        String beanName = getBeanName(field.getType());
                        System.out.println();
                        System.out.println("@Autowired beanName:" + beanName);
                        //用默认 BeanName 在 beanMap 获取实例化对象
                        Object dependencyBean = beanMap.get(beanName);
                        //注入，即属性赋值
                        field.set(bean, dependencyBean);
                    }
                }
            }
        }
    }

    /**
     * 根据类型获取 Bean 实例
     * 此处体现工厂模式：通过工厂类获取实例化对象，而非自己创建实例化对象
     */
    public <T> T getBean(Class<T> clazz) {
        //根据默认类名生成的 Bean 可用
        String beanName = getBeanName(clazz);
        //clazz.cast() 是 Java 中的一个方法，用于将一个对象强制转换为指定类型。它的作用类似于普通的类型转换语法
        return clazz.cast(beanMap.get(beanName));
    }

    //需要手动进行类型转换
    public Object getBean(String beanName) {
        //指定类名时生成的 Bean 可用
        return beanMap.get(beanName);
    }
}

