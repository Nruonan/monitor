<script setup>
import {reactive, ref,computed} from "vue";
import {LockOutlined, MailOutlined, SafetyCertificateOutlined} from "@ant-design/icons-vue";
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {get, post} from "../../net/index.js";

const router = useRouter()
const coldTime = ref(0)
let active = ref(0)
const formRef = ref()
const form = reactive({
  email:'',
  code:'',
  password:'',
  password_repeat:''
})

const validatePassword = (rule, value, callback) =>{
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}
const rules_a = {
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
  ]
};
const rules_B ={
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ],
}
function  askCode(){
  if(isEmailValid){
    coldTime.value=60
    get(`/api/auth/ask-code?email=${form.email}&type=reset`,() =>{
      ElMessage.success('验证码已发送到邮箱，请注意查收')
      setInterval (() => coldTime.value--,1000)
    },(message) => {
      ElMessage.warning(message)
      coldTime.value = 0
    })
  }else{
    ElMessage.warning('请输入正确的电子邮件！')
    coldTime.value = 0
  }
}
const isEmailValid = computed(() =>/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email) )
const resetConfirm = function (){
  formRef.value.validate(valid =>{
    if(valid){
      post(`/api/auth/reset-confirm`,{
        email: form.email,
        code: form.code
      },()=>active.value++)
    }
  })
}

const resetPassword = function (){
  formRef.value.validate(valid =>{
    if(valid){
      post('/api/auth/reset-password',{...form},()=>{
        ElMessage.success('密码重置成功，欢迎回来')
        router.push('/')
      })
    }
  })
}
</script>

<template>
  <div style="text-align: center;">
    <div style="margin-top: 5%">
      <el-steps :active="active" align-center finish-status="success" >
        <el-step title="验证电子邮件"/>
        <el-step title="重新设定密码"/>
      </el-steps>
    </div>
    <div v-if="active === 0" style="margin: 0 20px">
      <div >
        <div style="font-size:30px;font-weight:bold ;margin-top: 20px">重置密码</div>
        <div  style="font-size:16px; color: grey" >请输入需要重置密码的电子邮件地址</div>
      </div>
      <div style="margin-top: 40px">
        <el-form :model="form" :rules="rules_a" ref="formRef">
          <el-form-item prop="email">
                <el-input style="text-align: left; height: 42px; width: 400px ;" type="text"  v-model="form.email" placeholder="请输入邮箱地址">
                  <template #prefix>
                    <MailOutlined />
                  </template>
                </el-input>
          </el-form-item>
          <el-form-item prop="code" >
            <el-col :span="12">
              <el-input style="height: 42px;margin-right: 80px;width: 220px" maxlength="6" v-model="form.code" placeholder="请输入验证码">
                  <template #prefix>
                    <SafetyCertificateOutlined />
                  </template>
              </el-input>
            </el-col>
            <el-col :span="12" style="text-align: right ;transform:translateY(4%);" >
              <el-button @click=askCode() style="width: 60%;height: 35px" color="#1cc2af"  plain
                       :disabled="!isEmailValid ||coldTime">{{coldTime > 0 ? '请稍后 '+coldTime+'秒' : '获取验证码'}}</el-button>
            </el-col>
          </el-form-item>
          <el-form-item style="margin-top: 60px">
            <el-button  @click="resetConfirm" style = "height:40px;width: 350px" color="#a7a7f1"  plain>开始重置密码</el-button>
          </el-form-item>
        </el-form>
        <el-form-item>
          <el-button @click="router.push({name:'welcome-login'})" style = "height:40px;width: 320px" color="#4ed5b1"  plain>返回登录</el-button>
        </el-form-item>
      </div>
    </div>
    <div v-if="active === 1" style="margin: 0 20px">
      <div >
        <div style="font-size:30px;font-weight:bold ;margin-top: 20px">重置密码</div>
        <div  style="font-size:16px; color: grey" >请填写你的新密码，务必牢记，防止丢失</div>
      </div>
      <div style="margin-top: 40px">
        <el-form :model="form" :rules="rules_B" ref="formRef">
          <el-form-item prop="password">
            <el-input minlength="6" maxlength="15"
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
          <el-form-item prop="password_repeat">
            <el-input
                style="height: 42px"
                v-model="form.password_repeat"
                autocomplete="on"
                type="password"
                placeholder="请再次输入密码">
              <template #prefix>
                <LockOutlined />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="margin-top: 60px">
            <el-button  @click="resetPassword" style = "height:40px;width: 350px" color="#1e1efa"  plain>立即重置密码</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-form-item>
        <el-button @click="router.push({name:'welcome-login'})" style = "height:40px;width: 320px" color="#4ed5b1"  plain>返回登录</el-button>
      </el-form-item>
    </div>
  </div>
</template>

<style scoped>

</style>