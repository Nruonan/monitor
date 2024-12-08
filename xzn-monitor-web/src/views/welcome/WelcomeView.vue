<template>
  <div class="login-container">
    <div class="box-item desc">
      <div class="welcome">
        <p>欢迎登录  青轻监控系统</p>
        <p class="sub-welcome">用于监控服务器的实时数据</p>
        <p style="font-size: 13px;font-weight: normal">默认账号：admin  默认密码：123456</p>
      </div>
    </div>
    <div class="box-item login">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" style="height: 100%"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>
<script setup>

</script>
<style lang="less" scoped>

@import 'login.less';


</style>