package omut.javafx.apps.model;

import java.util.Locale;

public enum Language {
    RUSSIAN("Русский", new Locale("ru")),
    ENGLISH("English", Locale.ENGLISH),
    FRENCH("Français", Locale.FRENCH),
    GERMAN("Deutsch", Locale.GERMAN),
    SPANISH("Español", new Locale("es")),
    PORTUGUESE("Português", new Locale("pt")),
    KAZAKH("Қазақша", new Locale("kk")),
    ARABIC("العربية", new Locale("ar")),
    JAPANESE("日本語", Locale.JAPANESE),
    KOREAN("한국어", Locale.KOREAN),
    CHINESE("中文", Locale.CHINESE);

    private final String displayName;
    private final Locale locale;

    Language(String displayName, Locale locale) {
        this.displayName = displayName;
        this.locale = locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Locale getLocale() {
        return locale;
    }

    public static Language fromLocale(Locale locale) {
        String lang = locale.getLanguage();
        for (Language l : values()) {
            if (l.getLocale().getLanguage().equals(lang)) {
                return l;
            }
        }
        return ENGLISH; // Default
    }
}
