package cn.bdqn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import jdk.nashorn.internal.objects.annotations.Property;
import lombok.Data;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable , HttpSessionActivationListener, HttpSessionBindingListener {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer uid;

    /**
     * 
     */
    private String uname;

    /**
     * 
     */
    private String password;

    /**
     * 
     */
    private Integer level;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * @see解绑
     */
    public void valueUnbound(HttpSessionBindingEvent event)  {
        System.out.println("valueUnbound:"+uname);
    }


    /**
     * @see绑定
     */
    public void valueBound(HttpSessionBindingEvent event)  {
        System.out.println("valueBound:"+uname);
    }



    /**
     * @see钝化
     */
    public void sessionWillPassivate(HttpSessionEvent se)  {
        System.out.println("钝化:"+uname);
    }


    /**
     * @see活化
     */
    public void sessionDidActivate(HttpSessionEvent se)  {
        System.out.println("活化："+uname);
    }
}