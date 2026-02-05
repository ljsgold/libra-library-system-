import request from '@/utils/request'

export interface AdminBorrowRecord {
  recordId: number
  userName: string
  bookTitle: string
  borrowTime: string
  dueTime: string
  returnTime?: string
  status: number
  overdueDays?: number
}

export function getBorrowRecords(params: { status?: number; limit?: number }) {
  return request.get<AdminBorrowRecord[]>('/admin/borrow/records', { params })
}
