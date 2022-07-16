package cn.bdqn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName subject
 */
@TableName(value ="subject")
@Data
public class Subject implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer sid;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private Integer stype;

    /**
     * 
     */
    private Integer del;

    //该主题下的选项集合
    @TableField(exist = false)
    private List<Choose> chooses;

    //该主题下投票的用户数量
    @TableField(exist = false)
    private Integer user_count;

    //在整个主题下的用户总选择数量
    @TableField(exist = false)
    private  Integer user_choose_count;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}