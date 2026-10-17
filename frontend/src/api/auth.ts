import client from './client';
import type {
  RegisterUserRequest,
  RegisterUserResponse,
  UserLoginRequest,
  UserLoginResponse,
  UserLogoutRequest,
  UserLogoutResponse,
  ApiResponse,
} from '../types';

export async function register(data: RegisterUserRequest): Promise<RegisterUserResponse> {
  const res = await client.post<ApiResponse<RegisterUserResponse>>('/auth/register', data);
  return res.data.data;
}

export async function login(data: UserLoginRequest): Promise<UserLoginResponse> {
  const res = await client.post<ApiResponse<UserLoginResponse>>('/auth/login', data);
  return res.data.data;
}

export async function logout(data: UserLogoutRequest): Promise<UserLogoutResponse> {
  const res = await client.post<ApiResponse<UserLogoutResponse>>('/auth/logout', data);
  return res.data.data;
}
