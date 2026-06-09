import { useState, useEffect } from 'react';
import { Table, Select, Tag, Space, Typography, Card, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import axios from 'axios';

const { Title } = Typography;

interface AuditEvent {
  id: string;
  eventType: string;
  principalName: string;
  targetType: string;
  targetId: string;
  result: string;
  createdAt: string;
}

export default function AuditList() {
  const [data, setData] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [filterType, setFilterType] = useState<string | undefined>();
  const [filterResult, setFilterResult] = useState<string | undefined>();

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/api/audit/events', {
        params: { page, size: 20, eventType: filterType, result: filterResult }
      });
      setData(res.data.data?.records || []);
      setTotal(res.data.data?.total || 0);
    } catch {
      message.error('加载审计日志失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchEvents(); }, [page]);

  const eventLabels: Record<string, string> = {
    LOGIN: '登录', LOGOUT: '登出', TOKEN_ISSUE: 'Token签发', TOKEN_REVOKE: 'Token吊销',
    USER_CREATE: '创建用户', ROLE_CHANGE: '角色变更', APP_APPROVE: '应用审批',
  };

  const columns: ColumnsType<AuditEvent> = [
    {
      title: '事件类型', dataIndex: 'eventType', key: 'eventType',
      render: (t: string) => <Tag>{eventLabels[t] || t}</Tag>,
    },
    { title: '操作人', dataIndex: 'principalName', key: 'principalName' },
    { title: '目标类型', dataIndex: 'targetType', key: 'targetType' },
    { title: '目标ID', dataIndex: 'targetId', key: 'targetId', ellipsis: true },
    {
      title: '结果', dataIndex: 'result', key: 'result',
      render: (r: string) => (
        <Tag color={r === 'SUCCESS' ? 'green' : 'red'}>
          {r === 'SUCCESS' ? '成功' : '失败'}
        </Tag>
      ),
    },
    {
      title: '时间', dataIndex: 'createdAt', key: 'createdAt',
      render: (t: string) => t?.substring(0, 19),
    },
  ];

  return (
    <div>
      <Title level={4}>审计日志</Title>
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Select placeholder="事件类型" allowClear style={{ width: 150 }}
            value={filterType} onChange={v => { setFilterType(v); setPage(1); }}
            options={Object.entries(eventLabels).map(([k, v]) => ({ value: k, label: v }))} />
          <Select placeholder="结果" allowClear style={{ width: 120 }}
            value={filterResult} onChange={v => { setFilterResult(v); setPage(1); }}
            options={[{ value: 'SUCCESS', label: '成功' }, { value: 'FAILURE', label: '失败' }]} />
        </Space>
        <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
          pagination={{
            current: page, total, pageSize: 20, showTotal: t => `共 ${t} 条`,
            onChange: (p) => { setPage(p); }
          }} />
      </Card>
    </div>
  );
}
