import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LoginPage from './pages/LoginPage';
import ConsentPage from './pages/ConsentPage';
import LogoutPage from './pages/LogoutPage';
import ErrorPage from './pages/ErrorPage';

export default function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/consent" element={<ConsentPage />} />
          <Route path="/logout" element={<LogoutPage />} />
          <Route path="/error" element={<ErrorPage />} />
          <Route path="*" element={<LoginPage />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}
