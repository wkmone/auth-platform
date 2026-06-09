import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/es/locale/zh_CN';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import UserList from './pages/users/UserList';
import RoleList from './pages/roles/RoleList';
import AppList from './pages/apps/AppList';
import AppDetail from './pages/apps/AppDetail';
import AuditList from './pages/audit/AuditList';

export default function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="users" element={<UserList />} />
            <Route path="roles" element={<RoleList />} />
            <Route path="apps" element={<AppList />} />
            <Route path="apps/:id" element={<AppDetail />} />
            <Route path="audit" element={<AuditList />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}
