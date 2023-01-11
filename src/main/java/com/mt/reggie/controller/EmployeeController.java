package com.mt.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mt.reggie.common.R;
import com.mt.reggie.entity.Employee;
import com.mt.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/employee")
@Api(tags = "登录相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //登录操作
    @ApiOperation("登录")
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //根据传递过来的信息,先判断姓名在不在数据库
        String password = employee.getPassword();
        //将页面提交的密码password进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //设置查询条件
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        //查询结果为空则登录失败
        if(emp==null){
            return R.error("用户名不存在,登录失败");
        }
        //不为空则比对密码是否一致,
        if(emp.getPassword().equals(password)){
            if (emp.getStatus()!=0){
                request.getSession().setAttribute("employee",emp.getId());
                return R.success(emp);
            }else {
                return R.error("账号已禁用");
            }
        }else{
            return R.error("账号密码错误,登录失败");
        }
    }

    //退出操作
    @ApiOperation("退出")
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //删掉保存在localStorage里面的用户id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    //添加员工
    @ApiOperation("添加员工信息")
    @PostMapping
    public R<String> add(HttpServletRequest request,@RequestBody Employee employee){
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getName,employee.getName());
        List<Employee> list = employeeService.list(lqw);
        if(list!=null&&list.size()>0){
            return R.error("用户名已存在,请重新输入");
        }
        //设置初始密码,并加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*//设置创造时间
        employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //获取创建人ID信息,需要强转
        Long id = (Long) request.getSession().getAttribute("employee");
        //封装创建人Id
        employee.setCreateUser(id);
        //封装更新人Id
        employee.setUpdateUser(id);*/
        employeeService.save(employee);
        return R.success("添加成功");
    }

    //分页查询
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public R<IPage<Employee>> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        IPage<Employee> pageInfo = new Page<>();
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lqw.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    //数据回显的根据ID查询
    @ApiOperation("根据id查询")
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

    //编辑员工信息
    @ApiOperation("编辑员工信息")
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        /*employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/
        boolean flag = employeeService.updateById(employee);
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }

    //删除员工信息
    @ApiOperation("删除员工信息")
    @DeleteMapping
    public R<String> delete(@RequestParam("id") Long id){
        //创建条件查询器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(id!=null,Employee::getId,id);
        Employee employee = employeeService.getOne(lqw);
        if(!employee.getName().equals("admin")){
            boolean delete_flag = employeeService.removeById(id);
            return delete_flag?R.success("删除成功"):R.error("删除失败");
        }
        return R.error("错误,无法删除管理员账号");
    }
}
