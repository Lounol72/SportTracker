package io.github.lounol72.Seances;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Seance {
  private String nom;
  private List<Exercice> exercices;
  private LocalTime duree;


  public Seance(String nom) {
    this.nom = nom;
    this.exercices = new ArrayList<Exercice>();
  }
  
  public Seance(String nom, LocalTime duree) {
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

  public void afficherSeance(){
    System.out.println("Seance <" + this.nom + "> : \n");
    System.out.println(" Durée : " + duree);
    for (Exercice exercice : exercices) {
      exercice.afficher();
      System.out.println("\n");
    }
  }
  public void setDuree(LocalTime duree) {
    this.duree = duree;
  }
  public LocalTime getDuree() {
    return duree;
  }
  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }
}
