package com.ls.demo.service.impl;

import cn.hutool.json.JSONUtil;
import com.ls.demo.config.utils.JwtTokenUtil;
import com.ls.demo.dao.UmsAdminRoleRelationDao;
import com.ls.demo.mbg.mapper.UmsAdminMapper;
import com.ls.demo.mbg.model.UmsAdmin;
import com.ls.demo.mbg.model.UmsAdminExample;
import com.ls.demo.mbg.model.UmsPermission;


import com.ls.demo.service.UmsAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * UmsAdminService实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;//redis

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        //先查询redis没有查询到数据后在查询数据库
        UmsAdmin umsAdmin = null;
        umsAdmin = (UmsAdmin) redisTemplate.opsForValue().get("username:"+username);
        //没有查询到数据
        if(umsAdmin==null){
            UmsAdminExample example = new UmsAdminExample();
            example.createCriteria().andUsernameEqualTo(username);
            umsAdmin = adminMapper.selectByUserName(username);
            if (umsAdmin != null ) {
                System.out.println("从数据库查询出来的数据:"+ JSONUtil.toJsonStr(umsAdmin));
                redisTemplate.opsForValue().set("username:"+username,umsAdmin);
                return umsAdmin;
            }else{
                System.out.println("从数据库查询为空:"+ JSONUtil.toJsonStr(umsAdmin));
            }
        }else{
            return umsAdmin;
        }
        return null;
    }

    @Override
    public UmsAdmin register(UmsAdmin umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        UmsAdmin umsAdmin1 = adminMapper.selectByUserName(umsAdmin.getUsername());
        if (umsAdmin1 != null) {
            System.out.println("查询到相同用户名");
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        int insert = adminMapper.insert(umsAdmin);
        System.out.println("插入数据条数:insert:"+insert);
        return umsAdmin;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }


    @Override
    public List<UmsPermission> getPermissionList(Long adminId) {
        return adminRoleRelationDao.getPermissionList(adminId);
    }
}
