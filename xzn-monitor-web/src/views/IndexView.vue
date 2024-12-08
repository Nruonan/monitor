<template>
  <el-container class="main-container">
    <el-header class="main-header">
      <el-image class="logo" src="https://vignette.wikia.nocookie.net/onepiece/images/8/89/Wiki-wordmark.png/revision/latest/scale-to-height-down/40"></el-image>
      <div class="tabs">
        <tab-item v-for="item in tabs" :name="item.name"
                  :active="item.id === tab" @click="changePage(item)"/>
        <el-switch
            size="default"
            v-model="isDark"
            style="--el-switch-on-color: #000000; --el-switch-off-color: #d5d5d5; margin: 0 20px"
            :active-action-icon="Moon"
            :inactive-action-icon="Sunny"
        />
        <el-dropdown>
          <el-avatar class="avatar" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/index/user-setting')">
                <el-icon><Operation /></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon><Back/></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view v-slot="{Component}">
          <transition name="el-fade-in-linear" mode="out-in">
            <keep-alive exclude="Security">
              <component :is="Component" />
            </keep-alive>
          </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { logout } from '@/net'
import router from "@/router";
import {Back, Moon, Operation, Sunny} from "@element-plus/icons-vue";
import {useDark} from "@vueuse/core";
import TabItem from "@/components/TabItem.vue";
import {ref} from "vue";
import {useRoute} from "vue-router";

const route = useRoute()

const isDark = useDark()
const tabs =[
  {id : 1, name: '管理', route: 'manage'},
  {id : 2, name: '安全', route: 'security'}
]
const defaultIndex = () =>{
  for(let tab of tabs){
    if (route.name === tab.route)
      return tab.id
  }
  return 1
}

const tab = ref(defaultIndex())


function changePage(item){
  tab.value = item.id
  router.push({name: item.route})
}
function userLogout() {
  logout(() => router.push("/"))
}
</script>

<style scoped>
.main-container{
  height: 100vh;
  width: 100vw;

  .main-header{
    height: 55px;
    background-color: var(--el-bg-color);
    border-bottom: solid 1px var(--el-border-color);
    display: flex;
    align-items: center;

    .tabs {
      height: 55px;
      gap: 10px;
      flex: 1px;
      display: flex;
      align-items: center;
      justify-content: right;
    }
  }
  .main-content{
    height: 100%;
    background-color: #f5f5f5;
  }
}
.dark .main-container .main-content{
  background-color: #232323;
}
</style>
