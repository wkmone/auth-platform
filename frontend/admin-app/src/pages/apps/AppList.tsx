import { useState, useEffect } from 'react';
import { Table, Button, Tag, Space, Typography, Card, message } from 'antd';
import { PlusOutlined, EyeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import axios from 'axios';

const { Title } = Typography;

interface App {
  id: string;
  appName: string;
  clientId: string;
  status: string;
  owner: string;
  createdAt: string;
}

export default function AppList() {
  const [data, setData] = useState<App[]>([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const fetchApps = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/api/apps', { params: { page: 1, size: 100 } });
      setData(res.data.data?.records || []);
    } catch {
      message.error('加载应用列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchApps(); }, []);

  const statusColor: Record<string, string> = {
    draft: 'default', active: 'green', suspended: 'orange', revoked: 'red',
  };
  const statusLabel: Record<string, string> = {
    draft: '草稿', active: '已启用', suspended: '已挂起', revoked: '已吊销',
  };

  const columns: ColumnsType<App> = [
    { title: '应用名称', dataIndex: 'appName', key: 'appName' },
    { title: 'Client ID', dataIndex: 'clientId', key: 'clientId' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s: string) => <Tag color={statusColor[s]}>{statusLabel[s] || s}</Tag>,
    },
    { title: '负责人', dataIndex: 'owner', key: 'owner' },
    {
      title: '创建时间', dataIndex: 'createdAt', key: 'createdAt',
      render: (t: string) => t?.substring(0, 10),
    },
    {
      title: '操作', key: 'actions',
      render: (_, record) => (
        <Button type="link" icon={<EyeOutlined />} onClick={() => navigate(`/apps/${record.id}`)}>
          详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Title level={4}>应用管理</Title>
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/apps/new')}>
            注册应用
          </Button>
        </Space>
        <Table columns={columns} dataSource={data} rowKey="id" loading={loading} />
      </Card>
    </div>
  );
}
