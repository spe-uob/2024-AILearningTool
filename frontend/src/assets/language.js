import {colorSchemes} from "@/assets/color";

const translations = {
    en: {
        EXPORT: "Export",
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
        FAILED_TO_SEND_MESSAGE: "Failed to send message. Please try again.",
        SPEECH_RECOGNITION_NOT_SUPPORTED: "Speech recognition is not supported in your browser.",
        START_VOICE_INPUT: "Start voice input",
        STOP_VOICE_INPUT: "Stop voice input",
        LISTENING: "Listening...",
        VOICE_RECOGNITION_ERROR: "Voice recognition error. Please try again."
        DELETE_ACCOUNT: "Delete the account",
        DELETE_ACCOUNT_CONFIRMATION: "Are you sure that you want to delete your account?",
        ACCOUNT_DELETED: "Account has been deleted",
        ACCOUNT_NOT_DELETED: "Unable to delete the account"
    },
    zh: {
        EXPORT: "导出",
        UNDEFINED: "未定义",
        SETTINGS: "设置",
        LANGUAGE: "语言",
        HIGH_CONTRAST_MODE: "高专注模式",
        TURN_ON: "打开",
        TURN_OFF: "关闭",
        LOG_IN: "登录",
        LOG_OUT: "登出",
        REGISTER: "注册",
        CLOSE: "关闭",
        NEW_CONVERSATION: "新对话",
        I_NEED_HELP_WITH_CHOOSING_A_COURSE: "我在选择课程方面需要帮助",
        I_NEED_HELP_WITH_PLATFORM: "我需要 IBM SkillsBuild 平台方面的帮助",
        I_HAVE_QUESTIONS_ABOUT_UNI_LIFE: "我有关于大学生活的问题",
        USER: "用户",
        AI: "AI",
        TYPE_YOUR_MESSAGE: "输入您想说的话...",
        SEND: "发送",
        WELCOME_TO_WATSONX_AI: "欢迎使用 Watsonx AI！",
        SELECT_INITIAL_TOPIC: "请从以下主题中选择一个：",
        USERNAME: "用户名",
        PASSWORD: "密码",
        CONFIRM_PASSWORD: "确认密码",
        DONT_HAVE_AN_ACCOUNT: "没有账号？",
        ALREADY_HAVE_AN_ACCOUNT: "已经有账户了？",
        PLEASE_ACCEPT_COOKIES: "请先接受我们的 cookie 条款",
        PASSWORDS_DO_NOT_MATCH: "密码不匹配！",
        REGISTRATION_ERROR: "尝试注册时发生错误，请稍后再试！",
        REGISTRATION_SUCCESS: "注册成功！请登录。",
        REGISTRATION_FAILED: "注册失败！",
        LOGIN_FAILED: "登录失败！",
        COOKIE_DISCLAIMER: "Watsonx AI 使用 cookie。如果您继续使用本网站，即表示您同意将其存储在您的浏览器中。",
        I_UNDERSTAND: "我了解",
        PLEASE_ENTER_A_MESSAGE: "请输入信息！",
        FAILED_TO_SEND_MESSAGE: "发送信息失败！请重试。",
        SPEECH_RECOGNITION_NOT_SUPPORTED: "您的浏览器不支持语音识别功能。",
        START_VOICE_INPUT: "开始语音输入",
        STOP_VOICE_INPUT: "停止语音输入",
        LISTENING: "正在聆听...",
        VOICE_RECOGNITION_ERROR: "语音识别出错，请重试。"
        DELETE_ACCOUNT: "删除账户",
        DELETE_ACCOUNT_CONFIRMATION: "您确定要删除您的账户吗？",
        ACCOUNT_DELETED: "账户已删除",
        ACCOUNT_NOT_DELETED: "无法删除账户",
    },
    ru: {
        EXPORT: "экспорт",
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
        I_NEED_HELP_WITH_PLATFORM: "Мне нужна помощь с платформой IBM SkillsBuild",
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
        FAILED_TO_SEND_MESSAGE: "Не удалось отправить сообщение. Пожалуйста, попробуйте ещё раз.",
        SPEECH_RECOGNITION_NOT_SUPPORTED: "Ваш браузер не поддерживает распознавание речи.",
        START_VOICE_INPUT: "Начать голосовой ввод",
        STOP_VOICE_INPUT: "Остановить голосовой ввод",
        LISTENING: "Слушаю...",
        VOICE_RECOGNITION_ERROR: "Ошибка распознавания речи. Пожалуйста, попробуйте еще раз."
        DELETE_ACCOUNT: "Удалить аккаунт",
        DELETE_ACCOUNT_CONFIRMATION: "Вы уверены, что хотите удалить ваш аккаунт?",
        ACCOUNT_DELETED: "Аккаунт удалён",
        ACCOUNT_NOT_DELETED: "Не удалось удалить аккаунт",
    }
};

// Export with a fallback function
const getTranslation = (langCode = "en", phraseCode = "UNDEFINED") =>
    translations[langCode][phraseCode] || translations["en"][phraseCode] || translations["en"]["UNDEFINED"];

export { translations, getTranslation };
