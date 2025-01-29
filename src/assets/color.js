const colorSchemes = {
    default: {
        primary: "#5C88DA",
        secondary: "#FFFFE0",
        accent: "#B0B0B0",
        background: "#F4F4F4",
        text: "#2E2E2E",
        border: "#D3D3D3",
        button: "#5C88DA",
        error: "#E74C3C",
        success: "#27AE60",
    },
    colorblind_red_green: {
        primary: "#1F4E79",
        secondary: "#F2C94C",
        accent: "#E94E77",
        background: "#F4F4F4",
        text: "#2E2E2E",
        border: "#B6B6B6",
        button: "#F2994A", 
        error: "#D33F49",  
        success: "#6BAF92", 
    },
};

// Export with a fallback function
const getTheme = (themeName = "default") =>
    colorSchemes[themeName] || colorSchemes.default;

export { colorSchemes, getTheme };
