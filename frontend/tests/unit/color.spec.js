import { getTheme, colorSchemes } from '@/assets/color'

describe('Color utilities', () => {
  it('returns correct theme for default theme', () => {
    const theme = getTheme('default')
    
    // Check if all properties exist
    expect(theme).toHaveProperty('primary')
    expect(theme).toHaveProperty('secondary')
    expect(theme).toHaveProperty('accent')
    expect(theme).toHaveProperty('background')
    expect(theme).toHaveProperty('text')
    expect(theme).toHaveProperty('border')
    expect(theme).toHaveProperty('button')
    expect(theme).toHaveProperty('error')
    expect(theme).toHaveProperty('success')
    
    // Check specific values
    expect(theme.primary).toBe('#5C88DA')
    expect(theme.background).toBe('#F4F4F4')
  })
  
  it('returns correct theme for high contrast theme', () => {
    const theme = getTheme('high_contrast')
    
    // Check specific values for high contrast
    expect(theme.primary).toBe('#0A6CFF')
    expect(theme.background).toBe('#FFFFFF')
    expect(theme.text).toBe('#000000')
  })
  
  it('falls back to default theme when theme name is not found', () => {
    const theme = getTheme('non_existent_theme')
    
    // Should return default theme
    expect(theme).toEqual(colorSchemes.default)
  })
  
  it('uses default theme when no theme name is provided', () => {
    const theme = getTheme()
    
    // Should return default theme
    expect(theme).toEqual(colorSchemes.default)
  })
  
  it('contains all required properties in each theme', () => {
    const requiredProps = [
      'primary', 'secondary', 'accent', 'background',
      'text', 'border', 'button', 'error', 'success'
    ]
    
    // Check all themes
    for (const themeName in colorSchemes) {
      const theme = colorSchemes[themeName]
      
      // Each theme should have all required properties
      for (const prop of requiredProps) {
        expect(theme).toHaveProperty(prop)
        expect(theme[prop]).toBeTruthy()
      }
    }
  })
})