package cn.bdqn.service;

import cn.bdqn.pojo.Subject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.Page;

import java.util.List;

/**
* @author 29554
* @description 针对表【subject】的数据库操作Service
* @createDate 2022-06-12 22:56:21
*/
public interface SubjectService extends IService<Subject> {
    //查询主题集合分页
    Page<Subject> FindPageSubject(Integer index,QueryWrapper<Subject> que);
}
