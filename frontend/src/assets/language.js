import {colorSchemes} from "@/assets/color";

const translations = {
    en: {
        UNDEFINED: "Undefined",
        SETTINGS: "Settings",
        LANGUAGE: "Language",
        HIGH_CONTRAST_MODE: "High contrast mode",
        TURN_ON: "Turn on",
        TURN_OFF: "Turn off",
        LOG_OUT: "Log out",
        CLOSE: "Close",
    },
    zh: {
        // TODO: Add Chinese translation
        UNDEFINED: "Xiaobai",
    },
    ru: {
        UNDEFINED: "Перевод недоступен",
        SETTINGS: "Настройки",
        LANGUAGE: "Язык",
        HIGH_CONTRAST_MODE: "Режим высокой контрастности",
        TURN_ON: "Включить",
        TURN_OFF: "Выключить",
        LOG_OUT: "Выйти из аккаунта",
        CLOSE: "Закрыть",
    }
};

// Export with a fallback function
const getTranslation = (langCode = "en", phraseCode = "UNDEFINED") => translations[langCode][phraseCode] || translations[langCode]["UNDEFINED"] || translations["en"]["UNDEFINED"];

export { translations, getTranslation };
