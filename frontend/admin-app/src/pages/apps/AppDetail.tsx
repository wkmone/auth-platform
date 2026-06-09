import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Button, Tag, Space, Typography, message, Spin } from 'antd';
import axios from 'axios';

const { Title } = Typography;

interface AppDetail {
  id: string;
  appName: string;
  clientId: string;
  status: string;
  description: string;
  owner: string;
  redirectUris: string;
  scopes: string;
  grantTypes: string;
  accessTokenTtl: number;
  requireConsent: boolean;
  createdAt: string;
}

export default function AppDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [app, setApp] = useState<AppDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get(`/api/apps/${id}`).then(res => {
      setApp(res.data.data);
    }).catch(() => {
      message.error('加载应用详情失败');
    }).finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  if (!app) return <div>应用不存在</div>;

  const statusColor: Record<string, string> = {
    draft: 'default', active: 'green', suspended: 'orange', revoked: 'red',
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button onClick={() => navigate('/apps')}>返回列表</Button>
      </Space>
      <Title level={4}>{app.appName}</Title>
      <Card>
        <Descriptions bordered column={2}>
          <Descriptions.Item label="Client ID">{app.clientId}</Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color={statusColor[app.status]}>{app.status}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="负责人">{app.owner}</Descriptions.Item>
          <Descriptions.Item label="Access Token TTL">{app.accessTokenTtl}s</Descriptions.Item>
          <Descriptions.Item label="授权类型">{app.grantTypes}</Descriptions.Item>
          <Descriptions.Item label="需要授权确认">{app.requireConsent ? '是' : '否'}</Descriptions.Item>
          <Descriptions.Item label="回调地址" span={2}>{app.redirectUris}</Descriptions.Item>
          <Descriptions.Item label="Scope" span={2}>{app.scopes}</Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>{app.description || '无'}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{app.createdAt?.substring(0, 10)}</Descriptions.Item>
        </Descriptions>
      </Card>
    </div>
  );
}
