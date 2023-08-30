package edu.hitwh;

import edu.hitwh.config.Config;
import edu.hitwh.entity.User;
import edu.hitwh.entity.UserWithName;
import edu.hitwh.utils.AnnotationConfigApplicationContext;

// Test 相当于 Spring 启动类
public class Test {
    /**
     *      错误写法：
     *      @Autowired
     *      User user;
     *
     *      不能为 static 变量赋值
     *      因为 static 在 Test 类加载时就已经存在
     *      而对 @Autowired 的处理在调用 AnnotationConfigApplicationContext 的构造函数时才进行
     */
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(Config.class);
        User user = annotationConfigApplicationContext.getBean(User.class);
        user.print();

        UserWithName userWithName = (UserWithName) annotationConfigApplicationContext.getBean("IOC_user");
        userWithName.print();
    }
}
