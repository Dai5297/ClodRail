<template>
  <div class="portal-nav-shell">
    <div class="portal-nav">
      <router-link to="/" class="brand" aria-label="ClodRail 首页" @click="closeMenu">
        <span class="brand__mark" aria-hidden="true">
          <img src="/logo-light.png" alt="" />
        </span>
        <span class="brand__copy">
          <strong>ClodRail</strong>
          <small>云端铁路出行</small>
        </span>
      </router-link>

      <nav class="desktop-nav" aria-label="主导航">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="desktop-nav__link"
          :class="{ 'desktop-nav__link--active': isActive(item.path) }"
          :aria-current="isActive(item.path) ? 'page' : undefined"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="portal-nav__actions">
        <a-dropdown placement="bottomRight" :trigger="['click']">
          <button type="button" class="account-trigger" aria-label="打开用户菜单">
            <a-avatar :size="34" :src="userAvatar" class="account-trigger__avatar">
              <template v-if="!userAvatar" #icon><i class="ri-user-line"></i></template>
            </a-avatar>
            <span class="account-trigger__name">{{ username }}</span>
            <i class="ri-arrow-down-s-line account-trigger__arrow" aria-hidden="true"></i>
          </button>
          <template #overlay>
            <a-menu class="account-menu">
              <a-menu-item key="user" @click="onGoToUserCenter">
                <template #icon><i class="ri-user-line"></i></template>
                个人中心
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout" class="account-menu__logout" @click="onLogout">
                <template #icon><i class="ri-logout-box-r-line"></i></template>
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>

        <button
          ref="menuToggle"
          type="button"
          class="menu-toggle"
          :aria-expanded="isMenuOpen"
          aria-controls="mobile-navigation"
          :aria-label="isMenuOpen ? '关闭导航菜单' : '打开导航菜单'"
          @click="toggleMenu"
        >
          <i :class="isMenuOpen ? 'ri-close-line' : 'ri-menu-line'" aria-hidden="true"></i>
        </button>
      </div>
    </div>
  </div>

  <Transition name="drawer-fade">
    <button
      v-if="isMenuOpen"
      type="button"
      class="drawer-backdrop"
      aria-label="关闭导航菜单"
      @click="closeMenu"
    ></button>
  </Transition>

  <Transition name="drawer-slide">
    <aside
      v-if="isMenuOpen"
      id="mobile-navigation"
      ref="mobileDrawer"
      class="mobile-drawer"
      role="dialog"
      aria-modal="true"
      aria-labelledby="mobile-navigation-title"
      @keydown="handleDrawerKeydown"
    >
      <div class="mobile-drawer__header">
        <div class="mobile-drawer__heading">
          <span id="mobile-navigation-title">快捷导航</span>
          <small>选择要前往的服务</small>
        </div>
        <button
          type="button"
          class="mobile-drawer__close"
          aria-label="关闭导航菜单"
          @click="closeMenu"
        >
          <i class="ri-close-line" aria-hidden="true"></i>
          <span>关闭</span>
        </button>
      </div>
      <nav class="mobile-nav" aria-label="移动端主导航">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="mobile-nav__link"
          :class="{ 'mobile-nav__link--active': isActive(item.path) }"
          :aria-current="isActive(item.path) ? 'page' : undefined"
          @click="closeMenu"
        >
          <i :class="item.icon" aria-hidden="true"></i>
          <span>{{ item.label }}</span>
          <i class="ri-arrow-right-s-line mobile-nav__arrow" aria-hidden="true"></i>
        </router-link>
      </nav>
      <div class="mobile-drawer__account">
        <button type="button" @click="goToUserCenterFromDrawer">
          <i class="ri-user-line" aria-hidden="true"></i>
          进入个人中心
        </button>
        <button type="button" class="mobile-drawer__logout" @click="logoutFromDrawer">
          <i class="ri-logout-box-r-line" aria-hidden="true"></i>
          退出登录
        </button>
      </div>
    </aside>
  </Transition>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isMenuOpen = ref(false)
