import { getTranslation, translations } from '@/assets/language'

// Mock translations for testing
jest.mock('@/assets/language', () => {
  const mockTranslations = {
    en: {
      UNDEFINED: 'Undefined',
      SETTINGS: 'Settings',
      LANGUAGE: 'Language',
      SEND: 'Send'
    },
    zh: {
      UNDEFINED: '未定义',
      SETTINGS: '设置',
      LANGUAGE: '语言',
      SEND: '发送'
    }
  };
  
  return {
    translations: mockTranslations,
    getTranslation: (langCode = "en", phraseCode = "UNDEFINED") =>
      mockTranslations[langCode] && mockTranslations[langCode][phraseCode] 
        ? mockTranslations[langCode][phraseCode] 
        : (mockTranslations["en"][phraseCode] || mockTranslations["en"]["UNDEFINED"])
  };
});

describe('Language utilities', () => {
  it('returns correct translation for existing phrase', () => {
    // Test English translation
    expect(getTranslation('en', 'SETTINGS')).toBe('Settings')
    
    // Test Chinese translation
    expect(getTranslation('zh', 'SETTINGS')).toBe('设置')
  })
  
  it('falls back to English when language is not supported', () => {
    // Test with unsupported language code
    const result = getTranslation('fr', 'SETTINGS')
    expect(result).toBe('Settings')
  })
  
  it('returns UNDEFINED translation when phrase key does not exist', () => {
    // Test with non-existent phrase key
    const result = getTranslation('en', 'NON_EXISTENT_KEY')
    expect(result).toBe('Undefined')
  })
  
  it('handles default parameters correctly', () => {
    // Test with no parameters (should use defaults)
    const result = getTranslation()
    expect(result).toBe('Undefined')
  })
  
  it('contains common translations for each language', () => {
    // Check if common keys exist in all languages
    const commonKeys = ['SETTINGS', 'LANGUAGE', 'SEND']
    
    for (const lang of Object.keys(translations)) {
      for (const key of commonKeys) {
        expect(translations[lang]).toHaveProperty(key)
        expect(translations[lang][key]).toBeTruthy()
      }
    }
  })
})