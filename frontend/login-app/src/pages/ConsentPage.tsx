import { Card, Typography, Button, Space, Tag, Divider } from 'antd';
import { useSearchParams } from 'react-router-dom';

const { Title, Text } = Typography;

export default function ConsentPage() {
  const [searchParams] = useSearchParams();
  const clientId = searchParams.get('client_id') || 'Unknown App';
  const scopes = (searchParams.get('scope') || 'openid profile').split(' ');

  const scopeLabels: Record<string, string> = {
    openid: 'OpenID — 获取用户标识',
    profile: '个人信息 — 姓名、头像等',
    email: '邮箱地址',
    phone: '手机号码',
    roles: '角色信息',
  };

  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      minHeight: '100vh', background: '#f0f2f5'
    }}>
      <Card style={{ width: 480 }}>
        <Title level={4}>授权确认</Title>
        <Text>
          应用 <Text strong>{clientId}</Text> 请求访问您的以下信息：
        </Text>
        <Divider />
        <Space direction="vertical" style={{ width: '100%' }}>
          {scopes.map(scope => (
            <div key={scope}>
              <Tag color="blue">{scope}</Tag>
              <Text type="secondary">{scopeLabels[scope] || scope}</Text>
            </div>
          ))}
        </Space>
        <Divider />
        <Space style={{ width: '100%', justifyContent: 'flex-end' }}>
          <Button onClick={() => window.history.back()}>拒绝</Button>
          <Button type="primary" onClick={() => {
            // Submit consent - Spring Auth Server handles this via POST to /oauth2/authorize
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/oauth2/authorize';
            searchParams.forEach((v, k) => {
              const input = document.createElement('input');
              input.type = 'hidden';
              input.name = k;
              input.value = v;
              form.appendChild(input);
            });
            const consentInput = document.createElement('input');
            consentInput.type = 'hidden';
            consentInput.name = 'consent';
            consentInput.value = 'approve';
            form.appendChild(consentInput);
            document.body.appendChild(form);
            form.submit();
          }}>授权</Button>
        </Space>
      </Card>
    </div>
  );
}
