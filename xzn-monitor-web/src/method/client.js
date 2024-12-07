import {get, post} from "@/net";
import {ElMessage, ElMessageBox} from "element-plus";


export function rename(id,name,update){
  return ElMessageBox.prompt("请输入新的服务器主机名称","修改名称",{
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputValue: name,
    inputPattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]{1,10}$/,
    inputErrorMessage: '名称只能包含中英文字符、数字和下划线',
  }).then(({value})=>{
    post("/api/monitor/rename",{
      id: id,
      name: value
    },()=>{
      ElMessage.success("主机名称已更新")
      update()
    })
  })
}
export function getClientDetails(id,details){
  return get(`/api/monitor/details?clientId=${id}`,data=>{
    Object.assign(details.base,data)
  })
}

export function getClientHistory(id,details){
  return get(`/api/monitor/runtime-history?clientId=${id}`,data=>{
    Object.assign(details.runtime,data)
  })
}
