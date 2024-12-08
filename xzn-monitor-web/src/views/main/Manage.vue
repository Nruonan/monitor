<script setup>
import PreviewCard from "@/components/PreviewCard.vue";
import {reactive, ref} from "vue";

import {get} from "@/net";
import ClientDetails from "@/components/ClientDetails.vue";
import RegisterCard from "@/components/RegisterCard.vue";
import {refreshToken} from "@/method/client";
import {ElMessageBox} from "element-plus";

const list = ref([])
const updateList = () =>{
  get('/api/monitor/list',(data) =>{
    list.value = data
  })
}
setInterval(updateList,10000)
updateList()

const detail = reactive({
  show: false,
  id: -1,
})
const displayClientDetails = (id) => {
  detail.show = true
  detail.id = id
}
const register = reactive({
  show: false,
  token: ''
})
</script>

<template>
  <div class="manage-main">
    <div style="display: flex; justify-content: space-between; align-items: end">
      <div>
        <div class="title">
          <i class="fa-solid fa-server"></i>
          管理主机列表
        </div>
      </div>
      <div>
        <el-button :icon="Plus" type="primary" plain @click="register.show = true">添加新主机</el-button>
      </div>
    </div>

    <div class="desc">在这里管理所有已经注册的主机实例，实时监控主机运行状态，快速进行管理和操作。</div>
    <el-divider style="margin: 10px 0"/>
    <div class="card-list" v-if="list.length">
      <preview-card v-for="item in list" :data = "item" :update="updateList"
                    @click="displayClientDetails(item.id)"/>
    </div>
    <el-empty description="还没有任何主机哦" v-else/>
    <el-drawer size="520" :show-close="false" v-model="detail.show"
               :with-header="false" v-if="list.length" @close="detail.id = -1">
      <client-details :id="detail.id" :update="updateList" @delete="updateList"/>
    </el-drawer>
    <el-drawer v-model="register.show" direction="btt" :with-header="false"
      style="width: 600px; margin: 10px auto;"  size="300" @open="refreshToken(register)">
      <register-card :token="register.token"/>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer){
  margin: 10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}
:deep(.el-drawer__body){
  padding: 0;
}
.manage-main{
  margin: 0 50px;
  .title {
    font-size: 25px;
    font-weight: bold;
  }
  .desc {
    font-size: 15px;
    color: grey
  }
}
.card-list{
  display: flex;
  gap: 20px;
  flex-flow: wrap;
}
</style>