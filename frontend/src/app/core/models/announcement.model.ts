export interface Announcement {
  id: number;
  title: string;
  content: string;
  createdBy: number;
  createdAt: string;
  tags: string[];
}

export interface AnnouncementRequest {
  title: string;
  content: string;
  tagNames: string[];
}

export const AVAILABLE_TAGS = ['IMPORTANT', 'ACADEMIC', 'FINANCIAR', 'TERMEN_LIMITA'] as const;
