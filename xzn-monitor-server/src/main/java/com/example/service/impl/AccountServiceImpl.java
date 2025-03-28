package com.example.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountDO;
import com.example.entity.vo.request.ChangePasswordReqDTO;
import com.example.entity.vo.request.ConfirmResetReqDTO;
import com.example.entity.vo.request.CreateSubAccountReqDTO;
import com.example.entity.vo.request.EmailResetReqDTO;
import com.example.entity.vo.request.ModifyEmailReqDTO;
import com.example.entity.vo.response.SubAccountRespDTO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 账户信息处理相关服务
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {

    //验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    AmqpTemplate rabbitTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    FlowUtils flow;
    @Resource
    JwtUtils jwtUtils;

    @Override
    public Boolean logout(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        if(jwtUtils.invalidateJwt(authorization)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(int id, ChangePasswordReqDTO requestParam) {
        AccountDO account = this.getById(id);
        String password = account.getPassword();
        if (!passwordEncoder.matches(requestParam.getPassword(),password))return false;
        boolean update = this.update(Wrappers.lambdaUpdate(AccountDO.class)
            .eq(AccountDO::getId, id)
            .set(AccountDO::getPassword, passwordEncoder.encode(requestParam.getNewPassword())));
        return update;
    }

    @Override
    public void createSubAccount(CreateSubAccountReqDTO requestParam) {
        AccountDO account = this.findAccountByNameOrEmail(requestParam.getEmail());
        if (account != null){
            throw new IllegalArgumentException("该电子邮件已被注册");
        }
        account = this.findAccountByNameOrEmail(requestParam.getUsername());
        if (account != null){
            throw new IllegalArgumentException("该用户名已被注册");
        }
        account = new AccountDO(null, requestParam.getUsername(),
            passwordEncoder.encode(requestParam.getPassword()), requestParam.getEmail(), Const.ROLE_NORMAL,
            JSONArray.copyOf(requestParam.getClients()).toJSONString(), new Date());
        this.save(account);
    }

    @Override
    public void deleteSubAccount(int id) {
        this.removeById(id);
    }

    @Override
    public String modifyEmail(int id,ModifyEmailReqDTO requestParam) {
        String code = getEmailVerifyCode(requestParam.getEmail());
        if (code == null)return "请先获取验证码";
        if(!code.equals(requestParam.getCode()))return "验证码错误，请重新输入";
        this.deleteEmailVerifyCode(requestParam.getEmail());
        AccountDO account = this.findAccountByNameOrEmail(requestParam.getEmail());
        if (account != null && account.getId() != id)return "该电子邮件已被注册";
        this.update(Wrappers.lambdaUpdate(AccountDO.class)
            .eq(AccountDO::getEmail,requestParam.getEmail())
            .set(AccountDO::getEmail,requestParam.getEmail()));
        return null;
    }

    /**
     * 获取子账户列表
     * @return 子账户响应数据对象列表
     */
    @Override
    public List<SubAccountRespDTO> subAccountList() {
        // 查询角色为普通用户的账户列表，并转换为SubAccountRespDTO对象列表
        // 通过lambda表达式和流操作处理查询结果，确保只选择符合条件的账户
        // 使用BeanUtil进行对象类型转换，简化代码并提高可读性
        // 解析账户中的客户信息，使用JSONArray进行解析，将字符串转换为列表
        return this.list(Wrappers.lambdaQuery(AccountDO.class)
            .eq(AccountDO::getRole,Const.ROLE_NORMAL))
            .stream().map(account ->{
                SubAccountRespDTO bean = BeanUtil.toBean(account, SubAccountRespDTO.class);
                bean.setClientList(JSONArray.parse(account.getClients()));
                return bean;
            }).toList();
    }



    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDO accountDO = this.findAccountByNameOrEmail(username);
        if(accountDO == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(accountDO.getPassword())
                .roles(accountDO.getRole())
                .build();
    }

    /**
     * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
     * @param type 类型
     * @param email 邮件地址
     * @param address 请求IP地址
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String registerEmailVerifyCode(String type, String email, String address){
        synchronized (address.intern()) {
            if(!this.verifyLimit(address))
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type",type,"email", email, "code", code);
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     * @param info 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetEmailAccountPassword(EmailResetReqDTO info) {
        String verify = resetConfirm(new ConfirmResetReqDTO(info.getEmail(), info.getCode()));
        if(verify != null) return verify;
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if(update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     * @param info 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetConfirm(ConfirmResetReqDTO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(info.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    /**
     * 移除Redis中存储的邮件验证码
     * @param email 电邮
     */
    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     * @param address 地址
     * @return 是否通过验证
     */
    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    /**
     * 通过用户名或邮件地址查找用户
     * @param text 用户名或邮件
     * @return 账户实体
     */
    @Override
    public AccountDO findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }


}
