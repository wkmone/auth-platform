import axios from 'axios';

export async function login(username: string, password: string, oauth2Params: URLSearchParams) {
  const formData = new URLSearchParams();
  formData.append('username', username);
  formData.append('password', password);
  oauth2Params.forEach((v, k) => formData.append(k, v));

  return axios.post('/login', formData, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
}

export async function logout() {
  return axios.post('/logout');
}
