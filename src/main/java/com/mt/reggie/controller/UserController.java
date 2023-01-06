package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mt.reggie.common.R;
import com.mt.reggie.entity.User;
import com.mt.reggie.service.UserService;
import com.mt.reggie.utils.SMSUtils;
import com.mt.reggie.utils.UserNameGenerateUtils;
import com.mt.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpServletRequest request,@RequestBody User user){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //需要将生成的验证码保存到Session
            //request.getSession().setAttribute(phone,code);
            //将验证码保存到redis,有效期5分钟
            redisTemplate.opsForValue().set(phone,code,5,TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(HttpServletRequest request,@RequestBody Map map){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        //Object codeInSession = request.getSession().getAttribute(phone);
        //从redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        System.out.println(codeInSession);
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                String userNameByChineseSimp = UserNameGenerateUtils.getRandomChineseSimp(5);
                user.setName(userNameByChineseSimp);
                userService.save(user);
            }
            request.getSession().setAttribute("user",user.getId());
            //优化->  redis中删除验证码
            redisTemplate.delete(phone);
            /*
            1.通过线程池的方式来删除验证码
            2.可以设定时间颗粒度
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                        //session中删除验证码
                        request.getSession().removeAttribute("code");
                        //优化->  redis中删除验证码
                }
            };
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            //立即执行,10分钟循环执行
            ses.scheduleAtFixedRate(runnable, 10, 10, TimeUnit.MINUTES);*/
            return R.success(user);
        }
        return R.error("登录失败");
    }

    //根据id查询表
    @GetMapping
    public R<User> getById(String phone){
        System.out.println("123141415125151");
        //创建条件构造器
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(phone!=null,User::getPhone,phone);
        User user = userService.getOne(lqw);
        return R.success(user);
    }
}
