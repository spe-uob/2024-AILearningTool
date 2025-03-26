import { mount } from '@vue/test-utils'
import HistorySidebar from '@/components/HistorySidebar.vue'
import { getTranslation } from '@/assets/language'

// Mock getTranslation
jest.mock('@/assets/language', () => ({
  getTranslation: jest.fn((lang, key) => key)
}))

// Mock the getTheme function
jest.mock('@/assets/color.js', () => ({
  getTheme: jest.fn(() => ({
    background: '#F4F4F4',
    text: '#2E2E2E',
    border: '#D3D3D3',
    button: '#5C88DA'
  }))
}))

describe('HistorySidebar.vue', () => {
  it('renders correctly when expanded', async () => {
    const wrapper = mount(HistorySidebar, {
      props: {
        chats: [
          { chatID: '1', title: 'Chat 1' },
          { chatID: '2', title: 'Chat 2' }
        ],
        currentLanguage: 'en',
        currentChatID: '1'
      },
      global: {
        stubs: {
          transition: false
        }
      }
    })
    
    // Manually expand the sidebar since it's collapsed by default
    await wrapper.setData({ isCollapsed: false });
    await wrapper.vm.$nextTick();
    
    // Check if component exists
    expect(wrapper.exists()).toBe(true)
    
    // Check if chat items are rendered - should be 3 items (1 new chat button + 2 chat history items)
    const chatItems = wrapper.findAll('.chat-item')
    expect(chatItems.length).toBe(3)
  })
  
  it('highlights the current chat', async () => {
    const wrapper = mount(HistorySidebar, {
      props: {
        chats: [
          { chatID: '1', title: 'Chat 1' },
          { chatID: '2', title: 'Chat 2' }
        ],
        currentLanguage: 'en',
        currentChatID: '1'
      },
      global: {
        stubs: {
          transition: false
        }
      }
    })
    
    // Manually expand the sidebar since it's collapsed by default
    await wrapper.setData({ isCollapsed: false });
    await wrapper.vm.$nextTick();
    
    // Find all chat items in the history list (excluding the new chat button)
    const chatItems = wrapper.findAll('.history-list-wrapper .chat-item')
    
    // Check if the current chat has both 'selectable-chat' and 'selected' classes
    expect(chatItems[0].classes()).toContain('selectable-chat')
    expect(chatItems[0].classes()).toContain('selected')
  })
  
  it('emits chatSelected event when a chat is clicked', async () => {
    const wrapper = mount(HistorySidebar, {
      props: {
        chats: [
          { chatID: '1', title: 'Chat 1' },
          { chatID: '2', title: 'Chat 2' }
        ],
        currentLanguage: 'en',
        currentChatID: '1'
      },
      global: {
        stubs: {
          transition: false
        }
      }
    })
    
    // Manually expand the sidebar since it's collapsed by default
    await wrapper.setData({ isCollapsed: false });
    await wrapper.vm.$nextTick();
    
    // Find chat items in the history list
    const chatItems = wrapper.findAll('.history-list-wrapper .chat-item')
    
    // Click on the second chat (index 1)
    await chatItems[1].trigger('click')
    
    // Check if chatSelected event was emitted with the correct chatID ('2')
    expect(wrapper.emitted()).toHaveProperty('chatSelected')
    expect(wrapper.emitted().chatSelected[0]).toEqual(['2'])
  })
  
  it('has a new chat button', async () => {
    const wrapper = mount(HistorySidebar, {
      props: {
        chats: [
          { chatID: '1', title: 'Chat 1' },
          { chatID: '2', title: 'Chat 2' }
        ],
        currentLanguage: 'en',
        currentChatID: '1'
      },
      global: {
        stubs: {
          transition: false
        }
      }
    })
    
    // Manually expand the sidebar since it's collapsed by default
    await wrapper.setData({ isCollapsed: false });
    await wrapper.vm.$nextTick();
    
    // Find the new chat button (first chat-item, not in history-list-wrapper)
    const newChatButton = wrapper.find('button.chat-item:not(.history-list-wrapper .chat-item)')
    expect(newChatButton.exists()).toBe(true)
    
    // Click the new chat button
    await newChatButton.trigger('click')
    
    // Check if resetMainContent event was emitted
    expect(wrapper.emitted()).toHaveProperty('resetMainContent')
  })
})
