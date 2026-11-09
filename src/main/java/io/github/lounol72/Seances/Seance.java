package io.github.lounol72.Seances;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Seance {
  private String nom;
  private List<Exercice> exercices;
  private Duration duree;

  public Seance(String nom) {
    this(nom, Duration.ZERO);
  }

  public Seance(String nom, Duration duree) {
    this.nom = nom;
    this.duree = duree;
    this.exercices = new ArrayList<Exercice>();
  }

  public Boolean ajouterExercice(Exercice exo) {
    try {
      exercices.addLast(exo);
      return true;
    } catch (Exception e) {
      System.err.println("Ajoute de l'exercice <" + exo.getNom() + "> est un échec :\n" + e);
      return false;
    }
  }

  public void afficherSeance() {
    System.out.println("Seance <" + this.nom + "> : \n");
    System.out.printf(
        " Durée : %d h %02d min %02d s%n",
        duree.toHours(), duree.toMinutesPart(), duree.toSecondsPart());
    for (Exercice exercice : exercices) {
      exercice.afficher();
      System.out.println("\n");
    }
  }

  /**
   * Liste des exercices, en lecture seule.
   *
   * @return les exercices dans l'ordre de la séance
   */
  public List<Exercice> getExercices() {
    return Collections.unmodifiableList(exercices);
  }

  public void setDuree(Duration duree) {
    this.duree = duree;
  }

  public Duration getDuree() {
    return duree;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }
}
