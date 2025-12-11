/**
 * 表单验证工具
 */

// 验证用户名
export function validateUsername(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度应在3-20个字符之间'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else {
    callback()
  }
}

// 验证密码
export function validatePassword(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6个字符'))
  } else {
    callback()
  }
}

// 验证手机号
export function validatePhone(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 验证邮箱
export function validateEmail(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

// 验证验证码
export function validateCaptcha(rule, value, callback) {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else if (value.length !== 4) {
    callback(new Error('验证码长度应为4位'))
  } else {
    callback()
  }
}

