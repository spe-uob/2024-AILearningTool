import { mount } from '@vue/test-utils'
import Cookie from '@/Display interface/Cookie.vue'
import { getTranslation } from '@/assets/language'

// Mock getTranslation
jest.mock('@/assets/language', () => ({
  getTranslation: jest.fn((lang, key) => key)
}))

describe('Cookie.vue', () => {
  it('renders correctly', () => {
    const wrapper = mount(Cookie, {
      props: {
        currentLanguage: 'en'
      }
    })
    
    // Check if cookie popup is displayed
    expect(wrapper.find('.cookie-popup').exists()).toBe(true)
    expect(wrapper.find('.cookie-content').exists()).toBe(true)
    
    // Check if button is displayed
    const button = wrapper.find('.accept-button')
    expect(button.exists()).toBe(true)
    expect(button.text()).toBe('I_UNDERSTAND')
  })
  
  it('emits consent-choice event when button is clicked', async () => {
    const wrapper = mount(Cookie, {
      props: {
        currentLanguage: 'en'
      }
    })
    
    // Mock document.cookie
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: ''
    })
    
    // Click the accept button
    await wrapper.find('.accept-button').trigger('click')
    
    // Check if consent-choice event was emitted with true
    expect(wrapper.emitted()).toHaveProperty('consent-choice')
    expect(wrapper.emitted()['consent-choice'][0]).toEqual([true])
    
    // Check if cookie was set
    expect(document.cookie).toContain('optionalConsent=true')
  })
})