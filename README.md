# 🍲 CookSmarter

**CookSmarter** ist eine JavaFX-basierte Desktop-App zur einfachen Erstellung und Bearbeitung von Rezepten – mit automatischer Berechnung der Nährwerte.

Die App richtet sich an alle, die ihre Ernährung gezielt planen möchten (z. B. für Kalorienzählen oder Fitnessziele) und bietet eine flexible, editierbare Zutatenverwaltung.

---

## ✨ Features

- 📥 **Import** von Zutaten aus einer öffentlichen Lebensmitteldatenbank  
- 🧪 **Nährwerte anzeigen & bearbeiten** für einzelne Zutaten  
- 🍽️ **Eigene Rezepte erstellen** aus vorhandenen Zutaten  
- 🔄 **Dynamisches Hinzufügen/Entfernen** von Zutaten in Rezepten  
- 📂 **Lokale Speicherung** im JSON-Format (Zutaten & Rezepte)  

---

## 🎯 Motivation

Das Projekt entstand, als meine Schwester begann, Kalorien zu zählen und ein Tool suchte, mit dem sie flexibel experimentieren und gleichzeitig Nährwerte im Blick behalten konnte.  

Ich habe es auch genutzt, um meine Kenntnisse in **JavaFX**, im Umgang mit **JSON-Dateien** und öffentlichen **APIs** zu vertiefen.

---

## ⚙️ Installation & Ausführung

> Dieses Projekt ist ein **Java Maven-Projekt**. Du kannst es mit **IntelliJ IDEA** oder einer anderen Maven-kompatiblen IDE öffnen.

### 𞷾 Voraussetzungen

- Java 23
- Maven  

### 💻 Build & Run

```bash
git clone https://github.com/mbeck87/CookSmarter.git
cd CookSmarter
mvn clean install
```

Du kannst die App entweder direkt aus der IDE starten oder per Maven:

```bash
mvn javafx:run
```

---

## 🚀 Nutzung

- **Zutaten importieren**  
  → Durchsuche eine öffentliche Nährwert-Datenbank und importiere Zutaten lokal.

- **Zutaten verwalten**  
  → Bearbeite Nährwertangaben, Namen oder Einheiten vorhandener Zutaten.

- **Rezept erstellen**  
  → Kombiniere Zutaten, passe Mengen an, Gesamt-Nährwerte werden automatisch berechnet.

- **Rezepte einsehen & bearbeiten**  
  → Verwalte deine gespeicherten Rezepte bequem über die GUI.

---

## 📂 Projektstruktur

```
src/main/java/jixo/cook/
├── domain/               → Datenmodelle & Repository-Interfaces
├── application/usecase/  → Use Cases (Business-Logik)
├── infrastructure/       → API, JSON-Repositories, Bildverwaltung, i18n, DI
└── presentation/         → JavaFX Controller & UI-Komponenten

storage/                  → Lokale Datenspeicherung (wird zur Laufzeit erstellt)
├── ingredient/           → Zutaten als JSON-Dateien
├── recipe/               → Rezepte als JSON-Dateien
└── images/               → Heruntergeladene Bilder
```

---

## 🧪 Tests

> Derzeit sind **keine automatisierten Tests** integriert.  
Geplant ist die Erweiterung um Unit-Tests für Logikkomponenten in zukünftigen Releases.

---

## 🧹 Mitwirken

> Aktuell wird das Projekt **allein von mir gepflegt**.  
Pull Requests sind dennoch willkommen!

---

## 📜 Lizenz

Dieses Projekt steht unter der **MIT License**.  
Das bedeutet: freie Nutzung, Veränderung und Weiterverbreitung – bitte mit Verweis auf den/die Autor:in.

---

## 👤 Entwickler

**@mbeck87** – Entwicklung, Idee, Wartung  
👉 [GitHub-Profil](https://github.com/mbeck87)
