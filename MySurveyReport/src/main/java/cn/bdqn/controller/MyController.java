package cn.bdqn.controller;

import cn.bdqn.pojo.Choose;
import cn.bdqn.pojo.Logsheet;
import cn.bdqn.pojo.Subject;
import cn.bdqn.pojo.User;
import cn.bdqn.service.ChooseService;
import cn.bdqn.service.LogsheetService;
import cn.bdqn.service.SubjectService;
import cn.bdqn.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Bools;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import java.rmi.registry.Registry;
import java.util.*;

@Controller
@Transactional
public class MyController {

    //用户servisce
    @Autowired
    UserService usi;
    //主题service
    @Autowired
    SubjectService ssi;
    //选择service
    @Autowired
    ChooseService csi;
    //记录service
    @Autowired
    LogsheetService lsi;



    //执行登录
    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(User user, HttpSession session){
        System.out.println("执行登录！！!！！");
        //json待返回Map数据
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("key",0);  //默认失败

        System.out.println("登录session的id====="+session.getId());
        //判断该用户是否在别处登录👇👇👇
        ServletContext application=session.getServletContext();
        //application在线用户集合
        Set<User> users = (Set<User>)application.getAttribute("users");

        for (User u:users) {
            //判断存在此用户信息
            if(user.getUname().equals(u.getUname())&&user.getPassword().equals(u.getPassword())){
                System.out.println("已被占用！！");
                //如果存在此用户,终止以下执行，返回登录页面提示(占用)
                map.put("key",-1);
                return map;
            }
        }



       user= usi.getBaseMapper().selectOne(new QueryWrapper<User>().eq("uname",user.getUname()).eq("password",user.getPassword()).groupBy("uname"));
       if (user!=null){
           User old_user=new User(); //上一个用户
           if(session.getAttribute("user")!=null){
               old_user= (User) session.getAttribute("user");
               session.setAttribute("old_user",old_user);
           }

           session.setAttribute("user",user);
           session.setAttribute("index",1);//每次重新登录，刷新显示第 1 页
           session.setAttribute("keyword",""); //默认keyword关键字为 ""
           session.setAttribute("update_key",false); //默认update_key=false

           map.put("key",1);
           map.put("user",user);

//           //同步更新在线用户集合
//           users.add(user);
//           application.setAttribute("users",users);

       }//if用户存在范围结尾

       return map;
    }

