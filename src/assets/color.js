const colorSchemes = {
    default: {
        primary: "#5C88DA",  // IBM Blue
        secondary: "#FFFFE0",  // Light Yellow
        accent: "#CDDAF3",  // Pastel Blue
        background: "#F4F4F4",  // Light Gray
        text: "#2E2E2E",  // Dark Grey
        border: "#D3D3D3",  // Light Border Gray
        button: "#5C88DA",  // IBM Blue
        error: "#E74C3C",  // Red
        success: "#27AE60",  // Green
    },
    high_contrast: {
        primary: "#0050C8",  // Stronger IBM Blue
        secondary: "#FFEB99",  // Brighter Yellow 
        accent: "#A3C4F3",  // More vivid pastel blue
        background: "#767676",  // Grey
        text: "#FFFFFF",  // White
        border: "#A0A0A0",  // Medium Gray for stronger borders
        button: "#0050C8",  // Stronger IBM Blue 
        error: "#CC0000",  // Deeper Red for better visibility
        success: "#008F28",  // Stronger Green 
    },
};

// Export with a fallback function
const getTheme = (themeName = "default") =>
    colorSchemes[themeName] || colorSchemes.default;

export { colorSchemes, getTheme };
