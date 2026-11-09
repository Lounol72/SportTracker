package io.github.lounol72.Seances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Exercice takes a name
 *
 */
public class Exercice {
  private final String nom;
  private List<Serie> series;

  /**
   * Create an instance of Exercice
   *
   * @param nom String : name given for the exercice
   */
  Exercice(String nom) {
    this.nom = nom;
    series = new ArrayList<Serie>();
  }

  /**
   * Getter for the name
   *
   * @return String : name of the Exercice
   */
  public String getNom() {
    return nom;
  }

  /**
   * Liste des séries, en lecture seule.
   *
   * @return les séries dans l'ordre où elles ont été faites
   */
  public List<Serie> getSeries() {
    return Collections.unmodifiableList(series);
  }

  public void afficher() {
    System.out.println(" Exercice <" + nom + "> :");
    for (int i = 0; i < series.size(); i++) {
      System.out.println("Série n°" + (i + 1));
      series.get(i).afficher();
    }
  }

  public Boolean ajouterSerie(Serie s) {
    try {
      series.addLast(s);
    } catch (Exception e) {
      System.err.println("Erreur: Ajout de la série <" + s + " à l'éxercice <" + nom + ">");
      return false;
    }
    return true;
  }

  public Boolean retirerSerie(int index) {
    if (index >= series.size()) {
      System.err.println(" Erreur: L'index '" + index + "' n'existe pas");
      return false;
    }
    series.remove(index);
    return true;
  }
}
