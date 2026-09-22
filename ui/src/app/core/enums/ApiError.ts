export interface ApiError {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  traceId: string;
  tenantId?: string;
  errorCode: string;
  timestamp: string;
  extras?: any;
}