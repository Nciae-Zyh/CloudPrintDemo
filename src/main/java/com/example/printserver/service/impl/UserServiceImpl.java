package com.example.printserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.printserver.mapper.*;
import com.example.printserver.pojo.LoginMessage;
import com.example.printserver.pojo.RegisterMessage;
import com.example.printserver.pojo.dao.*;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {
    UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    CustomerMapper customerMapper;

    @Autowired
    public void setCustomerMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    ShopMapper shopMapper;

    @Autowired
    public void setShopMapper(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    CustomerViewMapper customerViewMapper;

    @Autowired
    public void setCustomerViewMapper(CustomerViewMapper customerViewMapper) {
        this.customerViewMapper = customerViewMapper;
    }

    ShopViewMapper shopViewMapper;

    @Autowired
    public void setShopViewMapper(ShopViewMapper shopViewMapper) {
        this.shopViewMapper = shopViewMapper;
    }

    /**
     * 登录功能
     *
     * @param loginMessage 登录信息
     * @param request      HttpRequest，用于在session添加用户信息
     * @return 登录的用户类型以及用户数据
     */
    public CommonResult login(LoginMessage loginMessage, HttpServletRequest request) {
        User user = checkUser(loginMessage.getPhoneNumber());
        if (user != null && user.getPassword().equals(loginMessage.getPassword())) {
            HttpSession session = request.getSession();
            if (user.getUserType() == '1') {
                CustomerView customer = customerViewMapper.selectById(user.getUid());
                session.setAttribute("user", customer);
                return CommonResult.success(customer, "customer");
            } else if (user.getUserType() == '2') {
                ShopView shop = shopViewMapper.selectById(user.getUid());
                session.setAttribute("user", shop);
                return CommonResult.success(shop, "shop");
            } else {
                /**
                 *  目前管理员页面暂未书写，留白。
                 */
                return CommonResult.failed("暂未设置管理员登陆页面！");
            }
        } else {
            return CommonResult.validateFailed("用户名或密码错误!");
        }
    }

    /**
     * 顾客注册功能
     *
     * @param customer 顾客注册时的信息表单
     * @return 注册成功的顾客信息
     */
    public CommonResult customerRegister(RegisterMessage customer) {
        User user = checkUser(customer.getPhoneNumber());
        if (user != null) {
            log.error("手机号" + customer.getPhoneNumber() + "已被注册");
            return CommonResult.failed("手机号" + customer.getPhoneNumber() + "已被注册");
        } else {
            user = new User();
            user.setUserType('1');
            user.setPassword(customer.getPassword());
            user.setPhoneNumber(customer.getPhoneNumber());
            userMapper.insert(user);
            Customer customer1 = new Customer();
            customer1.setUid(user.getUid());
            customer1.setName(customer.getName());
            customerMapper.insert(customer1);
            return CommonResult.success(customer1, "注册成功！");
        }
    }

    /**
     * 店铺注册
     *
     * @param shopView 店铺注册时的信息表单。
     * @return 返回注册成功的店铺信息
     */
    public CommonResult shopRegister(RegisterMessage shopView) {
        User user = checkUser(shopView.getPhoneNumber());
        if (user != null) {
            log.error("手机号" + shopView.getPhoneNumber() + "已被注册");
            return CommonResult.failed("手机号" + shopView.getPhoneNumber() + "已被注册");
        } else {
            user = new User();
            user.setUserType('2');
            user.setPassword(shopView.getPassword());
            user.setPhoneNumber(shopView.getPhoneNumber());
            userMapper.insert(user);
            Shop shop = new Shop();
            shop.setUid(user.getUid());
            shop.setName(shopView.getName());
            shop.setIsColor(shopView.getIsColor());
            shop.setIsDuplex(shopView.getIsDuplex());
            shopMapper.insert(shop);
            return CommonResult.success(shop, "注册成功");
        }
    }

    @Override
    public ShopView getShop(String uid) {
        return shopViewMapper.selectById(Integer.valueOf(uid));
    }

    private User checkUser(String phoneNumber) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone_number", phoneNumber);
        return userMapper.selectOne(wrapper);
    }
}
