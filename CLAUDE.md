# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Projet

SportTracker : application Java (Maven, Java 21, sans dépendance hors JUnit 5) qui lit un export CSV de l'app de musculation Liftoff et doit le modéliser en séances / exercices / séries. Le projet est à un stade très précoce. Identifiants, messages et Javadoc sont majoritairement en français.

## Commandes

```bash
mvn compile                                   # compiler
mvn test                                      # lancer tous les tests
mvn test -Dtest=AppTest                       # une classe de test
mvn test -Dtest=AppTest#shouldAnswerWithTrue  # une méthode de test
mvn compile exec:java                         # exécuter (depuis la racine, à cause du chemin relatif assets/)
```

`exec-maven-plugin` est déclaré dans `<build><plugins>` avec `mainClass` ; il s'exécute aussi pendant la phase `package`. Pas de linter configuré.

## État actuel du code (à connaître avant toute modification)

- Les libellés des types de série sont portés par l'enum privée `type_t` (champ `libelle`). Le constructeur `Serie(String type, …)` appelle `type_t.valueOf`, qui attend le nom de la constante (`"ECHAUFFEMENT"`) et non son libellé. Le CSV n'a pas de colonne pour ce type.
- Dans `Exercice` et `Seance`, les listes (`series`, `exercices`) ne sont jamais initialisées : `ajouter*` lèvent une `NullPointerException`, qui est interceptée et renvoyée sous forme de `false`.
- Le constructeur d'`Exercice` est package-private.
- `AppTest` n'est qu'un test placeholder.

## Architecture

- `io.github.lounol72.App` : point d'entrée ; lit actuellement `assets/liftoff_workout_data.csv` (chemin relatif au répertoire de travail) et l'affiche.
- `io.github.lounol72.Seances` : modèle de domaine, `Seance` → `List<Exercice>` → `List<Serie>`. `Serie` porte le type (échauffement / classique / échec), le poids, les répétitions, la distance, les secondes, le RPE et une note.

## Format des données (`assets/liftoff_workout_data.csv`)

Une ligne correspond à **une série** ; les séances doivent être reconstruites en regroupant les lignes.

En-tête : `Date,Durée,Nom de la séance,Nom de l'exercice,Ordre des séries,Poids,Réps,Distance,Secondes,RPE,Notes`

- `Date` (`yyyy-MM-dd HH:mm:ss`) identifie la séance ; toutes ses lignes partagent la même `Date` et la même `Durée`.
- `Durée` est du texte libre, par exemple `01 hours 10 minutes 00 seconds`.
- `Nom de la séance`, `RPE` et `Notes` sont souvent vides.
- `Ordre des séries` est un index qui commence à 0 dans chaque exercice.
- `Poids` est un flottant non arrondi (par exemple `110.2311310925`, soit exactement 50 kg en livres) ; l'unité est probablement la livre.
