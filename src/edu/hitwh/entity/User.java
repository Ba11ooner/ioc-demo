package edu.hitwh.entity;

import edu.hitwh.annotation.Autowired;
import edu.hitwh.annotation.Component;

@Component
public class User {

    @Autowired
    Group group;

    //输出通过 group 的 BeanName
    public void print() throws Exception{
        System.out.println("Group Bean Name:");
        System.out.println(group.getBeanName());
    }
}
