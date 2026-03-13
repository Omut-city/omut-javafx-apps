# omut-javafx-apps
Collection of small JavaFX applications


Build:
mvn clean package

Build installer:
jpackage --name OmutApps --input target --main-jar omut-javafx-apps-1.0.0-jar-with-dependencies.jar --main-class omut.javafx.apps.Launcher --type exe --win-shortcut --win-menu