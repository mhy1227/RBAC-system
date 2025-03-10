# 前端问题解决方案文档

## 1. MIME类型错误

### 问题描述
在启动前端项目时遇到以下错误：
```
Failed to load module script: Expected a JavaScript module script but the server responded with a MIME type of "video/mp2t". Strict MIME type checking is enforced for module scripts per HTML spec.
```

### 解决方案

#### 1. 更新依赖版本
修改 `package.json` 中的依赖版本：

```json
{
  "dependencies": {
    "vue": "^3.3.8",
    "vue-router": "^4.2.5",
    "element-plus": "^2.4.3",
    "pinia": "^2.1.7",
    "axios": "^1.6.2"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^4.5.0",
    "vite": "^5.0.0",
    "vue-tsc": "^1.8.22",
    "@types/node": "^20.10.3",
    "typescript": "^5.2.2",
    "sass": "^1.69.5"
  }
}
```

#### 2. 更新 Vite 配置
确保 `vite.config.ts` 配置正确：

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return 'vendor'
          }
        }
      }
    }
  }
})
```

#### 3. 清理和重新安装步骤

1. 停止当前运行的开发服务器
2. 执行清理命令：
```bash
# 删除依赖和缓存
rm -rf node_modules
rm package-lock.json
npm cache clean --force
```

3. 重新安装依赖：
```bash
npm install
```

4. 重启开发服务器：
```bash
npm run dev
```

### 注意事项
1. 确保 Node.js 版本兼容（推荐使用 v16 或更高版本）
2. 如果使用 yarn 或 pnpm，相应的命令需要调整
3. 如果还遇到问题，检查是否有网络代理影响

## 2. 其他常见问题
（待补充...） 