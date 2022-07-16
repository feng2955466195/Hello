package cn.bdqn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName logsheet
 */
@TableName(value ="logsheet")
@Data
@AllArgsConstructor  //有参构造
@NoArgsConstructor  //无参构造
public class Logsheet implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer lid;

    /**
     * 
     */
    private Integer uid;

    /**
     * 
     */
    private Integer cid;

    /**
     * 
     */
    private Integer sid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}