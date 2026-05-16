export interface UserProfile {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  cnp?: string;
  dateOfBirth?: string;
  phone?: string;
}

export interface UserProfileRequest {
  firstName: string;
  lastName: string;
  cnp?: string;
  dateOfBirth?: string;
  phone?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}
