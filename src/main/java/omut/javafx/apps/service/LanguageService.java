package omut.javafx.apps.service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import omut.javafx.apps.model.Language;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class LanguageService {

    private final ObjectProperty<Language> currentLanguage = new SimpleObjectProperty<>();

    @PostConstruct
    public void init() {
        Locale defaultLocale = Locale.getDefault();
        currentLanguage.set(Language.fromLocale(defaultLocale));
    }

    public Language getCurrentLanguage() {
        return currentLanguage.get();
    }

    public void setCurrentLanguage(Language language) {
        currentLanguage.set(language);
    }

    public ObjectProperty<Language> currentLanguageProperty() {
        return currentLanguage;
    }

    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("i18n.messages", getCurrentLanguage().getLocale());
    }
}
