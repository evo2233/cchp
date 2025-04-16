export interface LoginCredentials {
  username: string;
  password: string;
}
export interface User {
  id: number;
  username: string;
  role: "admin" | "doctor" | "patient";
  name: string;
  email: string;
  phone: string;
  address: string;
  createdAt: string;
  updatedAt: string;
  avatar?: string;
}
