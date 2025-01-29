const colorSchemes = {
    default: {
        primary: "#5C88DA",  // IBM Blue
        secondary: "#FFFFE0",  // Light Yellow
        accent: "#B0B0B0",  // Gray
        background: "#F4F4F4",  // Light Gray
        text: "#2E2E2E",  // Dark Gray
        border: "#D3D3D3",  // Light Border Gray
        button: "#5C88DA",  // IBM Blue
        error: "#E74C3C",  // Red
        success: "#27AE60",  // Green
    },
    colorblind_red_green: { //Blue-blind friendly mode
        primary: "#D17B0F",  // Deep Orange
        secondary: "#F2C14E",  // Gold
        accent: "#E88B8B",  // Soft Red-Pink
        background: "#F5E6CC",  // Warm Beige
        text: "#4A403A",  // Dark Brown
        border: "#A68A64",  // Warm Brown
        button: "#D17B0F",  // Deep Orange for emphasis
        error: "#D7263D",  // Bright Red for warnings
        success: "#A0C24D",  // Yellow-Green 
    },
};

// Export with a fallback function
const getTheme = (themeName = "default") =>
    colorSchemes[themeName] || colorSchemes.default;

export { colorSchemes, getTheme };
