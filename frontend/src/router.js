// src/router.js
import { createRouter, createWebHistory } from 'vue-router';
import EditingPage from './pages/EditingPage.vue';
import LandingPage from './pages/LandingPage.vue';

const routes = [
  {
    path: '/',
    name: 'LandingPage',
    component: LandingPage,
  },
  {
    path: '/edit',
    name: 'EditingPage',
    component: EditingPage,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
