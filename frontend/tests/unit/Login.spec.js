import { mount, flushPromises } from '@vue/test-utils'
import Login from '@/Display interface/Login.vue'
import { getTranslation } from '@/assets/language'

// Mock getTranslation
jest.mock('@/assets/language', () => ({
  getTranslation: jest.fn((lang, key) => key)
}))

// Mock router
const mockRouter = {
  push: jest.fn()
}

describe('Login.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn()
      }
    })
    
    // Mock fetch
    global.fetch = jest.fn()
    
    // Mock alert
    global.alert = jest.fn()
  })
  
  it('renders login form by default', () => {
    const wrapper = mount(Login, {
      props: {
        currentLanguage: 'en'
      },
      global: {
        mocks: {
          $router: mockRouter
        }
      }
    })
    
    // Check if login form is displayed
    expect(wrapper.find('h2').text()).toBe('LOG_IN')
    expect(wrapper.find('#username').exists()).toBe(true)
    expect(wrapper.find('#password').exists()).toBe(true)
    expect(wrapper.find('#confirmPassword').exists()).toBe(false) // Not in login mode
  })
  
  it('switches to register mode when toggle is clicked', async () => {
    const wrapper = mount(Login, {
      props: {
        currentLanguage: 'en'
      },
      global: {
        mocks: {
          $router: mockRouter
        }
      }
    })
    
    // Click toggle link
    await wrapper.find('.toggle-text span').trigger('click')
    
    // Check if register form is displayed
    expect(wrapper.find('h2').text()).toBe('REGISTER')
    expect(wrapper.find('#confirmPassword').exists()).toBe(true) // Should be in register mode
  })
  
  it('shows cookie consent message', async () => {
    const wrapper = mount(Login, {
      props: {
        currentLanguage: 'en'
      },
      global: {
        mocks: {
          $router: mockRouter
        }
      }
    })
    
    // Check if cookie message is displayed with the actual text
    expect(wrapper.find('.cookie-popup').exists()).toBe(true)
    expect(wrapper.find('.cookie-content p').text()).toBe('COOKIE_DISCLAIMER')
    
    // Accept cookies
    const acceptButton = wrapper.find('.accept-button')
    await acceptButton.trigger('click')
    
    // After accepting, the cookie message should be gone
    expect(wrapper.find('.cookie-popup').exists()).toBe(false)
  })
  
  it('redirects to main page if user is already logged in', async () => {
    // Mock localStorage to return a token
    window.localStorage.getItem.mockReturnValueOnce('user-123')
    
    const wrapper = mount(Login, {
      props: {
        currentLanguage: 'en'
      },
      global: {
        mocks: {
          $router: mockRouter
        }
      }
    })
    
    // Wait for mounted hook to complete
    await flushPromises()
    
    // Should redirect to main page
    expect(mockRouter.push).toHaveBeenCalledWith('/main')
  })
})