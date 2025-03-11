import { mount } from '@vue/test-utils'
import SettingSidebar from '@/components/SettingSidebar.vue'
import { getTranslation } from '@/assets/language'

// Mock getTranslation
jest.mock('@/assets/language', () => ({
  getTranslation: jest.fn((lang, key) => key)
}))

describe('SettingSidebar.vue', () => {
  it('renders correctly', () => {
    const wrapper = mount(SettingSidebar, {
      props: {
        currentLanguage: 'en'
      }
    })
    
    // Check if sidebar exists
    expect(wrapper.exists()).toBe(true)
    
    // Check if it has an aside element
    expect(wrapper.find('aside').exists()).toBe(true)
  })
  
  it('has a settings button', () => {
    const wrapper = mount(SettingSidebar, {
      props: {
        currentLanguage: 'en'
      }
    })
    
    // Check if the component has a settings button
    const settingsButton = wrapper.find('.settings-btn')
    expect(settingsButton.exists()).toBe(true)
  })
  
  it('shows settings modal when button is clicked', async () => {
    const wrapper = mount(SettingSidebar, {
      props: {
        currentLanguage: 'en'
      }
    })
    
    // Find settings button
    const settingsButton = wrapper.find('.settings-btn')
    
    // Click the button
    await settingsButton.trigger('click')
    
    // Wait for the modal to appear
    await wrapper.vm.$nextTick()
    
    // Check if the modal is now visible - using the actual class name
    expect(wrapper.find('.modal-overlay').exists()).toBe(true)
  })
})