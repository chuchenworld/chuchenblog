import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('@/views/Articles.vue')
  },
  {
    path: '/categories/:categoryId',
    name: 'CategoryPosts',
    component: () => import('@/views/CategoryPosts.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/life',
    name: 'Life',
    component: () => import('@/views/Life.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('@/views/Messages.vue')
  },
  {
    path: '/links',
    name: 'Links',
    component: () => import('@/views/Links.vue')
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/PostDetail.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/editor',
    name: 'Editor',
    component: () => import('@/views/Editor.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/tags/:tagName',
    name: 'TagPosts',
    component: () => import('@/views/TagPosts.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/album/:id',
    name: 'AlbumDetail',
    component: () => import('@/views/AlbumDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/photo/:id',
    name: 'PhotoDetail',
    component: () => import('@/views/PhotoDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/components/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/AdminPosts.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'photos',
        name: 'AdminPhotos',
        component: () => import('@/views/admin/AdminPhotos.vue'),
        meta: { title: '摄影管理' }
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/AdminSettings.vue'),
        meta: { title: '系统设置' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/AdminProfile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'tags',
        name: 'AdminTags',
        component: () => import('@/views/admin/AdminTags.vue'),
        meta: { title: '标签管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'comments/:articleId',
        name: 'AdminComments',
        component: () => import('@/views/admin/CommentManage.vue'),
        meta: { title: '评论管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  const tempUserId = localStorage.getItem('temp_user_id')
  const isGuest = !token && !!tempUserId

  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    next('/login')
  } else if (to.path.startsWith('/admin')) {
    if (role !== 'admin') {
      if (isGuest) {
        ElMessage.error('游客仅可浏览，请登录')
      } else {
        ElMessage.error('没有权限访问管理后台')
      }
      next('/home')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router