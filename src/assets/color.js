const colorSchemes = {
    default: {
        primary: "#5C88DA",  // IBM Blue
        secondary: "#FFFFE0",  // Light Yellow
        accent: "#CDDAF3",  // Pastel Blue
        background: "#F4F4F4",  // Light Gray
        text: "#2E2E2E",  // Dark Gray
        border: "#D3D3D3",  // Light Border Gray
        button: "#5C88DA",  // IBM Blue
        error: "#E74C3C",  // Red
        success: "#27AE60",  // Green
    },
    high_contrast: {
        primary: "#002147",  // Deep Navy Blue
        secondary: "#FFD700",  // Bright Gold (high contrast)
        accent: "#FF4500",  // Bright Orange-Red
        background: "#001F3F",  // Dark Blue (better than pure black)
        text: "#FFFFFF",  // White for max readability
        border: "#FFD700",  // Gold for clear separation
        button: "#00FFFF",  // Cyan for distinct interaction
        error: "#FF0000",  // Bright Red for warnings
        success: "#00FF00",  // Neon Green for success indicators
    },
};

// Export with a fallback function
const getTheme = (themeName = "default") =>
    colorSchemes[themeName] || colorSchemes.default;

export { colorSchemes, getTheme };
