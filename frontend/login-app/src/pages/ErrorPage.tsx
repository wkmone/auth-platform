import { Card, Typography, Button } from 'antd';
import { useSearchParams } from 'react-router-dom';

const { Title, Text } = Typography;

const errorMessages: Record<string, string> = {
  invalid_request: '无效的请求参数',
  unauthorized_client: '未授权的客户端',
  access_denied: '用户拒绝了授权',
  server_error: '服务器内部错误',
  temporarily_unavailable: '服务暂时不可用',
};

export default function ErrorPage() {
  const [searchParams] = useSearchParams();
  const errorCode = searchParams.get('error') || 'server_error';
  const errorDesc = searchParams.get('error_description') || '';

  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      minHeight: '100vh', background: '#f0f2f5'
    }}>
      <Card style={{ width: 480, textAlign: 'center' }}>
        <div style={{ fontSize: 48, marginBottom: 16 }}>!</div>
        <Title level={4} type="danger">{errorMessages[errorCode] || errorCode}</Title>
        {errorDesc && <Text type="secondary">{errorDesc}</Text>}
        <div style={{ marginTop: 24 }}>
          <Button type="primary" onClick={() => window.location.href = '/login'}>
            返回登录
          </Button>
        </div>
      </Card>
    </div>
  );
}
