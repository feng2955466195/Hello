package cn.bdqn.dao;

import cn.bdqn.pojo.Subject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 29554
* @description 针对表【subject】的数据库操作Mapper
* @createDate 2022-06-12 22:56:21
* @Entity cn.bdqn.Subject
*/
public interface SubjectMapper extends BaseMapper<Subject> {

    //查询主题集合分页
    List<Subject> FindPageSubject(@Param("ew") QueryWrapper<Subject> que);

}