    //执行注销
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();  //手动销毁session会话
        return "redirect:login.html";
    }

    //判断是否存在此用户名
    @PostMapping("/exist_uname")
    @ResponseBody
    public Map<String,Object> exist_uanme(String uname){
        User user= usi.getBaseMapper().selectOne(new QueryWrapper<User>().eq("uname",uname).groupBy("uname"));
        int exist=user!=null?1:0;
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("exist",exist);
        return map;
    }

    //执行注册
    @PostMapping("reg")
    public String reg(User user, HttpSession session,Model model){
        user.setLevel(0);//用户等级=0  普通用户
        //开始注册
        int reg= usi.getBaseMapper().insert(user);
        if (reg>0){//注册成功

            User old_user=new User(); //上一个用户
            if(session.getAttribute("user")!=null){
                old_user= (User) session.getAttribute("user");
                session.setAttribute("old_user",old_user);
            }
            session.setAttribute("user",user);  //存入这个用户信息
            session.setAttribute("index",1);//每次注册成功，刷新显示第 1 页
            session.setAttribute("keyword",""); //默认keyword关键字为 ""
            session.setAttribute("update_key",false); //默认update_key=false
            model.addAttribute("user",user);
            return "redirect:regist_success.html";   //跳转到注册成功页面
        }else{//注册失败
            return "regist"; //返回到注册页面
        }
    }

    //储存用户登录在线

    public void saveuser(){

    }

    //进入主页列表
    @GetMapping("/to_votelist")
    public String vote_list(HttpSession session,Model model,Integer index,String keyword,boolean update_key,boolean resetting){

//        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //进行存储session
//        ServletContext application=session.getServletContext();
//        //application在线用户集合
//        Set<HttpSession> session_Set = (Set<HttpSession>)application.getAttribute("session_Set");
//        //将本session添加到application的在线session集合中
//        session_Set.add(session);
//        //存入application作用域中（更新数据）
//        application.setAttribute("session_Set",session_Set);
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //判断是否登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }


        if (index==null){
            System.out.println("session_index=="+session.getAttribute("index"));
            index= (Integer) session.getAttribute("index");
        }
        model.addAttribute("user",user);

        //重置标识符=true,(重置)
        if (resetting){
            session.setAttribute("update_key",false); //修改标识重置
            session.setAttribute("keyword","");  //关键字重置
        }

        //搜索关键字为空
        if(keyword==null){
            keyword= (String) session.getAttribute("keyword");
        }
        //如果编辑标识符update_key=false
        if(!update_key){
            update_key=(Boolean) session.getAttribute("update_key");
        }
        //当执行编辑操作时update=true
        if (update_key){
            //判断是否是管理员leven=1
            if(user.getLevel()!=1){ //不是管理员，跳转到错误提示页面
                //跳转到结果页面的target标识符=no_admin
                model.addAttribute("target","no_admin");
                return "play_info";
            }
        }
        System.out.println("update_key==="+update_key);


        model.addAttribute("update_key",update_key);    //存入修改标识
        model.addAttribute("keyword",keyword);  //存入关键字

        session.setAttribute("keyword",keyword);   //存入keyword关键字
        session.setAttribute("update_key",update_key);  //存入修改标识符
        //打印测试数据
//        Page<Subject> sbj_page = ssi.FindPageSubject(index);
//        sbj_page.forEach(s->{
//            System.out.println(s);
//        });

        Page<Subject> page=ssi.FindPageSubject(index,new QueryWrapper<Subject>().like("s.title",keyword));
        session.setAttribute("index",index);
        model.addAttribute("page",page);
        return "votelist";
    }

    //进入查看主题投票信息
    @GetMapping("/FindSubject_Info")
    public String FindSubjectInfo(Model model,Integer sid,HttpSession session){

        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }

        Subject subject = ssi.FindPageSubject(1,new QueryWrapper<Subject>().eq("s.sid",sid)).get(0);
        System.out.println(subject);
        model.addAttribute("subject",subject);
        model.addAttribute("user",user);
        return "voteview";
    }

    //这是进入参与投票页面
    @GetMapping("/to_vote")
    public  String To_Vote(HttpSession session,Model model,Integer sid){
        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }

        Subject subject = ssi.FindPageSubject(1,new QueryWrapper<Subject>().eq("s.sid",sid)).get(0);
        System.out.println(subject);
        model.addAttribute("subject",subject);
        model.addAttribute("user",user);
        return "vote";
    }


    //这里是执行投票
    @PostMapping("/addlogsheet")
    public String AddlogSheet(Model model,Integer sid,HttpSession session,Integer [] options){

        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }

        model.addAttribute("user",user);

        //判断是否还有机会
        boolean chance=lsi.getBaseMapper().exists(new QueryWrapper<Logsheet>().eq("uid",user.getUid()).eq("sid",sid).eq("del",0));

        //还有机会投票
        if(!chance){ //进行投票记录新增

            //通过循环遍历创建logsheet记录对象和赋值， 并且执行新增 logsheet
            for(int i=0;i<options.length;i++){
                //创建记录对象
                Logsheet logsheet=new Logsheet(0,user.getUid(),options[i],sid);
                //执行记录新增
                lsi.getBaseMapper().insert(logsheet);
            }

        }

        //存入用户信息=user、是否还有机会=chance、主题编号=sid
        model.addAttribute("chance",chance);
        model.addAttribute("user",user);
        model.addAttribute("sid",sid);
        model.addAttribute("target","vote"); //这是判断目标显示（标识符）

        return "play_info";
    }


    //执行跳转到新增主题页面
    @GetMapping("/to_addsubject")
    public String to_addsubject(HttpSession session,Model model){
        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }



        System.out.println("user==="+user);
        //判断是否是管理员leven=1
        if(user.getLevel()!=1){ //不是管理员，跳转到错误提示页面
            //跳转到结果页面的target标识符=no_admin
            model.addAttribute("target","no_admin");
            model.addAttribute("user",user); //显示用户
            return "play_info";
        }

        model.addAttribute("user",user); //显示用户
        model.addAttribute("edit","add"); //编辑类型标识符  edit=add(执行新增主题)

        return "edit_subject";
    }

    //执行主题新增操作
    // 返回标识符  add_sbj   -1=主题标题同名   0=新增失败    1=新增成功
    @PostMapping("/add_subject")
    public String save_subject(Model model,Subject subject, HttpSession session, @RequestParam("cname") String [] options){

        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }
        model.addAttribute("user",user); //无论是否成功，都需要user用户对象
        model.addAttribute("target","add_sbj"); //本次操作结果的标识符，用于html模板判断显示结果

        //判断主题标题是否已存在重复
        boolean exist=ssi.getBaseMapper().exists(new QueryWrapper<Subject>().eq("del",0).eq("title",subject.getTitle()));

        System.out.println("Subject===>>>"+subject);

        //主题标题已存在！阻止新增
        if (exist){
            model.addAttribute("add_sbj",-1);
            return "play_info";  //不执行以下操作，直接跳转至结果页面
        }

        //将新增同名称的选项过滤
        List<String> new_option=new ArrayList<String>();

        //遍历原数组
        for(int i = 0; i<options.length; i++){

            if (i==0){
                new_option.add(options[i]);
            }

            boolean have=false; //默认不存在
            //新数组
            for(int j = 0; j<new_option.size(); j++){
                //原数组和新数组元素值相同结束内循环
                if(options[i] .equals(new_option.get(j))){
                   have=true;
                }

            }
            //如果不存在，则存入new_option集合
            if(!have){
                new_option.add(options[i]);
            }
        }



        //如果新增数据小于2 (add_sbj=-2)
        if (new_option.size()<2){
            model.addAttribute("add_sbj",-2);
            return "play_info";  //不执行以下操作，直接跳转至结果页面
        }

        //执行新增主题
        int add_sbj= ssi.getBaseMapper().insert(subject);  //若新增成功，会返回主键sid

        //主题新增成功！！执行新增选项👇👇
        if(add_sbj>0){
            int sid=subject.getSid();//获取到主题编号sid
            //通过遍历new_option数组，执行新增选项
            for (int i=0;i<new_option.size();i++){
                //创建选择实体数据
                Choose choose=new Choose();
                choose.setCname(new_option.get(i));
                choose.setSid(sid);
                //执行新增选项
                add_sbj=csi.getBaseMapper().insert(choose);
            }

        }
        model.addAttribute("add_sbj",add_sbj); //等待新增执行完毕后，返回结果，用于判断结果提示

        return "play_info";
    }

    //执行删除主题操作
    @GetMapping("del_subject")
    public String del_subject(HttpSession session,Model model,Integer sid){
        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }

        //执行删除主题顺序 （删除记录--->删除选项--->删除主题）
        //结果判断标识符  target=del_subject
        // 是否成功？ del_subject   false:删除失败  true：删除成功
        boolean del_subject=false; //默认删除失败！！！

        //判断记录表中是否存在该主题的记录
         boolean exist_log= lsi.getBaseMapper().exists(new QueryWrapper<Logsheet>().eq("sid",sid).eq("del",0));
        //执行删除记录表
        if(exist_log){
            UpdateWrapper<Logsheet> logsheetUpdateWrapper=new UpdateWrapper<>();
            logsheetUpdateWrapper.set("del",1).eq("sid",sid);
            int del_logsheet=lsi.getBaseMapper().update(null,logsheetUpdateWrapper);
        }

        //删除记录表成功，执行下一步删除（删除选项表数据）
        //删除选项表
        UpdateWrapper<Choose> chooseUpdateWrapper=new UpdateWrapper<>();
        chooseUpdateWrapper.set("del",1).eq("sid",sid);
        int del_choose=csi.getBaseMapper().update(null,chooseUpdateWrapper);

        //如果删除选项表成功，最后删除主题表
        if(del_choose>0){
            //删除主题表
            UpdateWrapper<Subject> subjectUpdateWrapper=new UpdateWrapper<>();
               subjectUpdateWrapper.set("del",1).eq("sid",sid);
               int del_sbj=ssi.getBaseMapper().update(null,subjectUpdateWrapper);
               //如果最后一步删除成功，返回删除成功值true
                if(del_sbj>0){
                    del_subject=true;
                }
        }


        model.addAttribute("user",user);
        model.addAttribute("target","del_subject");
        model.addAttribute("del_subject",del_subject);
        return "play_info";//跳转到结果显示页面
    }

    //跳转到修改主题页面
    @GetMapping("/to_update_subject")
    public String update_subject(Model model,HttpSession session,Integer sid){
        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }

        Subject subject = ssi.FindPageSubject(1,new QueryWrapper<Subject>().eq("s.sid",sid)).get(0);
        System.out.println(subject);
        model.addAttribute("subject",subject);
        model.addAttribute("user",user);
        model.addAttribute("edit","update"); //编辑类型标识符  edit=add(执行新增主题)
        return "edit_subject";

    }

    //执行修改主题
    @PostMapping("/update_subject")