const menuToggle = ref(null)
const mobileDrawer = ref(null)

let desktopMediaQuery
let bodyOverflowBeforeMenu = null
let shouldRestoreMenuFocus = false

const props = defineProps({
  username: {
    type: String,
    default: '用户名',
  },
  userAvatar: {
    type: String,
    default: '',
  },
  onGoToUserCenter: {
    type: Function,
    required: true,
  },
  onLogout: {
    type: Function,
    required: true,
  },
})

const menuItems = [
  { path: '/', label: '首页', icon: 'ri-home-5-line' },
  { path: '/ticket', label: '车票', icon: 'ri-ticket-2-line' },
  { path: '/mall', label: '积分商城', icon: 'ri-gift-line' },
  { path: '/user', label: '个人中心', icon: 'ri-user-3-line' },
  { path: '/assistant', label: 'AI 助手', icon: 'ri-customer-service-2-line' },
]

const isActive = (path) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

const closeMenu = (restoreFocus = true) => {
  if (!isMenuOpen.value) return
  shouldRestoreMenuFocus = restoreFocus !== false
  isMenuOpen.value = false
}

const toggleMenu = () => {
  if (isMenuOpen.value) {
    closeMenu()
  } else {
    isMenuOpen.value = true
  }
}

const goToUserCenterFromDrawer = () => {
  closeMenu()
  props.onGoToUserCenter()
}

const logoutFromDrawer = () => {
  closeMenu()
  props.onLogout()
}

const handleEscape = (event) => {
  if (event.key === 'Escape' && isMenuOpen.value) {
    event.preventDefault()
    closeMenu()
  }
}

const getDrawerFocusableElements = () => {
  if (!mobileDrawer.value) return []
  return Array.from(
    mobileDrawer.value.querySelectorAll(
      'a[href], button:not([disabled]), [tabindex]:not([tabindex="-1"])',
    ),
  ).filter((element) => !element.hasAttribute('hidden'))
}

const handleDrawerKeydown = (event) => {
  if (event.key !== 'Tab') return

  const focusableElements = getDrawerFocusableElements()
  if (!focusableElements.length) {
    event.preventDefault()
    return
  }

  const firstElement = focusableElements[0]
  const lastElement = focusableElements[focusableElements.length - 1]
  const activeElement = document.activeElement
  const focusIsOutsideDrawer = !mobileDrawer.value?.contains(activeElement)

  if (focusIsOutsideDrawer) {
    event.preventDefault()
    ;(event.shiftKey ? lastElement : firstElement).focus()
  } else if (event.shiftKey && activeElement === firstElement) {
    event.preventDefault()
    lastElement.focus()
  } else if (!event.shiftKey && activeElement === lastElement) {
    event.preventDefault()
    firstElement.focus()
  }
}

const restoreBodyOverflow = () => {
  if (bodyOverflowBeforeMenu === null) return
  document.body.style.overflow = bodyOverflowBeforeMenu
  bodyOverflowBeforeMenu = null
}

const handleDesktopChange = (event) => {
  if (event.matches) closeMenu(false)
}

watch(
  () => route.fullPath,
  () => closeMenu(),
)

watch(isMenuOpen, async (open) => {
  if (open) {
    bodyOverflowBeforeMenu = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    await nextTick()
    getDrawerFocusableElements()[0]?.focus()
    return
  }

  restoreBodyOverflow()
  if (shouldRestoreMenuFocus) {
    await nextTick()
    menuToggle.value?.focus()
  }
  shouldRestoreMenuFocus = false
})

onMounted(() => {
  window.addEventListener('keydown', handleEscape)
  desktopMediaQuery = window.matchMedia('(min-width: 900px)')
  desktopMediaQuery.addEventListener('change', handleDesktopChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleEscape)
  desktopMediaQuery?.removeEventListener('change', handleDesktopChange)
  restoreBodyOverflow()
})
</script>

