package edu.hitwh.entity;

import edu.hitwh.annotation.Component;

@Component
public class Group {
    public String getBeanName() throws Exception{
        //返回 BeanName，这里的 BeanName 是根据类名默认生成的 group
        Class<? extends Group> group = this.getClass();
        Component component = group.getAnnotation(Component.class);
        if (!component.value().isEmpty()){
            return component.value();
        }else{
            String simpleName = group.getSimpleName();
            return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        }
    }
}
