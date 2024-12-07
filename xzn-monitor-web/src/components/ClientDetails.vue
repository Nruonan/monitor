<script setup>
import {reactive, watch} from "vue";
import {getClientDetails, rename} from "@/method/client";
import {copyIp} from "@/tool";

const props = defineProps({
  id: Number,
  update: Function
})
const details = reactive({
  base: {},
  runtime: {}
})
watch(() => props.id, (value) => {
  if (value !== -1) {
    details.base = {}
    getClientDetails(value,details)
  }
},{immediate: true})
</script>

<template>
  <div class="client-details" v-loading="Object.keys(details.base).length === 0">
    <div v-if="Object.keys(details.base).length">
      <div class="title">
        <i class="fa-solid fa-server"/>
        服务器信息
      </div>
      <el-divider style="margin: 10px 0"/>
      <div class="details-list">
        <div>
          <span>服务器ID</span>
          <span>{{details.base.id}}</span>
        </div>
        <div>
          <span>服务器名称</span>
          <span>{{details.base.name}}</span>&nbsp;
          <i @click.stop="rename(details.base.id, details.base.name, props.update)"
             class="fa-solid fa-pen-to-square interact-item"/>
        </div>
        <div>
          <span>运行状态</span>
          <span>
            <i style="color: #18cb18" class="fa-solid fa-circle-play" v-if="details.base.online"></i>
            <i style="color: #18cb18" class="fa-solid fa-circle-stop" v-else></i>
            {{details.base.online ? '运行中' : '离线'}}
          </span>
        </div>
        <div v-if="!details.editNode">
          <span>服务器节点</span>
          <span :class="`flag-icon flag-icon-${details.base.location}`"></span>&nbsp;
          <span>{{details.base.node}}</span>&nbsp;
          <i @click.stop="enableNodeEdit"
             class="fa-solid fa-pen-to-square interact-item"/>
        </div>
        <div>
          <span>公网IP地址</span>
          <span>
            {{details.base.ip}}
            <i class="fa-solid fa-copy interact-item" style="color: dodgerblue" @click.stop="copyIp(details.base.ip)"></i>
          </span>
        </div>
        <div style="display: flex">
          <span>处理器</span>
          <span>{{details.base.cpuName}}</span>
          <el-image style="height: 20px;margin-left: 10px"
                    />
        </div>
        <div>
          <span>硬件配置信息</span>
          <span>
            <i class="fa-solid fa-microchip"></i>
            <span style="margin-right: 10px">{{` ${details.base.cpuCore} CPU 核心数 /`}}</span>
            <i class="fa-solid fa-memory"></i>
            <span>{{` ${details.base.memory.toFixed(1)} GB 内存容量`}}</span>
          </span>
        </div>
        <div>
          <span>操作系统</span>
          <span>{{`${details.base.osName} ${details.base.osVersion}`}}</span>
        </div>
      </div>
      <div class="title" style="margin-top: 20px">
        <i class="fa-solid fa-gauge-high"></i>
        实时监控
      </div>
      <el-divider style="margin: 10px 0"/>
      <div style="display: flex">
        <el-progress type="dashboard" :width="100" :percentage="20" status="success">
          <div style="font-size: 17px;font-weight: bold;color: initial">CPU</div>
          <div style="font-size:13px;color: grey;margin-top: 5px">20%</div>
        </el-progress>
        <el-progress style="margin-left:20px"
                     type="dashboard" :width="100" :percentage="60" status="success">
          <div style="font-size: 16px;font-weight: bold;color: initial">内存</div>
          <div style="font-size: 13px;color: grey;margin-top: 5px">28.6 GB</div>
        </el-progress>
        <div style="flex: 1; margin-left: 30px; display: flex; flex-direction: column;height: 80px">
          <div style="flex:1;font-size:14px">
            <div>实时网络速度</div>
            <div style="margin: 5px 0">
              <i style="color: orange" class="fa-solid fa-arrow-up"></i>
              <span>{{` 20KB/s`}}</span>
              <el-divider direction="vertical"/>
              <i style="color: dodgerblue" class="fa-solid fa-arrow-down"></i>
              <span>{{` 20KB/s`}}</span>
            </div>
          </div>
          <div>
          <div style="font-size:13px;display: flex;justify-content: space-between">
            <div>
              <i class="fa-solid fa-hard-drive"></i>
              <span> 磁盘总容量</span>
            </div>
            <div> 6.6GB/40.0 GB</div>
          </div>
          <el-progress type="line" status="success" :percentage="24" :show-text="false"/></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.interact-item {
  transition: .3s;
  &:hover{
    cursor: pointer;
    scale: 1.1;
    opacity: 0.8;
  }
}
.client-details{
  height: 100%;
  padding: 20px;

  .title {
    color: #1e1efa;
    font-size: 15px;
    font-weight: bold;
  }

  .details-list{
    font-size: 14px;

    & div{
      margin-bottom: 10px;

      & span:first-child{
        color: grey;
        font-size: 13px;
        font-weight: normal;
        width: 120px;
        display: inline-block;
      }

      & span{
        font-weight: bold;
      }
    }
  }
}
</style>