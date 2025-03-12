import { mount } from '@vue/test-utils'
import App from '@/App.vue'
import SettingSidebar from '@/components/SettingSidebar.vue'
import { getTheme } from '@/assets/color.js'

// Mock vue-router
jest.mock('vue-router', () => ({
  useRouter: jest.fn(() => ({
    beforeEach: jest.fn()
  }))
}))

// Mock getTheme
jest.mock('@/assets/color.js', () => ({
  getTheme: jest.fn(() => ({
    primary: '#5C88DA',
    secondary: '#FFFFE0',
    accent: '#CDDAF3',
    background: '#F4F4F4',
    text: '#2E2E2E',
    border: '#D3D3D3',
    button: '#5C88DA',
    error: '#E74C3C',
    success: '#27AE60'
  }))
}))

// Mock components
jest.mock('@/components/SettingSidebar.vue', () => ({
  name: 'SettingSidebar',
  render: () => {}
}))

// Mock document.documentElement.style.setProperty
document.documentElement.style.setProperty = jest.fn();

describe('App.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(() => 'en'),
        setItem: jest.fn()
      }
    })
    
    // Clear mock calls
    document.documentElement.style.setProperty.mockClear();
  })
  
  it('renders correctly', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          RouterView: true,
          SettingSidebar: true
        },
        mocks: {
          $route: {
            path: '/main'
          }
        }
      }
    })
    
    // Check if app container exists
    expect(wrapper.find('#app').exists()).toBe(true)
    
    // Check if SettingSidebar is rendered
    expect(wrapper.findComponent(SettingSidebar).exists()).toBe(true)
  })
  
  it('applies theme on mount', () => {
    mount(App, {
      global: {
        stubs: {
          RouterView: true,
          SettingSidebar: true
        },
        mocks: {
          $route: {
            path: '/main'
          }
        }
      }
    })
    
    // Check if getTheme was called with 'default'
    expect(getTheme).toHaveBeenCalledWith('default')
    
    // Check if CSS variables were set
    expect(document.documentElement.style.setProperty).toHaveBeenCalled()
  })
  
  it('changes language correctly', async () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          RouterView: true,
          SettingSidebar: true
        },
        mocks: {
          $route: {
            path: '/main'
          }
        }
      }
    })
    
    // Call changeLanguage method
    await wrapper.vm.changeLanguage('zh')
    
    // Check if currentLanguage was updated
    expect(wrapper.vm.currentLanguage).toBe('zh')
    
    // Check if localStorage was updated
    expect(localStorage.setItem).toHaveBeenCalledWith('langCode', 'zh')
  })
  
  it('toggles high contrast mode correctly', async () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          RouterView: true,
          SettingSidebar: true
        },
        mocks: {
          $route: {
            path: '/main'
          }
        }
      }
    })
    
    // Reset mock to clear initial calls
    getTheme.mockClear()
    document.documentElement.style.setProperty.mockClear()
    
    // Call onHighContrastToggled method with true
    await wrapper.vm.onHighContrastToggled(true)
    
    // Check if currentTheme was updated
    expect(wrapper.vm.currentTheme).toBe('high_contrast')
    
    // Check if getTheme was called with 'high_contrast'
    expect(getTheme).toHaveBeenCalledWith('high_contrast')
  })
  
  it('does not render SettingSidebar on login page', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          RouterView: true,
          SettingSidebar: true
        },
        mocks: {
          $route: {
            path: '/login'
          }
        }
      }
    })
    
    // SettingSidebar should not be rendered on login page
    expect(wrapper.findComponent(SettingSidebar).exists()).toBe(false)
  })
})