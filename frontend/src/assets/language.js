import {colorSchemes} from "@/assets/color";

const translations = {
    en: {
        UNDEFINED: "Undefined",
        SETTINGS: "Settings",
        LANGUAGE: "Language",
        HIGH_CONTRAST_MODE: "High contrast mode",
        TURN_ON: "Turn on",
        TURN_OFF: "Turn off",
        LOG_IN: "Log in",
        LOG_OUT: "Log out",
        REGISTER: "Register",
        CLOSE: "Close",
        NEW_CONVERSATION: "New conversation",
        I_NEED_HELP_WITH_CHOOSING_A_COURSE: "I need help with choosing a course",
        I_NEED_HELP_WITH_PLATFORM: "I need help with IBM SkillsBuild platform",
        I_HAVE_QUESTIONS_ABOUT_UNI_LIFE: "I have questions about university life",
        USER: "User",
        AI: "AI",
        TYPE_YOUR_MESSAGE: "Type your message...",
        SEND: "Send",
        WELCOME_TO_WATSONX_AI: "Welcome to Watsonx AI!",
        SELECT_INITIAL_TOPIC: "Select one of the topics below:",
        USERNAME: "Username",
        PASSWORD: "Password",
        CONFIRM_PASSWORD: "Confirm Password",
        DONT_HAVE_AN_ACCOUNT: "Don't have an account?",
        ALREADY_HAVE_AN_ACCOUNT: "Already have an account?",
        PLEASE_ACCEPT_COOKIES: "Please accept our cookie terms first.",
        PASSWORDS_DO_NOT_MATCH: "Passwords do not match!",
        REGISTRATION_ERROR: "An error occurred while trying to register.",
        REGISTRATION_SUCCESS: "Registration successful! Please login.",
        REGISTRATION_FAILED: "Registration failed!",
        LOGIN_FAILED: "Login failed!",
        COOKIE_DISCLAIMER: "Watsonx AI uses cookies. If you continue to use this site, you consent to the their storage in your browser.",
        I_UNDERSTAND: "I understand",
        PLEASE_ENTER_A_MESSAGE: "Please enter a message!",
        FAILED_TO_SEND_MESSAGE: "Failed to send message. Please try again."
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
        LOG_IN: "Войти",
        LOG_OUT: "Выйти из аккаунта",
        REGISTER: "Зарегистрироваться",
        CLOSE: "Закрыть",
        NEW_CONVERSATION: "Новый чат",
        I_NEED_HELP_WITH_CHOOSING_A_COURSE: "Мне нужна помощь с выбором онлайн-курса",
        I_HAVE_QUESTIONS_ABOUT_UNI_LIFE: "У меня есть вопросы про университетскую жизнь",
        USER: "Пользователь",
        AI: "ИИ",
        TYPE_YOUR_MESSAGE: "Введите ваше сообщение...",
        SEND: "Отправить",
        WELCOME_TO_WATSONX_AI: "Добро пожаловать в ИИ Watsonx!",
        SELECT_INITIAL_TOPIC: "Выберете тему для нового чата:",
        USERNAME: "Имя пользователя",
        PASSWORD: "Пароль",
        CONFIRM_PASSWORD: "Подтвердите пароль",
        DONT_HAVE_AN_ACCOUNT: "Нет аккаунта?",
        ALREADY_HAVE_AN_ACCOUNT: "Уже есть аккаунт?",
        PLEASE_ACCEPT_COOKIES: "Пожалуйста ознакомьтесь с условиями использования файлов куки.",
        PASSWORDS_DO_NOT_MATCH: "Пароли не совпадают!",
        REGISTRATION_SUCCESS: "Регистрация прошла успешно. Теперь войдите в аккаунт.",
        REGISTRATION_FAILED: "Регистрация не удалась.",
        REGISTRATION_ERROR: "При попытке регистрации произошла ошибка.",
        LOGIN_FAILED: "При попытке войти в аккаунт произошла ошибка.",
        COOKIE_DISCLAIMER: "ИИ Watsonx использует файлы куки. Продолжая использовать наше веб-приложение, вы соглашаетесь на хранение куки-файлов в вашем браузере.",
        I_UNDERSTAND: "Согласен",
        PLEASE_ENTER_A_MESSAGE: "Пожалуйста введите сообщение!",
        FAILED_TO_SEND_MESSAGE: "Не удалось отправить сообщение. Пожалуйста, попробуйте ещё раз."
    }
};

// Export with a fallback function
const getTranslation = (langCode = "en", phraseCode = "UNDEFINED") =>
    translations[langCode][phraseCode] || translations["en"][phraseCode] || translations["en"]["UNDEFINED"];

export { translations, getTranslation };
