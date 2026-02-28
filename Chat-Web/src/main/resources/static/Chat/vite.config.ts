import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
 

  plugins: [
    vue(),
    vueDevTools(),
  ],


  server: {
    port:5173,
    proxy:{
      '/chat-io': {
        target: 'http://localhost:9090',
        ws: true,
        changeOrigin: true
      },
      '/api':{
        target: 'http://localhost:8080',
        changeOrigin:true,
        rewrite:(path)=>path.replace(/^\/api/,'')  //去掉/api
      },
      
    },
     allowedHosts: [
      'localhost',
      '127.0.0.1',
      '.cpolar.top' // 用通配符：允许所有 cpolar.top 的子域名
    ]
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
