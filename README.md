🍲 CookSmarter
CookSmarter ist eine JavaFX-basierte Desktop-App zur einfachen Erstellung und Bearbeitung von Rezepten mit automatischer Berechnung der Nährwerte. Die App richtet sich an Menschen, die ihre Ernährung gezielt planen möchten – etwa zum Kalorienzählen oder für Fitnessziele – und bietet dabei eine flexible, editierbare Zutatenverwaltung.

✨ Features
📥 Zutaten aus einer öffentlichen Lebensmitteldatenbank importieren

🧮 Nährwerte einzelner Zutaten einsehen und editieren

🍽️ Eigene Rezepte aus vorhandenen Zutaten erstellen

🔄 Dynamisches Hinzufügen/Entfernen von Zutaten in Rezepten

💾 Lokale Speicherung von Zutaten und Rezepten im JSON-Format

🎯 Motivation
Die Idee entstand, als meine Schwester begann, Kalorien zu zählen und ein Tool suchte, mit dem sie bei Rezepten flexibel experimentieren und dabei die Nährwerte stets im Blick behalten konnte.
Zudem nutzte ich das Projekt, um meine Kenntnisse in JavaFX, im Umgang mit JSON-Dateien, sowie dem Arbeiten mit öffentlichen APIs zu vertiefen.

⚙️ Installation & Ausführung
Dieses Projekt ist ein Java Maven-Projekt. Du kannst es mit IntelliJ IDEA oder jeder anderen Maven-kompatiblen IDE öffnen und direkt starten.

Voraussetzungen
Java 17 oder neuer

Maven

Build & Run
bash
Kopieren
Bearbeiten
git clone https://github.com/mbeck87/CookSmarter.git
cd CookSmarter
mvn clean install
Du kannst die App aus der IDE starten oder per Maven-Befehl:

bash
Kopieren
Bearbeiten
mvn javafx:run
🚀 Nutzung
Zutaten importieren
→ Durchsuche eine öffentliche Nährwert-Datenbank und importiere Zutaten lokal.

Zutaten verwalten
→ Bearbeite Nährwertangaben, Namen oder Einheiten vorhandener Zutaten.

Rezept erstellen
→ Kombiniere Zutaten, passe Mengen an, und lasse die Gesamt-Nährwerte automatisch berechnen.

Rezepte einsehen & bearbeiten
→ Verwalte deine gespeicherten Rezepte einfach über die GUI.

🗂️ Projektstruktur
css
Kopieren
Bearbeiten
src/
├── main/
│   ├── java/
│   │   └── jixo.cook.controller/     → JavaFX Controller (FXML-Logik)
│   │   └── jixo.cook.scripts/        → Hauptklassen, Datenmodelle, Hilfsmethoden
│   ├── resources/
│       └── jixo.cool.fxml/           → GUI-Definitionen (FXML-Dateien)
│       └── jixo.cool.images/         → Grafiken und Icons

🧪 Tests
Derzeit sind keine automatisierten Tests integriert. Geplant ist die Erweiterung um Unit-Tests für die Logikkomponenten in zukünftigen Releases.

🧩 Mitwirken
Aktuell wird das Projekt ausschließlich von mir gepflegt.

📜 Lizenz
Dieses Projekt ist Open Source unter der MIT License.

👤 Entwickler
@mbeck87 – Entwicklung, Idee, Wartung
GitHub: https://github.com/mbeck87
