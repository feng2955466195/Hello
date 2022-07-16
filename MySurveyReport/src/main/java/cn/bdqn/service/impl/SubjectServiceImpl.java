package cn.bdqn.service.impl;

import cn.bdqn.pojo.Subject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.service.SubjectService;
import cn.bdqn.dao.SubjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 29554
* @description 针对表【subject】的数据库操作Service实现
* @createDate 2022-06-12 22:56:21
*/
@Service
@Transactional
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject>
    implements SubjectService{

    //通过分页查询主题数据
    @Override
    public Page<Subject> FindPageSubject(Integer index,QueryWrapper<Subject> que) {
        Page<Subject> subjects = PageHelper.startPage(index, 6);
        getBaseMapper().FindPageSubject(que);
        return subjects;
    }
}




