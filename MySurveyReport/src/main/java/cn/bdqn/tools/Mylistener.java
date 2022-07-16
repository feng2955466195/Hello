package cn.bdqn.tools;

import cn.bdqn.pojo.User;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.HashSet;
import java.util.Set;

@WebListener //Web监听
@Component
public class Mylistener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    //全局作用域application
    private ServletContext application;

    //session属性新增事件
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        System.out.println("attributeAdded:"+se.getName()+"\t"+se.getValue());
        HttpSession session = se.getSession();

        Set<User> users= (Set<User>) application.getAttribute("users");
        users.add((User) session.getAttribute("user")); //存入
        application.setAttribute("users",users); //更新一下
    }

    //session属性发生替换时
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        HttpSession session = se.getSession();
        System.out.println("attributeReplaced before:"+se.getName()+"\t"+se.getValue());
        System.out.println("attributeReplaced after:"+se.getName()+"\t"+session.getAttribute(se.getName()));

        Set<User> users= (Set<User>) application.getAttribute("users");
        users.add((User) session.getAttribute("user")); //存入
        boolean del= users.remove(session.getAttribute("old_user"));        //删除上一个用户
        System.out.println("移除上一个用户：："+del);
        application.setAttribute("users",users); //更新一下
    }

    //这是application 作用域的初始化
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.application = sce.getServletContext();
        System.out.println("create application:"+this.application);
        //创建一个session集合，用于判断这个用户是否在登录
        application.setAttribute("users", new HashSet<>());
    }

    //这是 application 作用域的销毁
    @Override
    public void contextDestroyed(ServletContextEvent se) {
        System.out.println("destroy application:"+this.application);
    }

    //这是session会话的创建
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.setMaxInactiveInterval(3*60);  //session会话的时长
        System.out.println("create sessionId:"+session.getId());
    }

    //这是session会话的销毁
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println("destroy sessionId:"+session.getId());
        //更新用户集合储存
        System.out.println("移除session==="+session.getId());
        Set<User> users = (Set<User>)application.getAttribute("users");  //获取到application作用域中的用户集合
        boolean del= users.remove(session.getAttribute("user")); //在user集合中移除这个user
        System.out.println("是否成功删除？？？？"+del);
        application.setAttribute("users",users); //更新一下
    }
}
