import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import * as path from "node:path";

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        createSvgIconsPlugin({
            iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
            symbolId: 'icon-[dir]-[name]',
        }),
        vue(),
        vueDevTools(),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:18080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            },
            '/ws': {
                target: 'http://localhost:18085',
                changeOrigin: true,
                ws: true
            }
        }
    }
})
