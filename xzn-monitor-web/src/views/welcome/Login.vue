<template>
<div slot="login">
  <img class="login-qr" :src="loginQR" />
  <div class="login-title">账号登录</div>
  <el-form ref="formRef" class="login-form" :model="form" :rules="rules">
    <el-form-item prop="username">
      <el-input type="text" style="height: 42px" v-model="form.username" placeholder="请输入用户名/邮箱">
        <template #prefix>
          <UserOutlined />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
          style="height: 42px"
          v-model="form.password"
          autocomplete="on"
          type="password"
          placeholder="请输入密码：最小 6 位"
      >
        <template #prefix>
          <LockOutlined />
        </template>
      </el-input>
    </el-form-item>
    <el-row>
      <!-- 验证码输入框部分 -->
      <el-col :span="12" style="text-align: left; ">
        <el-form-item prop="code">
          <el-input style="height: 42px" class="captcha-input" v-model="form.code" placeholder="请输入验证码">
            <template #prefix>
              <SafetyCertificateOutlined />
            </template>
          </el-input>
        </el-form-item>
      </el-col>

      <!-- 验证码图片部分 -->
      <el-col :span="12" style="text-align: right;">
        <PicCode :width="180" :height="40" v-model:Code="Code" />
      </el-col>
    </el-row>

    <el-row>
      <!-- 左侧记住密码 -->
      <el-col prop="remember" :span="12" style="text-align: left;">
        <el-form-item name="remember">
          <el-checkbox v-model="form.remember">记住密码</el-checkbox>
        </el-form-item>
      </el-col>

      <!-- 右侧忘记密码 -->
      <el-col :span="11" style="text-align: right;">
        <el-link @click="router.push('/reset')">忘记密码?</el-link>
      </el-col>
    </el-row>

    <el-form-item style="margin-top: 55px">
      <el-button  @click="userLogin()" style = "height:40px;width: 350px" color="#1e1efa"  plain>登录</el-button>
    </el-form-item>
  </el-form>
</div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import loginQR from '../../assets/images/login-qr.png';
import PicCode from "../../components/PicCode/PicCode.vue";
import { UserOutlined,LockOutlined ,SafetyCertificateOutlined } from '@ant-design/icons-vue';
import {login} from "../../net/index.js";
import {ElMessage} from "element-plus";
import { useRouter } from 'vue-router'
const router = useRouter()
//--------------------- 登录表单 ---------------------------------
const formRef = ref()
const Code = ref(1);
const showPassword = ref(false);
const form= reactive({
  username: '',
  password: '',
  code: '',
  remember: false
})
const isDarkTheme = window.matchMedia("(prefers-color-scheme: light)"); // 是深色
if (isDarkTheme.matches) { // 是深色
                       // 主题设置为深色。
} else { // 不是深色
         // 主题设置为浅色。
}

const rules = {
  username: [{ required: true, message: '用户名不能为空' }],
  password: [{ required: true, message: '密码不能为空' }],
  code: [{ required: true, message: '验证码不能为空' }],
};

function userLogin(){
  formRef.value.validate((valid) => {
    if(valid){
      if(Code.value === form.code){
        login(form.username,form.password,form.remember,()=>{router.push("/index")})
      }else{
        ElMessage.error("验证码错误，请重新输入")
      }
    }
  })
}
function userRegister(){
  router.push({
    name: 'welcome-register'
  })
}

</script>
<style lang="less" scoped>
@import 'login.less';


</style>