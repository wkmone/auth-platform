import axios from 'axios';

export const auditApi = {
  list: (params: any) => axios.get('/api/audit/events', { params }),
  getById: (id: string) => axios.get(`/api/audit/events/${id}`),
};