//    @ResponseBody
    public String update_subject(Model model,HttpSession session,Subject subject,String old_title,@RequestParam(value = "cid",required = false) List<Integer> cids,@RequestParam(value = "cname",required = false) List<String> cnames){
        //判断是否已存在登录
        User user=(User) session.getAttribute("user");
        if (user==null){
            System.out.println("你还未登录~~~");
            return "redirect:login.html"; //回到登录页面
        }

        //这个页面将要返回去play_info页面时的标识 target="update_subject"
        // 提示内容标识 update_subject= (1:修改成功 ;  0:选项数量小于2)  默认=1 成功
        model.addAttribute("target","update_subject");
        model.addAttribute("update_subject",1);
        model.addAttribute("user",user); //需要显示用户
        model.addAttribute("sid",subject.getSid()); //主题编号 sid

        //如果主题名除原名以外还存在重复 update_subject= -2
        boolean title_exsit= ssi.getBaseMapper().exists(new QueryWrapper<Subject>().ne("title",old_title).eq("del",0).eq("title",subject.getTitle()));
        if(title_exsit){
            model.addAttribute("update_subject",-2);
            return "play_info";
        }

        //如果cids==null (停止后面的操作，直接跳转至》》提示结果页面: play_info.html)   update_subject= -1
        if(cids==null){
            model.addAttribute("update_subject",-1);
            return "play_info";
        }



        //*1页面提交的Choose选项对象集合储存(过滤掉同名称的选项)  《！！！！！重点：实则就是将在数据库中 》》》该主题下的最新全部选项》
        List<Choose> form_chooses=new ArrayList<>();
        //*2通过主题编号sid获取到该主题下的所有选项===>得到>>>>list<Choose> table_chooses（查询选项表）
        List<Choose> table_chooses= csi.getBaseMapper().selectList(new QueryWrapper<Choose>().eq("del",0).eq("sid",subject.getSid()));
        //*3合法数据集合储存(~中包括修改和新增的集合，!!!还需再进一步过滤拆分得到 修改 和 新增!!! )
            // ====list<Choose> update_add_chooses
        List<Choose> update_add_chooses=new ArrayList<>();
        //*4待新增的Choose集合===add_chooses (!!!需要在update_add_chooses集合中分离出来)
        List<Choose> add_chooses=new ArrayList<>();
        //*5待修改的Choose集合===update_chooses (!!!需要在update_add_chooses集合中分离出来)
        List<Choose> update_chooses=new ArrayList<>();
        //*6待删除的cid集合===del_cids
        List<Integer> del_cids=new ArrayList<>();

        //f1通过循环来注入到Choose对象,再注入到集合form_chooses中（边存+边筛选）
        for (int i = 0; i <cids.size() ; i++) {
            Integer cid=cids.get(i);
            String cname=cnames.get(i);

            //通过循环这个chooses筛选出同名的选项名
            boolean pass=true; //筛选标识（是否通过？ true=通过   false=不通过）
            for (int j=0;j<form_chooses.size();j++){
                if(cname.equals(form_chooses.get(j).getCname())){//匹配到重名
                        pass=false; //不通过
                    break;
                }
            }
            //不通过
            if(!pass){
                continue; //跳过本次
            }
            //创建Choose对象储存数据
            Choose c=new Choose(cid,cname,subject.getSid(),0);
            //再将Choose对象存入集合form_chooses中
            form_chooses.add(c);
        }

        //如果将要更新的总数据小于 2 (停止后面的操作，直接跳转至》》提示结果页面: play_info.html)
        if(form_chooses.size()<2){
            model.addAttribute("update_subject",0);
            return "play_info";
        }


        //f2通过循环form_chooses集合，内嵌循环table_chooses集合过滤掉同选择名称的对象（就说明，该选项未做改变）
        //条件原理： cname=#{cname}
        for (Choose form_c:form_chooses) {//循环form_chooses

            boolean pass=true;//默认通过=true
            //循环table_chooses过滤同选项名称
            for (Choose table_c:table_chooses) {//循环table_chooses
                if(form_c.getCname().equals(table_c.getCname())){ //选项名称相同（需要过滤掉）
                    //pass改为不通过==false
                    pass=false;
                    break;
                }
            }
            //不通过
            if (!pass){
                continue;//跳过
            }

            //否则，顺利存入 update_add_chooses集合中
            update_add_chooses.add(form_c);
        }

        //f3通过循环update_add_chooses集合，内嵌循环table_chooses集合过滤得到 1待修改数据 和 2待新增数据
        //条件原理：1.待修改的数据 cid=#{cid} and cname!=#{cname}  2.待新增的数据：cid!=#{cid} and cname!=#{cname}
        for (Choose update_add_c:update_add_chooses) {

            boolean add=true; //默认执行待新增数据过滤

            //内嵌table_chooses集合过滤 得到  1.待修改数据对象
            for (Choose table_c:table_chooses) {

//                int exist=table_chooses.size(); //默认在table_chooses集合中存在的次数
                //过滤得到待修改数据
                if(update_add_c.getCid()==table_c.getCid() && !update_add_c.getCname().equals(table_c.getCname())){
                    System.err.println("一定要看这里！！！！！");
                    System.out.println("table_c.cid==="+table_c.getCid()+"\t update_c.cid==="+update_add_c.getCid());
                    System.out.println("table_c.cname==="+table_c.getCname()+"\t update_c.cname==="+update_add_c.getCname());

                    update_chooses.add(update_add_c);
                    add=false;
                    break;
                }

            }
            //不执行新增数据过滤
            if(!add){
                continue;
            }
            //否则执行新增数据过滤👇👇👇
            //内嵌table_chooses集合过滤 得到  2.待新增数据对象
            for (Choose table_c:table_chooses) {
                //过滤得到待修改数据
                if(update_add_c.getCid()!=table_c.getCid() && !update_add_c.getCname().equals(table_c.getCname())){
                    add_chooses.add(update_add_c);
                    break;
                }
            }


        }

        //f4通过循环table_chooses集合，内嵌form_chooses循环集合过滤得到 待删除数据对象==cid
        //条件原理:  table_cid==form_chooses中的cid "或者" table_cname==form_chooses中的cname
        // （!!!重点!!!: exist=false  存入这个对象cid）
        for (Choose table_c:table_chooses) {

            boolean exist=false; //默认该对象不存在
            for (Choose form_c:form_chooses) {
                if (table_c.getCid()==form_c.getCid() || table_c.getCname().equals(form_c.getCname())) {
                    exist=true;
                }
            }
            //重点：：：如果不存在,则将这个table_c中的cid 存入del_cids集合
            if(!exist){
                del_cids.add(table_c.getCid());
            }

        }

        //执行修改主题
        UpdateWrapper<Subject> subjectUpdateWrapper=new UpdateWrapper<>();
        subjectUpdateWrapper.set("title",subject.getTitle()).set("stype",subject.getStype()).eq("sid",subject.getSid());
        ssi.getBaseMapper().update(null,subjectUpdateWrapper);

        //执行新增
        add_chooses.forEach(add->{
            csi.getBaseMapper().insert(add);
        });


        //执行删除
        del_cids.forEach(del->{
            //先删除记录表
            UpdateWrapper<Logsheet> logsheetUpdateWrapper=new UpdateWrapper<>();
            logsheetUpdateWrapper.set("del",1).eq("cid",del);
            lsi.getBaseMapper().update(null,logsheetUpdateWrapper);
            //再删除》》选项
            UpdateWrapper<Choose> chooseUpdateWrapper=new UpdateWrapper<>();
            chooseUpdateWrapper.set("del",1).eq("cid",del);
            csi.getBaseMapper().update(null,chooseUpdateWrapper);

        });

        //执行修改
        update_chooses.forEach(update->{
            UpdateWrapper<Choose> chooseUpdateWrapper=new UpdateWrapper<>();
            chooseUpdateWrapper.set("cname",update.getCname()).eq("cid",update.getCid());
            csi.getBaseMapper().update(null,chooseUpdateWrapper);
        });

        return "play_info";
    }


}
