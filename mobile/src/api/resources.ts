import { apiGet } from './client';

export type ResourceType = 'ROOM' | 'EQUIPMENT' | 'OTHER';

export type ResourceItem = {
  id: number;
  name: string;
  type: ResourceType;
  description: string | null;
  location: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
};

export function getResources(params?: {
  type?: ResourceType;
  includeInactive?: boolean;
}): Promise<ResourceItem[]> {
  return apiGet<ResourceItem[]>('/api/resources', params);
}

export function getResource(resourceId: number): Promise<ResourceItem> {
  return apiGet<ResourceItem>(`/api/resources/${resourceId}`);
}
