package com.chen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.R;
import com.chen.pojo.Employee;
import com.chen.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /*
     * 员工登录
     * */

    @PostMapping("/login")
    R<Employee> login(HttpServletRequest request, @RequestBody Employee employee, @RequestParam(value = "checked", required = false) boolean checked, HttpServletResponse response) {
        String password = employee.getPassword();
        String username = employee.getUsername();
//        if(checked){
//            Cookie cookie=new Cookie("username",employee.getUsername());
//            cookie.setMaxAge(60*60*24*7);
//            cookie.setPath(request.getContextPath());
//            response.addCookie(cookie);
//        }
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, username);
        Employee one = employeeService.getOne(wrapper);

        if (one == null) {
            return R.error("用户不存在！");
        } else if (!password.equals(one.getPassword())) {
            return R.error("密码错误");
        } else if (one.getStatus() == 0) {
            return R.error("该人已离职");
        } else {
            request.getSession().setAttribute("employee", one.getId());
            return R.success(one);
        }

    }
    /*
    员工退出登录
    */

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /*
     * 增加员工
     * 设置初始密码123456
     * */
    @PostMapping
    public R<String> add(@RequestBody Employee employee, HttpServletRequest request) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        boolean save = employeeService.save(employee);
        return R.success("添加成功！");

    }

    /*
     * 分页查询
     * */
    @GetMapping("/page")
    public R<Page> selectByPage(@RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam(required = false) String name) {
        Page page1 = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(page1, lambdaQueryWrapper);
        return R.success(page1);

    }

    /*
     * 编辑回显数据
     * */
    @GetMapping("/{id}")
    public R<Employee> getOne(@PathVariable int id) {
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getId, id);
        Employee one = employeeService.getOne(lambdaQueryWrapper);
        return R.success(one);
    }

    /*
     * 编辑操作
     * */
    @PutMapping
    public R<String> employeeEdit(@RequestBody Employee employee, HttpServletRequest request) {
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        boolean b = employeeService.updateById(employee);
        if (b) {
            return R.success(employee.getUsername() + "信息修改成功");
        } else {
            return R.error("网络异常");
        }
    }


}
