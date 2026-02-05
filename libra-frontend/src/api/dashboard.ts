import request from '@/utils/request'

// 仪表盘总览
export interface DashboardOverview {
  totalBookTypes: number
  totalInventory: number
  borrowingCount: number
  overdueCount: number
  todayBorrowCount: number
  todayReturnCount: number
}

// 借阅趋势数据
export interface BorrowTrendItem {
  date: string // 格式示例：2026-02-05
  borrowCount: number
}

// 热门图书
export interface HotBookItem {
  bookId: number | string
  title: string
  author: string
  borrowCount: number
}

// 逾期记录
export interface OverdueRecordItem {
  recordId: number | string
  userName: string
  bookTitle: string
  overdueDays: number
}

// 获取仪表盘核心指标
export function getDashboardOverview() {
  return request.get<DashboardOverview>('/dashboard/overview')
}

// 获取借阅趋势
export function getBorrowTrend(params: { days: number }) {
  return request.get<BorrowTrendItem[]>('/dashboard/borrow-trend', {
    params
  })
}

// 获取热门图书排行
export function getHotBooks(params: { limit: number }) {
  return request.get<HotBookItem[]>('/dashboard/hot-books', {
    params
  })
}

// 获取逾期提醒列表
export function getOverdueRecords(params: { limit: number }) {
  return request.get<OverdueRecordItem[]>('/dashboard/overdue-records', {
    params
  })
}
