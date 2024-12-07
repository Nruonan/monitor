<script setup>

</script>

<template>
  <div class="instance-card">
    <div style="display: flex; justify-content: space-between">
      <div>
        <div class="name">
          <span class="flag-icon flag-icon-cn"/>
          <span style="margin: 0 5px">轻青后端云服务器</span>
          <i class="fa-solid fa-pen-to-square"/>
        </div>
        <div class="os">
          操作系统：windows 11
        </div>
      </div>
      <div class="status">
        <i style="color: #18cb14" class="fa-solid fa-circle-play"></i>
        <span style="margin-left: 5px">运行中</span>
      </div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="network">
      <span style="margin-right: 10px">公网ip:192.123.123.123</span>
      <i style="color: cornflowerblue" class="fa-solid fa-copy"></i>
    </div>
    <div class="hardware">
      <i class="fa-solid fa-microchip"></i>
      <span style="margin-right: 10px"> 2 CPU</span>
      <i class="fa-solid fa-memory"></i>
      <span> 4 GB</span>
    </div>
    <div class="progress">
      <span>CPU: 25%</span>
     <el-progress :percentage="25" :stroke-width="6" :show-text="false" />
    </div>
    <div class="progress">
      <span>内存: 1.2GB</span>
      <el-progress :percentage="25" :stroke-width="6" :show-text="false"/>
    </div>
    <div class="network-flow">
      <div>网络流量</div>
      <div>
        <i class="fa-solid fa-arrow-up"></i>
        <span>1000 KB/s</span>
        <el-divider direction="vertical"/>
        <i class="fa-solid fa-arrow-down"></i>
        <span>52 KB/s</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-progress-bar__inner){
  background-color: #18cb18;
}
:deep(.el-progress-bar__outer){
  background-color: #18cb1823;
}
.dark .instance-card {
  color: #d9d9d9
}
.instance-card {
  width: 320px;
  padding: 15px;
  background-color: var(--el-bg-color);
  border-radius: 5px;
  box-sizing: border-box;

  .name {
    font-size: 15px;
    font-weight: bold;
  }

  .status {
    font-size: 14px;
  }
  .os {
    font-size: 13px;
    color: grey;
  }
  .network {
    font-size: 13px;
  }
  .hardware {
    margin-top: 5px;
    font-size: 13px;
  }
  .progress {
    margin-top: 10px;
    font-size: 12px;
  }

  .cpu {
    font-size: 13px;
  }

  .network-flow {
    margin-top: 10px;
    font-size: 12px;
    display: flex;
    justify-content: space-between;
  }
}
</style>