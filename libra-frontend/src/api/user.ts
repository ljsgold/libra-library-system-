import request from '@/utils/request'

// 分类
export interface CategoryItem {
  id: number
  name: string
}

// 图书基础信息
export interface BookItem {
  id: string
  title: string
  author: string
  isbn: string
  categoryName: string
  coverUrl?: string
  canBorrow: boolean
  availableCount: number
  totalCount: number
}

export interface PagedResult<T> {
  records: T[]
  total: number
}

// 首页 - 新书 / 热门
export function getNewBooks() {
  return request.get<BookItem[]>('/user/home/new-books')
}

export function getPopularBooks() {
  return request.get<BookItem[]>('/user/home/popular-books')
}

export function getCategories() {
  return request.get<CategoryItem[]>('/user/categories')
}

// 图书检索 / 列表
export interface SearchBookParams {
  keyword?: string
  categoryId?: number
  onlyAvailable?: boolean
  sort?: 'latest' | 'popular' | 'available'
  page: number
  size: number
}

export function searchBooks(params: SearchBookParams) {
  return request.get<PagedResult<BookItem>>('/user/books', { params })
}

// 图书详情
export interface BookDetail extends BookItem {
  description?: string
  publisher?: string
  publishDate?: string
  locationInfo?: string
}

export function getBookDetail(id: number | string) {
  return request.get<BookDetail>(`/user/books/${id}`)
}

// 借阅 / 预约
export function borrowBook(bookId: number | string) {
  return request.post<void>('/user/borrow', { bookId })
}

export function reserveBook(bookId: number | string) {
  return request.post<void>('/user/reserve', { bookId })
}

// 我的借阅
export interface BorrowRecord {
  id: string
  bookTitle: string
  author: string
  borrowDate: string
  dueDate: string
  returnDate?: string
  status: 'BORROWING' | 'OVERDUE' | 'RETURNED'
  renewable: boolean
}

export function getCurrentBorrowList() {
  return request.get<BorrowRecord[]>('/user/my-borrow/current')
}

export function getHistoryBorrowList() {
  return request.get<BorrowRecord[]>('/user/my-borrow/history')
}

export function renewBorrow(id: number | string) {
  return request.post<void>('/user/my-borrow/renew', { id })
}

export function returnBorrow(id: number | string) {
  return request.post<void>('/user/my-borrow/return', { id })
}

// 预约记录
export interface ReservationRecord {
  id: string
  bookTitle: string
  author: string
  status: 'WAITING' | 'NOTIFIED' | 'CANCELLED'
  queueNo?: number
  expectedDate?: string
  pickupDeadline?: string
  notified?: boolean
  createdAt: string
}

export function getReservationList() {
  return request.get<ReservationRecord[]>('/user/reservations')
}

export function cancelReservation(id: number | string) {
  return request.post<void>('/user/reservations/cancel', { id })
}

export function subscribeArrival(id: number | string) {
  return request.post<void>('/user/reservations/subscribe', { id })
}

// 个人信息与借阅规则
export interface UserProfile {
  name: string
  studentNo?: string
  employeeNo?: string
  department?: string
  email?: string
  phone?: string
  isAdmin?: boolean
}

export interface BorrowRuleItem {
  title: string
  content: string
}

export function getUserProfile() {
  return request.get<UserProfile>('/user/profile')
}

export function getBorrowRules() {
  return request.get<BorrowRuleItem[]>('/user/borrow-rules')
}

