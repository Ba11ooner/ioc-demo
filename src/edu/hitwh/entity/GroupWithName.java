package edu.hitwh.entity;

import edu.hitwh.annotation.Component;

@Component("IoC_group")
public class GroupWithName {
    public String getBeanName() throws Exception{
        //返回 BeanName，这里的 BeanName 是 IOC_group
        Class<? extends GroupWithName> group = this.getClass();
        Component component = group.getAnnotation(Component.class);
        if (!component.value().isEmpty()){
            return component.value();
        }else{
            String simpleName = group.getSimpleName();
            return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        }
    }
}
