import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus, {ElMessage} from 'element-plus' //挂载全局组件
import './index.css'
import 'element-plus/dist/index.css' // 导入全局样式（确保组件样式正常）
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import {createPinia} from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import {userStore} from './store/UserStore'

import '@fortawesome/fontawesome-free/css/all.min.css'
//Element-plus全局组件svg
const app = createApp(App);

const pinia = createPinia();

pinia.use(piniaPluginPersistedstate);
 

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.config.globalProperties.$message = ElMessage;
app.use(ElementPlus)
app.use(router)
app.use(pinia)
app.mount('#app').$nextTick(()=>{
  const user = userStore();
  if(user.isLogin&&user.token){
    user.initApp();
  }
})
