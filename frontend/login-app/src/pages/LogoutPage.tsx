import { Card, Typography, Button, Space } from 'antd';

const { Title, Text } = Typography;

export default function LogoutPage() {
  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      minHeight: '100vh', background: '#f0f2f5'
    }}>
      <Card style={{ width: 400, textAlign: 'center' }}>
        <Title level={4}>您已退出登录</Title>
        <Text type="secondary">您已成功退出统一身份认证平台</Text>
        <div style={{ marginTop: 24 }}>
          <Space>
            <Button type="primary" onClick={() => window.location.href = '/login'}>
              重新登录
            </Button>
          </Space>
        </div>
      </Card>
    </div>
  );
}
