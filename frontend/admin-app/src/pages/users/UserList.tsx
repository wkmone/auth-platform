import { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Tag, Modal, Form, Typography, message, Card } from 'antd';
import { PlusOutlined, EditOutlined, LockOutlined, SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import axios from 'axios';

const { Title } = Typography;

interface User {
  id: string;
  username: string;
  displayName: string;
  email: string;
  phone: string;
  status: string;
  createdAt: string;
  roles: string[];
}

export default function UserList() {
  const [data, setData] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [searchUsername, setSearchUsername] = useState('');
  const [searchStatus, setSearchStatus] = useState<string | undefined>();
  const [createOpen, setCreateOpen] = useState(false);
  const [createForm] = Form.useForm();

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/api/users', {
        params: { page, size: 20, username: searchUsername || undefined, status: searchStatus }
      });
      setData(res.data.data?.records || []);
      setTotal(res.data.data?.total || 0);
    } catch {
      message.error('加载用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, [page]);

  const handleCreate = async (values: any) => {
    try {
      await axios.post('/api/users/register', values);
      message.success('用户创建成功');
      setCreateOpen(false);
      createForm.resetFields();
      fetchUsers();
    } catch {
      message.error('创建用户失败');
    }
  };

  const columns: ColumnsType<User> = [
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '显示名', dataIndex: 'displayName', key: 'displayName' },
    { title: '邮箱', dataIndex: 'email', key: 'email' },
    { title: '手机', dataIndex: 'phone', key: 'phone' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s: string) => (
        <Tag color={s === 'active' ? 'green' : s === 'locked' ? 'red' : 'default'}>
          {s === 'active' ? '正常' : s === 'locked' ? '已锁定' : s}
        </Tag>
      ),
    },
    {
      title: '角色', dataIndex: 'roles', key: 'roles',
      render: (roles: string[]) => roles?.map(r => <Tag key={r}>{r}</Tag>),
    },
    {
      title: '创建时间', dataIndex: 'createdAt', key: 'createdAt',
      render: (t: string) => t?.substring(0, 10),
    },
    {
      title: '操作', key: 'actions',
      render: (_, record) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} size="small">编辑</Button>
          <Button type="link" icon={<LockOutlined />} size="small">重置密码</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Title level={4}>用户管理</Title>
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Input placeholder="搜索用户名" prefix={<SearchOutlined />}
            value={searchUsername} onChange={e => setSearchUsername(e.target.value)}
            onPressEnter={fetchUsers} style={{ width: 200 }} />
          <Select placeholder="状态" allowClear style={{ width: 120 }}
            value={searchStatus} onChange={v => setSearchStatus(v)}
            options={[
              { value: 'active', label: '正常' },
              { value: 'disabled', label: '禁用' },
              { value: 'locked', label: '锁定' },
            ]} />
          <Button type="primary" onClick={fetchUsers}>查询</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateOpen(true)}>
            新建用户
          </Button>
        </Space>
        <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
          pagination={{
            current: page, total, pageSize: 20, showTotal: t => `共 ${t} 条`,
            onChange: (p) => { setPage(p); }
          }} />
      </Card>

      <Modal title="新建用户" open={createOpen} onCancel={() => setCreateOpen(false)}
        onOk={() => createForm.submit()} destroyOnClose>
        <Form form={createForm} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, min: 10 }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="displayName" label="显示名">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱" rules={[{ type: 'email' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
