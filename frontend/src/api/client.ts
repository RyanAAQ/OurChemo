import axios from 'axios';

const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

// Axios returns response.data automatically; we unwrap one level further
// so callers get back the inner `data` field of ApiResponse.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    // Normalise error message from ApiResponse or network error
    const message =
      error.response?.data?.data ||
      error.response?.data?.message ||
      error.message ||
      'Unknown error';
    return Promise.reject(new Error(message));
  }
);

export default client;
