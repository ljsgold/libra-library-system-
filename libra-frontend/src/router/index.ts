import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/auth/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/auth/Register.vue')
    },
    {
      path: '/forgot-password',
      name: 'ForgotPassword',
      component: () => import('../views/auth/ForgotPassword.vue')
    },
    {
      path: '/',
      redirect: '/u/home'
    },
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: 'books',
          name: 'AdminBooks',
          component: () => import('../views/BookList.vue')
        },
        {
          path: 'borrows',
          name: 'AdminBorrows',
          component: () => import('../views/admin/BorrowManage.vue')
        }
      ]
    },
    {
      path: '/dashboard',
      redirect: '/admin/dashboard'
    },
    {
      path: '/books',
      redirect: '/admin/books'
    },
    {
      path: '/u',
      component: () => import('../layouts/UserLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/u/home'
        },
        {
          path: 'home',
          name: 'UserHome',
          component: () => import('../views/user/Home.vue')
        },
        {
          path: 'books',
          name: 'UserBookList',
          component: () => import('../views/user/BookList.vue')
        },
        {
          path: 'books/:id',
          name: 'UserBookDetail',
          component: () => import('../views/user/BookDetail.vue')
        },
        {
          path: 'my-borrow',
          name: 'UserMyBorrow',
          component: () => import('../views/user/MyBorrow.vue')
        },
        {
          path: 'profile',
          name: 'UserProfile',
          component: () => import('../views/user/Profile.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('accessToken')
  const publicPaths = ['/login', '/register', '/forgot-password']
  const isPublic = publicPaths.includes(to.path)

  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (token && isPublic) {
    next('/u/home')
    return
  }

  next()
})

export default router
