<template>
  <div class="home-page">
    <section class="hero">
      <div class="page-shell hero-content">
        <div class="hero-copy">
          <span class="eyebrow">Libra Library</span>
          <h1 class="hero-title">发现你的下一本好书</h1>
          <p class="hero-subtitle">探索海量馆藏，轻松借阅，让阅读成为一种优雅的体验。</p>
          <div class="hero-search">
            <div class="search-wrapper">
              <el-input
                v-model="keyword"
                placeholder="搜索书名、作者或 ISBN..."
                size="large"
                clearable
                class="hero-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon class="search-icon"><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" class="search-btn" @click="handleSearch">
                <span>搜索</span>
                <el-icon class="btn-icon"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-value">10,000+</span>
              <span class="stat-label">馆藏图书</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">5,000+</span>
              <span class="stat-label">活跃读者</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">98%</span>
              <span class="stat-label">满意度</span>
            </div>
          </div>
        </div>
        <div class="hero-visual" aria-hidden="true">
          <div class="hero-orb hero-orb--one"></div>
          <div class="hero-orb hero-orb--two"></div>
          <div class="hero-books">
            <div
              v-for="(book, index) in heroBooks"
              :key="book.id"
              class="hero-book"
              :class="`hero-book--${index + 1}`"
            >
              <el-image class="hero-cover" :src="resolveCoverUrl(book.coverUrl)" fit="cover" />
            </div>
          </div>
          <div class="hero-badge">
            <span class="hero-badge__label">今日推荐</span>
            <span class="hero-badge__value">{{ heroBadgeCount }} 本</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="page-shell">
        <div class="section-header">
          <div>
            <h2 class="section-title">新书上架</h2>
            <p class="section-subtitle">最新到馆的精选图书</p>
          </div>
          <el-button type="primary" plain @click="goBooks('new')" class="section-cta">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div v-if="loadingNew" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="!newBooks.length" class="empty-state">
          <div class="empty-state__title">暂无新书</div>
          <div class="empty-state__description">敬请期待更多精彩图书上架</div>
        </div>
        <div v-else class="book-grid">
          <div
            v-for="book in newBooks"
            :key="book.id"
            class="book-card"
            @click="goBookDetail(book.id)"
          >
            <el-image class="book-cover" :src="resolveCoverUrl(book.coverUrl)" fit="cover" />
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <div class="book-status" :class="{ available: book.canBorrow }">
                {{ book.canBorrow ? '可借阅' : '已借出' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section section--muted">
      <div class="page-shell">
        <div class="section-header">
          <div>
            <h2 class="section-title">热门借阅</h2>
            <p class="section-subtitle">最受读者欢迎的图书</p>
          </div>
        </div>

        <div v-if="loadingPopular" class="loading-state">
          <el-skeleton :rows="4" animated />
        </div>
        <div v-else-if="!popularBooks.length" class="empty-state">
          <div class="empty-state__title">暂无数据</div>
        </div>
        <div v-else class="popular-list">
          <div
            v-for="(book, index) in popularBooks"
            :key="book.id"
            class="popular-item"
            @click="goBookDetail(book.id)"
          >
            <div class="popular-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
            <div class="popular-info">
              <h4 class="popular-title">{{ book.title }}</h4>
              <p class="popular-author">{{ book.author }}</p>
            </div>
            <div class="popular-badge">{{ book.availableCount }} 本可借</div>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="page-shell">
        <div class="section-header">
          <div>
            <h2 class="section-title">分类浏览</h2>
            <p class="section-subtitle">按类别探索更多图书</p>
          </div>
        </div>

        <div v-if="loadingCategory" class="loading-state">
          <el-skeleton :rows="2" animated />
        </div>
        <div v-else-if="!categories.length" class="empty-state">
          <div class="empty-state__title">暂无分类</div>
        </div>
        <div v-else class="category-grid">
          <div
            v-for="c in categories"
            :key="c.id"
            class="category-card"
            @click="goBooksByCategory(c.id)"
          >
            <div class="category-image-wrapper">
              <img 
                :src="getCategoryImage(c.name)" 
                :alt="c.name"
                class="category-image"
                @error="handleImageError"
              />
              <div class="category-overlay"></div>
            </div>
            <div class="category-info">
              <h3 class="category-name">{{ c.name }}</h3>
              <p class="category-desc">{{ getCategoryDesc(c.name) }}</p>
            </div>
            <el-icon class="category-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Search, ArrowRight } from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { BookItem, CategoryItem } from '@/api/user'
import { getCategories, getNewBooks, getPopularBooks } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const keyword = ref('')
const newBooks = ref<BookItem[]>([])
const popularBooks = ref<BookItem[]>([])
const categories = ref<CategoryItem[]>([])

const loadingNew = ref(false)
const loadingPopular = ref(false)
const loadingCategory = ref(false)

const fallbackBooks: BookItem[] = [
  {
    id: 'hero-1',
    title: '精选好书',
    author: 'LIBRA',
    isbn: '',
    categoryName: '',
    coverUrl: undefined,
    canBorrow: false,
    availableCount: 0,
    totalCount: 0
  },
  {
    id: 'hero-2',
    title: '精选好书',
    author: 'LIBRA',
    isbn: '',
    categoryName: '',
    coverUrl: undefined,
    canBorrow: false,
    availableCount: 0,
    totalCount: 0
  },
  {
    id: 'hero-3',
    title: '精选好书',
    author: 'LIBRA',
    isbn: '',
    categoryName: '',
    coverUrl: undefined,
    canBorrow: false,
    availableCount: 0,
    totalCount: 0
  }
]

const heroBooks = computed(() => (newBooks.value.length ? newBooks.value.slice(0, 3) : fallbackBooks))
const heroBadgeCount = computed(() => (newBooks.value.length ? newBooks.value.length : 12))

const apiBase = ((import.meta.env.VITE_APP_BASE_API as string) || '/api').replace(/\/$/, '')
const defaultCover = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
  `<svg xmlns="http://www.w3.org/2000/svg" width="120" height="160" viewBox="0 0 120 160">
    <rect width="120" height="160" fill="#f2f3f5"/>
    <rect x="14" y="18" width="92" height="124" rx="8" fill="#ffffff" stroke="#dcdfe6"/>
    <text x="60" y="86" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="18" fill="#909399">BOOK</text>
  </svg>`
)}`

const resolveCoverUrl = (raw?: string) => {
  if (!raw) return defaultCover
  if (raw.startsWith('data:') || raw.startsWith('blob:')) return raw

  if (/^https?:\/\//i.test(raw)) {
    try {
      const u = new URL(raw)
      if (u.hostname.endsWith('doubanio.com')) {
        return `${apiBase}/public/image-proxy?url=${encodeURIComponent(raw)}`
      }
    } catch {
      return defaultCover
    }
    return raw
  }

  const path = raw.startsWith('/') ? raw : `/${raw}`
  return `${apiBase}${path}`
}

const fetchNewBooks = async () => {
  loadingNew.value = true
  try {
    newBooks.value = await getNewBooks()
  } catch (e) {
    ElMessage.error('获取新书失败')
  } finally {
    loadingNew.value = false
  }
}

const fetchPopularBooks = async () => {
  loadingPopular.value = true
  try {
    popularBooks.value = await getPopularBooks()
  } catch (e) {
    ElMessage.error('获取热门图书失败')
  } finally {
    loadingPopular.value = false
  }
}

const fetchCategories = async () => {
  loadingCategory.value = true
  try {
    categories.value = await getCategories()
  } catch (e) {
    ElMessage.error('获取分类失败')
  } finally {
    loadingCategory.value = false
  }
}

const handleSearch = () => {
  router.push({
    path: '/u/books',
    query: { keyword: keyword.value || undefined }
  })
}

const goBooks = (tag?: string) => {
  router.push({
    path: '/u/books',
    query: { tag }
  })
}

const goBooksByCategory = (categoryId: number) => {
  router.push({
    path: '/u/books',
    query: { categoryId }
  })
}

const goBookDetail = (id: string | number) => {
  router.push(`/u/books/${id}`)
}

// 分类图片和描述配置（基于中图法22大类，使用Unsplash专业图书馆风格图片）
const categoryConfig: Record<string, { image: string; desc: string }> = {
  '马克思主义、列宁主义、毛泽东思想、邓小平理论': {
    image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop&q=80',
    desc: '马克思主义经典著作、毛泽东思想、邓小平理论等理论文献'
  },
  '哲学、宗教': {
    image: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=300&fit=crop&q=80',
    desc: '哲学理论、世界哲学、中国哲学、宗教文化等思想经典'
  },
  '社会科学总论': {
    image: 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?w=400&h=300&fit=crop&q=80',
    desc: '社会学、人口学、管理学、统计学等社会科学理论'
  },
  '政治、法律': {
    image: 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=400&h=300&fit=crop&q=80',
    desc: '政治理论、中国共产党、各国政治、法律法规等'
  },
  '军事': {
    image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop&q=80',
    desc: '军事理论、战略战术、军事历史、武器装备等'
  },
  '经济': {
    image: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=300&fit=crop&q=80',
    desc: '经济学理论、金融投资、贸易经济、财政金融等'
  },
  '文化、科学、教育、体育': {
    image: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400&h=300&fit=crop&q=80',
    desc: '文化理论、信息传播、科学教育、体育运动等'
  },
  '语言、文字': {
    image: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=300&fit=crop&q=80',
    desc: '语言学、汉语、外语学习、语言文字研究等'
  },
  '文学': {
    image: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=300&fit=crop&q=80',
    desc: '文学理论、世界文学、中国文学、各国文学作品'
  },
  '艺术': {
    image: 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=300&fit=crop&q=80',
    desc: '绘画、书法、雕塑、摄影、音乐、舞蹈、戏剧、电影等'
  },
  '历史、地理': {
    image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop&q=80',
    desc: '史学理论、世界史、中国史、人物传记、地理等'
  },
  '自然科学总论': {
    image: 'https://images.unsplash.com/photo-1532619675605-1ede6c9ed2d7?w=400&h=300&fit=crop&q=80',
    desc: '自然科学理论与方法论、自然科学现状与发展等'
  },
  '数理科学和化学': {
    image: 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=300&fit=crop&q=80',
    desc: '数学、力学、物理学、化学等基础科学'
  },
  '天文学、地球科学': {
    image: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=400&h=300&fit=crop&q=80',
    desc: '天文学、测绘学、地球物理学、气象学、地质学等'
  },
  '生物科学': {
    image: 'https://images.unsplash.com/photo-1532619675605-1ede6c9ed2d7?w=400&h=300&fit=crop&q=80',
    desc: '普通生物学、细胞学、遗传学、生理学、生物化学等'
  },
  '医药、卫生': {
    image: 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=400&h=300&fit=crop&q=80',
    desc: '预防医学、卫生学、中国医学、基础医学、临床医学等'
  },
  '农业科学': {
    image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=400&h=300&fit=crop&q=80',
    desc: '农业基础科学、农业工程、农学、林业、畜牧、水产等'
  },
  '工业技术': {
    image: 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=400&h=300&fit=crop&q=80',
    desc: '一般工业技术、矿业工程、机械、电子、计算机、自动化等'
  },
  '交通运输': {
    image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop&q=80',
    desc: '综合运输、铁路运输、公路运输、水路运输、航空运输等'
  },
  '航空、航天': {
    image: 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=400&h=300&fit=crop&q=80',
    desc: '航空技术、航天技术、航天器、航天工程等'
  },
  '环境科学、安全科学': {
    image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=400&h=300&fit=crop&q=80',
    desc: '环境科学基础理论、环境工程、安全科学等'
  },
  '综合性图书': {
    image: 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?w=400&h=300&fit=crop&q=80',
    desc: '丛书、百科全书、辞典、年鉴、期刊、连续性出版物等'
  }
}

// 默认分类配置（用于未匹配的分类）
const defaultCategoryConfig = {
  image: '/img/categories/default.jpg',
  desc: '探索更多精彩图书'
}

// 分类名称映射（支持部分匹配，基于中图法）
const categoryNameMap: Record<string, string> = {
  '马克思主义': '马克思主义、列宁主义、毛泽东思想、邓小平理论',
  '哲学': '哲学、宗教',
  '宗教': '哲学、宗教',
  '社会科学': '社会科学总论',
  '政治': '政治、法律',
  '法律': '政治、法律',
  '军事': '军事',
  '经济': '经济',
  '文化': '文化、科学、教育、体育',
  '科学': '文化、科学、教育、体育',
  '教育': '文化、科学、教育、体育',
  '体育': '文化、科学、教育、体育',
  '语言': '语言、文字',
  '文字': '语言、文字',
  '文学': '文学',
  '艺术': '艺术',
  '历史': '历史、地理',
  '地理': '历史、地理',
  '自然科学': '自然科学总论',
  '数学': '数理科学和化学',
  '物理': '数理科学和化学',
  '化学': '数理科学和化学',
  '天文': '天文学、地球科学',
  '地球': '天文学、地球科学',
  '生物': '生物科学',
  '医药': '医药、卫生',
  '卫生': '医药、卫生',
  '医学': '医药、卫生',
  '农业': '农业科学',
  '工业': '工业技术',
  '技术': '工业技术',
  '交通': '交通运输',
  '运输': '交通运输',
  '航空': '航空、航天',
  '航天': '航空、航天',
  '环境': '环境科学、安全科学',
  '安全': '环境科学、安全科学',
  '综合': '综合性图书'
}

const getCategoryImage = (name: string): string => {
  // 精确匹配
  if (categoryConfig[name]) {
    return categoryConfig[name].image
  }
  // 部分匹配
  for (const [key, mappedName] of Object.entries(categoryNameMap)) {
    if (name.includes(key)) {
      return categoryConfig[mappedName]?.image || defaultCategoryConfig.image
    }
  }
  return defaultCategoryConfig.image
}

const getCategoryDesc = (name: string): string => {
  // 精确匹配
  if (categoryConfig[name]) {
    return categoryConfig[name].desc
  }
  // 部分匹配
  for (const [key, mappedName] of Object.entries(categoryNameMap)) {
    if (name.includes(key)) {
      return categoryConfig[mappedName]?.desc || defaultCategoryConfig.desc
    }
  }
  return defaultCategoryConfig.desc
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  const categoryName = img.alt || '分类'
  // 使用默认占位图
  img.src = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300">
      <rect width="400" height="300" fill="#f5f7fa"/>
      <rect x="50" y="50" width="300" height="200" rx="8" fill="#ffffff" stroke="#e4e7ed"/>
      <text x="200" y="140" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="24" fill="#909399">${categoryName}</text>
      <text x="200" y="170" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="14" fill="#c0c4cc">分类图片</text>
    </svg>`
  )}`
}

onMounted(() => {
  fetchNewBooks()
  fetchPopularBooks()
  fetchCategories()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
}
.hero {
  padding: 120px 0 80px;
  text-align: left;
  background: linear-gradient(to bottom, var(--color-background-secondary) 0%, var(--color-background) 100%);
}

.hero-content {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  gap: 32px;
  align-items: center;
}

.hero-copy {
  display: grid;
  gap: 20px;
}

.eyebrow {
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-primary);
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.hero-title {
  font-size: clamp(48px, 6vw, 64px);
  font-weight: 700;
  letter-spacing: -0.003em;
  line-height: 1.05;
  margin: 0;
  color: var(--color-text);
  background: linear-gradient(135deg, var(--color-text) 0%, rgba(29, 29, 31, 0.7) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 21px;
  line-height: 1.5;
  color: var(--color-text-secondary);
  margin: 0;
  max-width: 640px;
  font-weight: 400;
  letter-spacing: -0.022em;
}

.hero-search {
  max-width: 640px;
}

.hero-search :deep(.el-input__wrapper) {
  padding: 10px 128px 10px 46px;
  border-radius: 14px;
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  height: 54px;
}

.hero-search :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(var(--color-primary-rgb), 0.35);
  box-shadow: 0 0 0 4px rgba(var(--color-primary-rgb), 0.12);
}

.search-icon {
  font-size: 20px;
  color: var(--color-text-secondary);
  margin-left: 4px;
}

.hero-search :deep(.el-input__inner) {
  font-size: 16px;
  height: 100%;
  color: var(--color-text);
  font-weight: 500;
}

.hero-search :deep(.el-input__inner::placeholder) {
  color: var(--color-muted);
}

.search-wrapper {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.search-btn {
  position: absolute;
  right: 8px;
  top: 7px;
  bottom: 7px;
  border-radius: 12px !important;
  padding: 0 18px !important;
  font-weight: 700;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 6px;
  height: auto !important;
}

.search-btn:hover {
  transform: none;
}

.btn-icon {
  font-size: 18px;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  max-width: 720px;
}

.hero-visual {
  position: relative;
  min-height: 480px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, 
    rgba(var(--color-primary-rgb), 0.08) 0%, 
    rgba(var(--color-primary-rgb), 0.03) 50%,
    rgba(0, 0, 0, 0.02) 100%);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  display: grid;
  place-items: center;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(0);
  opacity: 0.55;
}

.hero-orb--one {
  width: 180px;
  height: 180px;
  background: rgba(var(--color-primary-rgb), 0.18);
  top: -40px;
  left: -30px;
}

.hero-orb--two {
  width: 140px;
  height: 140px;
  background: rgba(15, 23, 42, 0.08);
  bottom: -30px;
  right: 20px;
}

.hero-books {
  position: absolute;
  inset: 0;
}

.hero-book {
  position: absolute;
  width: 190px;
  aspect-ratio: 3 / 4;
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: var(--shadow-lg);
  background: var(--color-surface);
}

.hero-book--1 {
  transform: translate(-90px, -20px) rotate(-9deg);
  top: 20%;
  left: 28%;
}

.hero-book--2 {
  width: 210px;
  transform: translate(0, 0) rotate(2deg);
  top: 18%;
  left: 45%;
  z-index: 2;
}

.hero-book--3 {
  transform: translate(90px, 30px) rotate(10deg);
  top: 24%;
  left: 55%;
}

.hero-cover {
  width: 100%;
  height: 100%;
  display: block;
}

.hero-cover :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.hero-badge {
  position: absolute;
  right: 20px;
  bottom: 20px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  display: grid;
  gap: 4px;
}

.hero-badge__label {
  font-size: 12px;
  color: var(--color-text-secondary);
  font-weight: 600;
}

.hero-badge__value {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
}

.stat-item {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  padding: 24px 20px;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  cursor: default;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: rgba(var(--color-primary-rgb), 0.15);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.02em;
  display: block;
  margin-bottom: 6px;
  color: var(--color-text);
}

.stat-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-secondary);
  letter-spacing: 0.02em;
}
.section {
  padding: 80px 0;
}

.section--muted {
  background: var(--color-background-secondary);
  border-top: 1px solid var(--color-border-light);
  border-bottom: 1px solid var(--color-border-light);
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}

.section-title {
  font-size: 40px;
  font-weight: 700;
  letter-spacing: -0.003em;
  margin: 0 0 12px;
  color: var(--color-text);
}

.section-subtitle {
  font-size: 19px;
  color: var(--color-text-secondary);
  margin: 0;
  font-weight: 400;
  letter-spacing: -0.022em;
}

.section-cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.book-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.book-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: var(--shadow-lg);
  border-color: rgba(var(--color-primary-rgb), 0.2);
}

.book-cover {
  width: 100%;
  aspect-ratio: 3/4;
  background-color: var(--color-background-secondary);
}

.book-info {
  padding: 16px;
}

.book-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 4px;
  color: var(--color-text);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-author {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 12px;
}

.book-status {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 6px;
  background: var(--color-background-secondary);
  color: var(--color-text-secondary);
}

.book-status.available {
  background: rgba(52, 199, 89, 0.12);
  color: #248A3D;
}

.popular-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.popular-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: var(--color-surface);
  border-radius: 14px;
  cursor: pointer;
  transition: all 200ms ease;
  border: 1px solid var(--color-border-light);
}

.popular-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.popular-rank {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: var(--color-background-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.popular-rank.rank-1 {
  background: rgba(var(--color-primary-rgb), 0.12);
  color: var(--color-primary);
}

.popular-rank.rank-2 {
  background: rgba(15, 23, 42, 0.06);
}

.popular-rank.rank-3 {
  background: rgba(15, 23, 42, 0.06);
}

.popular-info {
  flex: 1;
  min-width: 0;
}

.popular-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 2px;
  color: var(--color-text);
}

.popular-author {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.popular-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(var(--color-primary-rgb), 0.1);
  color: var(--color-primary);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.category-card {
  position: relative;
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  min-height: 260px;
}

.category-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: var(--shadow-xl);
  border-color: rgba(var(--color-primary-rgb), 0.25);
}

.category-image-wrapper {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(var(--color-primary-rgb), 0.1), rgba(15, 23, 42, 0.05));
}

.category-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 400ms ease;
}

.category-card:hover .category-image {
  transform: scale(1.08);
}

.category-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
  opacity: 0;
  transition: opacity 300ms ease;
}

.category-card:hover .category-overlay {
  opacity: 1;
}

.category-info {
  flex: 1;
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
}

.category-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
  letter-spacing: -0.01em;
  line-height: 1.3;
}

.category-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-arrow {
  position: absolute;
  bottom: 18px;
  right: 20px;
  font-size: 18px;
  color: var(--color-text-secondary);
  transition: all 300ms ease;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
}

.category-card:hover .category-arrow {
  transform: translateX(4px) scale(1.1);
  color: var(--color-primary);
  background: rgba(var(--color-primary-rgb), 0.1);
  box-shadow: var(--shadow-md);
}
.loading-state {
  padding: 40px 0;
}

.empty-state {
  text-align: center;
  padding: 64px 24px;
}

.empty-state__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.empty-state__description {
  font-size: 15px;
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .hero {
    padding: 56px 0 40px;
  }

  .hero-content {
    grid-template-columns: 1fr;
  }

  .hero-visual {
    min-height: 320px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-stats {
    gap: 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .section {
    padding: 40px 0;
  }

  .section-title {
    font-size: 24px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .book-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .category-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .category-card {
    min-height: 200px;
  }

  .category-image-wrapper {
    height: 120px;
  }

  .category-name {
    font-size: 16px;
  }

  .category-desc {
    font-size: 12px;
  }
}
</style>
