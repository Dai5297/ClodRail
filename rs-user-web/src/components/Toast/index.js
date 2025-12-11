import { createVNode, render } from 'vue'
import ToastComponent from './Toast.vue'

let toastInstance = null

const initToast = () => {
  if (toastInstance) return toastInstance
  
  const container = document.createElement('div')
  document.body.appendChild(container)

  const vm = createVNode(ToastComponent)
  render(vm, container)
  
  toastInstance = vm.component.exposed
  return toastInstance
}

const getToast = () => {
  if (!toastInstance) {
    return initToast()
  }
  return toastInstance
}

const toast = {
  show(options) {
    getToast().add(options)
  },
  success(message, title, duration = 3000) {
    this.show({ type: 'success', message, title, duration })
  },
  error(message, title, duration = 3000) {
    this.show({ type: 'error', message, title, duration })
  },
  warning(message, title, duration = 3000) {
    this.show({ type: 'warning', message, title, duration })
  },
  info(message, title, duration = 3000) {
    this.show({ type: 'info', message, title, duration })
  }
}

export { toast }
export default toast
