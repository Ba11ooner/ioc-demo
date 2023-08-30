package edu.hitwh.entity;

import edu.hitwh.annotation.Autowired;
import edu.hitwh.annotation.Component;

@Component("IOC_user")
public class UserWithName {

    @Autowired
    GroupWithName group;

    //输出通过 group 的 BeanName
    public void print() throws Exception {
        System.out.println("Group Bean Name:");
        System.out.println(group.getBeanName());
    }
}
