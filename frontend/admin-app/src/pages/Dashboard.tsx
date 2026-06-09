import { useState } from 'react';
import { Row, Col, Card, Statistic, Typography } from 'antd';
import { UserOutlined, AppstoreOutlined, LoginOutlined, KeyOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface DashboardStats {
  totalUsers: number;
  totalApps: number;
  todayLogins: number;
  activeTokens: number;
}

export default function Dashboard() {
  const [stats] = useState<DashboardStats>({
    totalUsers: 0, totalApps: 0, todayLogins: 0, activeTokens: 0
  });

  return (
    <div>
      <Title level={4}>仪表盘</Title>
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="总用户数" value={stats.totalUsers} prefix={<UserOutlined />} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="接入应用" value={stats.totalApps} prefix={<AppstoreOutlined />} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="今日登录" value={stats.todayLogins} prefix={<LoginOutlined />} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="活跃 Token" value={stats.activeTokens} prefix={<KeyOutlined />} /></Card>
        </Col>
      </Row>
    </div>
  );
}
