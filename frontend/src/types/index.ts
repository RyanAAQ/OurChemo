// ── Auth ────────────────────────────────────────────────────────────────────

export interface RegisterUserRequest {
  username: string;
  password: string;
  fullName: string;
}

export interface UserLoginRequest {
  username: string;
  password: string;
}

export interface UserLogoutRequest {
  username: string;
}

export interface RegisterUserResponse {
  username: string;
  fullName: string;
  message: string;
}

export interface UserLoginResponse {
  username: string;
  loggedIn: boolean;
  message: string;
}

export interface UserLogoutResponse {
  username: string;
  loggedIn: boolean;
  message: string;
}

// ── Drug / Batch ─────────────────────────────────────────────────────────────

export interface Batch {
  id: string;
  drugId: string;
  costPrice: number;
  purchaseQuantity: number;
  quantityLeft: number;
  purchaseDate: string; // ISO date string
}

export interface Drug {
  id: string;
  name: string;
  genericName: string;
  strength: string;
  dosageForm: string;
  brand: string;
  unitPrice: number;
  quantity: number;
  expiryDate: string; // ISO date string
  batches: Batch[];
}

// ── Drug DTOs ────────────────────────────────────────────────────────────────

export interface AddDrugRequest {
  name: string;
  genericName?: string;
  strength?: string;
  dosageForm?: string;
  brand: string;
  unitPrice: number;
  costPrice?: number;
  purchaseQuantity: number;
  expiryDate?: string; // ISO date string YYYY-MM-DD
}

export interface UpdateDrugRequest {
  name: string;
  brand?: string;
  price?: number;
}

export interface DeleteDrugRequest {
  name: string;
}

export interface AddDrugResponse {
  id: string;
  name: string;
  genericName: string;
  brand: string;
  unitPrice: number;
  totalQuantity: number;
  totalDrugs: number;
  drugBatch: Batch[];
  message: string;
}

export interface UpdateDrugResponse {
  id: string;
  name: string;
  brand: string;
  price: number;
  message: string;
}

export interface DeleteDrugResponse {
  id: string;
  message: string;
}

// ── Dispense ─────────────────────────────────────────────────────────────────

export interface DispensedDrugItem {
  batchId: string;
  drugId: string;
  drugName: string;
  quantity: number;
  totalPrice: number;
}

export interface DispenseDrugsRequest {
  username: string;
  items: DispensedDrugItem[];
}

export interface DispenseDrugsResponse {
  savedCount: number;
  totalAmount: number;
  message: string;
}

// ── History ──────────────────────────────────────────────────────────────────

export interface DispensedDrugs {
  id: string;
  items: DispensedDrugItem[];
  saleDateTime: string; // ISO date-time string
  dispensedBy: string;
}

// ── API Wrapper ──────────────────────────────────────────────────────────────

export interface ApiResponse<T = unknown> {
  successful: boolean;
  data: T;
}