<style scoped>
.portal-nav-shell {
  height: 100%;
  background: #fff;
}

.portal-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: min(100% - 32px, 1200px);
  height: 100%;
  margin: 0 auto;
}

.brand {
  display: inline-flex;
  align-items: center;
  flex: 0 0 auto;
  gap: 10px;
  color: var(--rail-navy);
  text-decoration: none;
}

.brand__mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  overflow: hidden;
  border-radius: 10px;
  background: var(--rail-navy);
}

.brand__mark img {
  display: block;
  width: 34px;
  height: 34px;
  object-fit: contain;
}

.brand__copy {
  display: grid;
  line-height: 1.1;
}

.brand__copy strong {
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 20px;
  letter-spacing: 0.015em;
}

.brand__copy small {
  margin-top: 4px;
  color: var(--rail-muted);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.15em;
}

.desktop-nav {
  display: flex;
  align-self: stretch;
  gap: 4px;
  margin-left: auto;
  margin-right: 28px;
}

.desktop-nav__link {
  position: relative;
  display: inline-flex;
  align-items: center;
  padding: 0 16px;
  color: #475467;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: color 160ms ease, background-color 160ms ease;
}

.desktop-nav__link::after {
  position: absolute;
  right: 16px;
  bottom: -1px;
  left: 16px;
  height: 3px;
  background: var(--rail-primary);
  content: '';
  opacity: 0;
  transform: scaleX(0.45);
  transition: opacity 160ms ease, transform 160ms ease;
}

.desktop-nav__link:hover {
  background: #f8fafc;
  color: var(--rail-navy);
}

.desktop-nav__link--active {
  color: var(--rail-primary);
}

.desktop-nav__link--active::after {
  opacity: 1;
  transform: scaleX(1);
}

.portal-nav__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.account-trigger {
  display: flex;
  align-items: center;
  gap: 9px;
  min-height: 42px;
  padding: 4px 8px 4px 5px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: transparent;
  color: var(--rail-ink);
  cursor: pointer;
  transition: border-color 160ms ease, background-color 160ms ease;
}

.account-trigger:hover {
  border-color: var(--rail-line);
  background: var(--rail-background);
}

.account-trigger__avatar {
  background: var(--rail-primary);
  color: #fff;
}

