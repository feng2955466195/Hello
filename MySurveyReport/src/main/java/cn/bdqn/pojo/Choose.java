package cn.bdqn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @TableName choose
 */
@TableName(value ="choose")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Choose implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer cid;

    /**
     * 
     */
    private String cname;

    /**
     * 
     */
    private Integer sid;

    //该选择的用户数量
    @TableField(exist = false)
    private Integer user_count;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;





}