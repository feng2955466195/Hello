package cn.bdqn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bdqn.pojo.User;
import cn.bdqn.service.UserService;
import cn.bdqn.dao.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 29554
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-06-11 17:27:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




