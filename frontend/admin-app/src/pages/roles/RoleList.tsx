import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Tree, Space, Tag, Typography, Card, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import axios from 'axios';

const { Title } = Typography;

interface Role {
  id: string;
  name: string;
  description: string;
  status: string;
  createdAt: string;
}

export default function RoleList() {
  const [data, setData] = useState<Role[]>([]);
  const [loading, setLoading] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);
  const [permOpen, setPermOpen] = useState(false);
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);
  const [createForm] = Form.useForm();
  const [checkedKeys, setCheckedKeys] = useState<string[]>([]);

  const fetchRoles = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/api/roles', { params: { page: 1, size: 100 } });
      setData(res.data.data?.records || []);
    } catch {
      message.error('加载角色列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchRoles(); }, []);

  const handleCreate = async (values: any) => {
    try {
      await axios.post('/api/roles', values);
      message.success('角色创建成功');
      setCreateOpen(false);
      createForm.resetFields();
      fetchRoles();
    } catch {
      message.error('创建角色失败');
    }
  };

  const handlePermSave = async () => {
    if (!selectedRole) return;
    try {
      await axios.put(`/api/roles/${selectedRole.id}/permissions`, { permissionIds: checkedKeys });
      message.success('权限分配成功');
      setPermOpen(false);
    } catch {
      message.error('权限分配失败');
    }
  };

  const columns: ColumnsType<Role> = [
    { title: '角色名称', dataIndex: 'name', key: 'name' },
    { title: '描述', dataIndex: 'description', key: 'description' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s: string) => <Tag color={s === 'active' ? 'green' : 'default'}>{s}</Tag>,
    },
    {
      title: '操作', key: 'actions',
      render: (_, record) => (
        <Button type="link" onClick={() => { setSelectedRole(record); setPermOpen(true); }}>
          分配权限
        </Button>
      ),
    },
  ];

  const permTree = [
    { title: '用户管理', key: 'user:read', children: [
      { title: '创建用户', key: 'user:create' },
      { title: '编辑用户', key: 'user:update' },
      { title: '删除用户', key: 'user:delete' },
    ]},
    { title: '角色管理', key: 'role:read', children: [
      { title: '创建角色', key: 'role:create' },
      { title: '编辑角色', key: 'role:update' },
    ]},
    { title: '应用管理', key: 'app:read', children: [
      { title: '创建应用', key: 'app:create' },
      { title: '审批应用', key: 'app:approve' },
    ]},
    { title: '审计日志', key: 'audit:read' },
  ];

  return (
    <div>
      <Title level={4}>角色管理</Title>
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateOpen(true)}>
            新建角色
          </Button>
        </Space>
        <Table columns={columns} dataSource={data} rowKey="id" loading={loading} />
      </Card>

      <Modal title="新建角色" open={createOpen} onCancel={() => setCreateOpen(false)}
        onOk={() => createForm.submit()} destroyOnClose>
        <Form form={createForm} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="name" label="角色名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title={`分配权限 — ${selectedRole?.name || ''}`} open={permOpen}
        onCancel={() => setPermOpen(false)} onOk={handlePermSave} destroyOnClose>
        <Tree checkable defaultExpandAll treeData={permTree}
          checkedKeys={checkedKeys} onCheck={(keys) => setCheckedKeys(keys as string[])} />
      </Modal>
    </div>
  );
}
