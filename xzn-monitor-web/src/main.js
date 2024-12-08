import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios";
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as Icons from '@ant-design/icons-vue';
import 'flag-icon-css/css/flag-icons.min.css'
import '@/assets/element.less'
import {createPinia} from "pinia";
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
axios.defaults.baseURL = 'http://localhost:8081'

const app = createApp(App)
app.use(Antd)
app.use(ElementPlus)
app.use(router)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

const icons= Icons
app.mount('#app')
for(const  i in icons){
  app.component(i,icons[i])
}