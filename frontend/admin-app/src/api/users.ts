import axios from 'axios';

export const userApi = {
  list: (params: any) => axios.get('/api/users', { params }),
  getById: (id: string) => axios.get(`/api/users/${id}`),
  create: (data: any) => axios.post('/api/users/register', data),
  update: (id: string, data: any) => axios.put(`/api/users/${id}`, data),
  delete: (id: string) => axios.delete(`/api/users/${id}`),
  changePassword: (id: string, data: any) => axios.put(`/api/users/${id}/password`, data),
  updateStatus: (id: string, status: string) => axios.put(`/api/users/${id}/status?status=${status}`),
  assignRoles: (id: string, roleIds: string[]) => axios.put(`/api/users/${id}/roles`, { roleIds }),
};
