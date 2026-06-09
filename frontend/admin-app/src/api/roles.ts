import axios from 'axios';

export const roleApi = {
  list: (params: any) => axios.get('/api/roles', { params }),
  create: (data: any) => axios.post('/api/roles', data),
  update: (id: string, data: any) => axios.put(`/api/roles/${id}`, data),
  delete: (id: string) => axios.delete(`/api/roles/${id}`),
  assignPermissions: (id: string, permissionIds: string[]) =>
    axios.put(`/api/roles/${id}/permissions`, { permissionIds }),
};
