import client from './client';
import type {
  Drug,
  AddDrugRequest,
  AddDrugResponse,
  UpdateDrugRequest,
  UpdateDrugResponse,
  DeleteDrugRequest,
  DeleteDrugResponse,
  DispenseDrugsRequest,
  DispenseDrugsResponse,
  DispensedDrugs,
  ApiResponse,
} from '../types';

export async function getAllDrugs(): Promise<Drug[]> {
  const res = await client.get<ApiResponse<Drug[]>>('/drugs/all');
  return res.data.data;
}

export async function searchDrugs(word: string): Promise<Drug[]> {
  const res = await client.get<ApiResponse<{ drugs: Drug[] }>>('/drugs/search', {
    params: { word },
  });
  return res.data.data.drugs;
}

export async function addDrug(data: AddDrugRequest): Promise<AddDrugResponse> {
  const res = await client.post<ApiResponse<AddDrugResponse>>('/drugs/add', data);
  return res.data.data;
}

export async function updateDrug(data: UpdateDrugRequest): Promise<UpdateDrugResponse> {
  const res = await client.put<ApiResponse<UpdateDrugResponse>>('/drugs/update', data);
  return res.data.data;
}

export async function deleteDrug(data: DeleteDrugRequest): Promise<DeleteDrugResponse> {
  const res = await client.delete<ApiResponse<DeleteDrugResponse>>('/drugs/delete', {
    data,
  });
  return res.data.data;
}

export async function dispenseDrugs(data: DispenseDrugsRequest): Promise<DispenseDrugsResponse> {
  const res = await client.post<ApiResponse<DispenseDrugsResponse>>('/drugs/dispense', data);
  return res.data.data;
}

export async function getDispenseHistory(): Promise<DispensedDrugs[]> {
  const res = await client.get<ApiResponse<DispensedDrugs[]>>('/drugs/history');
  return res.data.data;
}
