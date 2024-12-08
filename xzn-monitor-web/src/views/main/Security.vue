<script setup>
import {computed, reactive, ref} from "vue";
import {get, logout, post} from "@/net";
import router from "@/router";
import {ElMessage} from "element-plus";
import CreateSubAccount from "@/components/CreateSubAccount.vue";
import {useStore} from "@/store";
const store = useStore()
const formRef = ref()
const valid = ref(false)
const onValidate =  (prop, isValid) => valid.value = isValid
const form = reactive({
  password: '',
  newPassword: '',
  confirmPassword: ''
})
const validatePassword = (rule, value, callback) =>{
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}
const emailFormRef = ref()
const emailForm = reactive({
  email: store.user.email,
  code:''
})
const coldTime = ref(0)
function  askCode(){
  if(isEmailValid){
    coldTime.value=60
    get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`,() =>{
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
function updateEmail(){
  if(emailForm.code.length ===0){
    ElMessage.warning('请先获取验证码！')
    return;
  }
  emailFormRef.value.validate((valid) =>{
    if(valid){
      post('/api/user/modify-email', emailForm, () => {
        ElMessage.success('邮件修改成功')
        store.user.email = emailForm.email
        emailForm.code = ''
      })
    }
  })
}
const isEmailValid = ref(true)
const rules = {
  password: [
      {required: true, message: '请输入当前密码', trigger: 'blur'},
      {min: 6, max: 16, message: '密码长度在6到16位之间', trigger: 'blur'}
  ],
  newPassword: [
      {required: true, message: '请输入新密码', trigger: 'blur'},
      {min: 6, max: 16, message: '密码长度在6到16位之间', trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, message: '请再次输入新密码', trigger: 'blur'},
    {validator: validatePassword, trigger: ['blur','change']}
  ],
  email:[
      {required: true, message: '请输入电子邮件地址', trigger: 'blur'},
      {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur','change']}
  ]
}
const onEmailValidate = (prop, isValid) => {
  if (prop === 'email')
    isEmailValid.value = isValid
}
function resetPassword() {
  formRef.value.validate(isValid =>{
    if(isValid){
      post('/api/user/change-password', form,() =>{
        ElMessage.success('密码已重置，请重新登录')
        logout(() => router.push('/'))
      })
    }
  })
}
const simpleList = ref([])
if (store.isAdmin){
  get('/api/monitor/simple-list', list => {
    simpleList.value = list
    initSubAccounts()
  })
}
const accounts = ref([])
const initSubAccounts = () =>
    get('/api/user/sub/list', list => accounts.value = list)

const createAccount = ref(false)

function deleteAccount(id) {
  get(`/api/user/sub/delete?uid=${id}`, () => {
    ElMessage.success('子账户删除成功')
    initSubAccounts()
  })
}
</script>

<template>
  <div style="display: flex; gap: 10px">
    <div style="flex: 50%">
      <div class="info-card">
        <div class="title"><i class="fa-solid fa-lock"> 修改密码</i></div>
        <el-divider style="margin: 10px 0"/>
        <el-form @validate="onValidate" :model="form" :rules="rules"
              ref="formRef" style="margin-top: 20px;" label-width="100">
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" v-model="form.password"
                      :prefix-icon="Lock" placeholder="请输入当前密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input type="password" v-model="form.newPassword"
                      :prefix-icon="Lock" placeholder="请输入新密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="form.confirmPassword"
                      :prefix-icon="Lock" placeholder="请再次输入新密码" maxlength="16"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button :icon="Switch" @click="resetPassword"
            type="success" :disabled="!valid">立即重置密码</el-button>
          </div>
        </el-form>
      </div>
      <div class="info-card" style="margin-top: 10px">
        <div class="title"><i class="fa-regular fa-envelope"> 电子邮件设置</i></div>
        <el-divider style="margin: 10px 0"/>
        <el-form :rules="rules" @validate="onEmailValidate" :model="emailForm" ref="emailFormRef"
                 label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件" label-position="top"  prop="email">
            <el-input  v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item label="验证码" label-position="top" prop="code">
            <el-row style="width: 100%" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码" v-model="emailForm.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" style="width: 100%" :disabled="!isEmailValid || coldTime > 0"
                           @click="askCode" plain>
                  {{ coldTime > 0 ? `请稍后 ${coldTime} 秒` : '获取验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" type="success" @click="updateEmail">更新电子邮件</el-button>
          </div>
        </el-form>
      </div>
    </div>
    <div class="info-card" style="flex: 50%">
      <div class="title"><i class="fa-solid fa-users"> 子用户管理</i></div>
      <el-divider style="margin: 10px 0"/>
      <div v-if="accounts.length" style="text-align: center">
        <div v-for="item in accounts" class="account-card">
          <el-avatar class="avatar" :size="30"
                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
          <div style="margin-left: 15px;line-height: 18px;flex: 1">
            <div>
              <span>{{item.username}}</span>
              <span style="font-size: 13px;color: grey;margin-left: 5px">
                管理 {{item.clientList.length}} 个服务器
              </span>
            </div>
            <div style="font-size: 13px;color: grey">{{item.email}}</div>
          </div>
          <el-button type="danger" :icon="Delete"
                     @click="deleteAccount(item.id)" text>删除子账户</el-button>
        </div>
        <el-button :icon="Plus" type="primary"
                   @click="createAccount = true" plain>添加更多子用户</el-button>
      </div>
      <div v-else>
        <el-empty :image-size="100" description="还没有任何子用户哦" v-if="store.isAdmin">
          <el-button :icon="Plus" type="primary" plain
                     @click="createAccount = true">添加子用户</el-button>
        </el-empty>
        <el-empty :image-size="100" description="子账户只能由管理员账号进行操作" v-else/>
      </div>
    </div>
    <el-drawer v-model="createAccount" size="350" :with-header="false">
      <create-sub-account :clients="simpleList" @create="createAccount = false;initSubAccounts()"/>
    </el-drawer>
  </div>
</template>

<style scoped>
.info-card {
  border-radius: 6px;
  width: 100%;
  padding: 15px 20px;
  background-color: var(--el-bg-color);
  height: fit-content;
  .title {
    font-size: 18px;
    font-weight: bold;
    color: #1e1efa;
  }
}
.account-card {
  border-radius: 5px;
  background-color: var(--el-bg-color-page);
  padding: 10px;
  display: flex;
  align-items: center;
  text-align: left;
  margin: 10px 0;
}
:deep(.el-drawer){
  margin: 10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}
:deep(.el-drawer__body){
  padding: 0;
}
</style>