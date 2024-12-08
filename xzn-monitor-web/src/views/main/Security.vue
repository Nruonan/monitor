<script setup>
import {reactive, ref} from "vue";
import {logout, post} from "@/net";
import router from "@/router";
import {ElMessage} from "element-plus";
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
  ]
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
</script>

<template>
  <div style="display: flex; gap: 10px">
    <div style="flex: 50%">
      <div class="info-card">
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
      </div>
    </div>
    <div class="info-card" style="flex: 50%">

    </div>
  </div>
</template>

<style scoped>
.info-card {
  border-radius: 6px;
  width: 100%;
  padding: 15px 20px;
  background-color: var(--el-bg-color);
}
</style>