.account-trigger__name {
  max-width: 92px;
  overflow: hidden;
  font-size: 14px;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-trigger__arrow {
  color: #98a2b3;
  font-size: 16px;
}

.menu-toggle {
  display: none;
  place-items: center;
  width: 42px;
  height: 42px;
  border: 1px solid var(--rail-line);
  border-radius: 8px;
  background: #fff;
  color: var(--rail-navy);
  cursor: pointer;
  font-size: 22px;
}

.account-menu {
  min-width: 150px;
  padding: 6px;
  border: 1px solid var(--rail-line);
  box-shadow: var(--rail-shadow-soft);
}

.drawer-backdrop,
.mobile-drawer {
  display: none;
}

@media (max-width: 899px) {
  .desktop-nav {
    display: none;
  }

  .menu-toggle {
    display: grid;
  }

  .drawer-backdrop {
    position: fixed;
    z-index: 48;
    top: 72px;
    right: 0;
    bottom: 0;
    left: 0;
    display: block;
    width: 100%;
    border: 0;
    background: rgba(11, 37, 89, 0.52);
    cursor: default;
  }

  .mobile-drawer {
    position: fixed;
    z-index: 49;
    top: 72px;
    right: 0;
    bottom: 0;
    display: flex;
    flex-direction: column;
    width: min(86vw, 360px);
    border-left: 1px solid var(--rail-line);
    background: #fff;
    box-shadow: -12px 0 32px rgba(11, 37, 89, 0.14);
  }

  .mobile-drawer__header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    padding: 24px 22px 18px;
    border-bottom: 1px solid var(--rail-line);
    color: var(--rail-navy);
    font-size: 18px;
    font-weight: 700;
  }

  .mobile-drawer__heading {
    display: grid;
    gap: 3px;
  }

  .mobile-drawer__header small {
    color: var(--rail-muted);
    font-size: 12px;
    font-weight: 400;
  }

  .mobile-drawer__close {
    display: inline-flex;
    flex: 0 0 auto;
    align-items: center;
    justify-content: center;
    gap: 4px;
    min-height: 36px;
    padding: 0 10px;
    border: 1px solid var(--rail-line);
    border-radius: 8px;
    background: #fff;
    color: var(--rail-navy);
    cursor: pointer;
    font-size: 13px;
    font-weight: 600;
  }

  .mobile-drawer__close:hover {
    border-color: var(--rail-primary);
    background: var(--rail-primary-soft);
    color: var(--rail-primary-active);
  }

  .mobile-drawer__close:focus-visible {
    outline: 3px solid rgba(22, 119, 255, 0.24);
    outline-offset: 2px;
  }

  .mobile-drawer__close i {
    font-size: 18px;
  }

  .mobile-nav {
    display: grid;
    gap: 6px;
    padding: 14px;
  }

  .mobile-nav__link {
    display: grid;
    grid-template-columns: 24px 1fr 20px;
    align-items: center;
    min-height: 50px;
    padding: 0 12px;
    border-left: 3px solid transparent;
    border-radius: 8px;
    color: #344054;
    font-size: 15px;
    font-weight: 600;
    text-decoration: none;
  }

  .mobile-nav__link > i:first-child {
    color: #667085;
    font-size: 19px;
  }

  .mobile-nav__link--active {
    border-left-color: var(--rail-primary);
    background: var(--rail-primary-soft);
    color: var(--rail-primary-active);
  }

  .mobile-nav__arrow {
    color: #98a2b3;
    font-size: 18px;
  }

  .mobile-drawer__account {
    display: grid;
    gap: 8px;
    margin-top: auto;
    padding: 18px 14px 24px;
    border-top: 1px solid var(--rail-line);
  }

  .mobile-drawer__account button {
    display: flex;
    align-items: center;
    gap: 10px;
    min-height: 44px;
    padding: 0 14px;
    border: 1px solid var(--rail-line);
    border-radius: 8px;
    background: #fff;
    color: var(--rail-ink);
    cursor: pointer;
    font-weight: 600;
  }

  .mobile-drawer__account .mobile-drawer__logout {
    color: var(--rail-danger);
  }

  .drawer-fade-enter-active,
  .drawer-fade-leave-active,
  .drawer-slide-enter-active,
  .drawer-slide-leave-active {
    transition: opacity 180ms ease, transform 180ms ease;
  }

  .drawer-fade-enter-from,
  .drawer-fade-leave-to {
    opacity: 0;
  }

  .drawer-slide-enter-from,
  .drawer-slide-leave-to {
    transform: translateX(100%);
  }
}

@media (max-width: 520px) {
  .portal-nav {
    width: min(100% - 24px, 1200px);
  }

  .brand__mark {
    width: 38px;
    height: 38px;
  }

  .brand__mark img {
    width: 31px;
    height: 31px;
  }

  .brand__copy strong {
    font-size: 18px;
  }

  .brand__copy small,
  .account-trigger__name,
  .account-trigger__arrow {
    display: none;
  }

  .account-trigger {
    width: 42px;
    padding: 3px;
    justify-content: center;
  }
}

@media (prefers-reduced-motion: reduce) {
  .desktop-nav__link::after,
  .drawer-fade-enter-active,
  .drawer-fade-leave-active,
  .drawer-slide-enter-active,
  .drawer-slide-leave-active {
    transition: none;
  }
}
</style>
