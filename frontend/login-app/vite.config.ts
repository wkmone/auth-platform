import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3001,
    proxy: {
      '/login': 'http://localhost:9000',
      '/oauth2': 'http://localhost:9000',
      '/.well-known': 'http://localhost:9000',
    },
  },
});
