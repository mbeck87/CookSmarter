# 🍲 CookSmarter

**CookSmarter** ist eine JavaFX-basierte Desktop-App zur einfachen Erstellung und Bearbeitung von Rezepten mit automatischer Berechnung der Nährwerte. Die App richtet sich an Menschen, die ihre Ernährung gezielt planen möchten – etwa zum Kalorienzählen oder für Fitnessziele – und bietet dabei eine flexible, editierbare Zutatenverwaltung.

---

## ✨ Features

- 📥 Zutaten aus einer öffentlichen Lebensmitteldatenbank importieren
- 🧮 Nährwerte einzelner Zutaten einsehen und editieren
- 🍽️ Eigene Rezepte aus vorhandenen Zutaten erstellen
- 🔄 Dynamisches Hinzufügen/Entfernen von Zutaten in Rezepten
- 💾 Lokale Speicherung von Zutaten und Rezepten im JSON-Format

---

## 🎯 Motivation

Die Idee entstand, als meine Schwester begann, Kalorien zu zählen und ein Tool suchte, mit dem sie bei Rezepten flexibel experimentieren und dabei die Nährwerte stets im Blick behalten konnte.  
Zudem nutzte ich das Projekt, um meine Kenntnisse in **JavaFX**, im **Umgang mit JSON-Dateien**, sowie dem Arbeiten mit **öffentlichen APIs** zu vertiefen.

---

## ⚙️ Installation & Ausführung

Dieses Projekt ist ein **Java Maven-Projekt**. Du kannst es mit IntelliJ IDEA oder jeder anderen Maven-kompatiblen IDE öffnen und direkt starten.

### Voraussetzungen

- Java 17 oder neuer
- Maven

### Build & Run

```bash
git clone https://github.com/mbeck87/CookSmarter.git
cd CookSmarter
mvn clean install
