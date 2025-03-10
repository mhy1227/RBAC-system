export const getItem = <T>(key: string): T | null => {
  const data = window.localStorage.getItem(key)
  if (!data) return null
  try {
    return JSON.parse(data) as T
  } catch (err) {
    return null
  }
}

export const setItem = (key: string, value: unknown): void => {
  if (typeof value === 'string') {
    window.localStorage.setItem(key, value)
  } else {
    window.localStorage.setItem(key, JSON.stringify(value))
  }
}

export const removeItem = (key: string): void => {
  window.localStorage.removeItem(key)
}

export const clearItems = (): void => {
  window.localStorage.clear()
}
