package classes;

import classes.interfaces.ICategoryManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Demo {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

        ICategoryManager manager = (ICategoryManager) context.getBean("category");
        manager.create("BasicCategory");
    }
}
