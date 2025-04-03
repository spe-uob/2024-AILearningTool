import { mount, flushPromises } from '@vue/test-utils'
import MainContent from '@/components/MainContent.vue'

// Mock getTranslation
jest.mock('@/assets/language', () => ({
  getTranslation: jest.fn((lang, key) => key)
}))

// Mock marked
jest.mock('marked', () => ({
  marked: jest.fn(text => `<p>${text}</p>`)
}))

// Mock getTheme
jest.mock('@/assets/color.js', () => ({
  getTheme: jest.fn(() => ({
    background: '#F4F4F4',
    text: '#2E2E2E'
  }))
}))

// Mock SpeechSynthesis API
Object.defineProperty(window, 'speechSynthesis', {
  value: {
    speaking: false,
    getVoices: jest.fn(() => []),
    speak: jest.fn(),
    cancel: jest.fn(),
    onvoiceschanged: null
  }
})

describe('MainContent.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    
    // Mock localStorage with sessionID
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(() => 'test-session-id'),
        setItem: jest.fn()
      }
    })
    
    // Mock fetch
    global.fetch = jest.fn(() => 
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ content: 'AI response', chatID: '123' })
      })
    )
  })
  
  it('renders correctly', () => {
    const wrapper = mount(MainContent, {
      props: {
        messages: [],
        chats: [],
        currentChatID: '',
        currentLanguage: 'en',
        chatInitButtonsDisabled: false
      },
      global: {
        stubs: {
          TypingText: true // Stub the TypingText component
        }
      }
    })
    
    // Check if component exists
    expect(wrapper.exists()).toBe(true)
  })
  
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