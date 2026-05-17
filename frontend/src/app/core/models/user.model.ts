export type UserRole = 'STUDENT' | 'ADMIN' | 'SECRETARIAT';

export interface User {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  enabled: boolean;
  createdAt: string;
}

export interface UserRequest {
  username: string;
  email: string;
  password: string;
}

export const ALL_ROLES: UserRole[] = ['STUDENT', 'ADMIN', 'SECRETARIAT'];
