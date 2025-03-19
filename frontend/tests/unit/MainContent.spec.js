import { mount, flushPromises } from '@vue/test-utils'
import MainContent from '@/components/MainContent.vue'

// Mock getTranslation
jest.mock('@/assets/language', () => ({
  getTranslation: jest.fn((lang, key) => key)
}))

// Mock marked
jest.mock('marked', () => ({
  parse: jest.fn(text => `<p>${text}</p>`)
}))

// Mock getTheme
jest.mock('@/assets/color.js', () => ({
  getTheme: jest.fn(() => ({
    background: '#F4F4F4',
    text: '#2E2E2E'
  }))
}))

describe('MainContent.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })
  
  it('renders correctly', () => {
    const wrapper = mount(MainContent, {
      props: {
        messages: [],
        chats: [],
        currentChatID: '',
        currentLanguage: 'en',
        chatInitButtonsDisabled: false
      }
    })
    
    // Check if component exists
    expect(wrapper.exists()).toBe(true)
  })
  
  // Add more basic tests that don't rely on complex component structure
  it('has message input when chat is active', () => {
    const wrapper = mount(MainContent, {
      props: {
        messages: [],
        chats: [],
        currentChatID: '123', // Active chat
        currentLanguage: 'en',
        chatInitButtonsDisabled: false
      }
    })
    
    // Check if input area exists
    const inputArea = wrapper.find('input[type="text"], textarea')
    expect(inputArea.exists()).toBe(true)
  })
})