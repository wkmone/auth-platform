import axios from 'axios';

export const appApi = {
  list: (params: any) => axios.get('/api/apps', { params }),
  getById: (id: string) => axios.get(`/api/apps/${id}`),
  create: (data: any) => axios.post('/api/apps', data),
  update: (id: string, data: any) => axios.put(`/api/apps/${id}`, data),
  submit: (id: string) => axios.post(`/api/apps/${id}/submit`),
  approve: (id: string, reason?: string) => axios.post(`/api/apps/${id}/approve`, { reason }),
  reject: (id: string, reason?: string) => axios.post(`/api/apps/${id}/reject`, { reason }),
  updateStatus: (id: string, status: string) => axios.put(`/api/apps/${id}/status?status=${status}`),
  rotateSecret: (id: string) => axios.post(`/api/apps/${id}/secret/rotate`),
};
