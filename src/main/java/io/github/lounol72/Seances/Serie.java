package io.github.lounol72.Seances;

import java.time.LocalTime;

public class Serie {

  Serie(String type, float poids, int repetition) {
    this.type = TypeSerie.depuisLibelle(type);
    this.poids = poids;
    this.repetition = repetition;
    this.distance = 0;
    this.secondes = "0";
    this.rpe = 0;
    this.note = "";
  }

  Serie(String type, float poids, int repetition, float distance, String seconds, int rpe, String note) {
    this.type = TypeSerie.depuisLibelle(type);
    this.poids = poids;
    this.repetition = repetition;
    this.distance = distance;
    this.secondes = seconds;
    this.rpe = rpe;
    this.note = note;
  }

  private TypeSerie type;
  private float poids;
  private int repetition;
  private String note;
  private int rpe;
  private float distance;

  private String secondes;

  private enum TypeSerie {
    ECHAUFFEMENT("echauffement"), CLASSIC("classique"), ECHEC("echec");

    private final String libelle;

    TypeSerie(String libelle) {
      this.libelle = libelle;
    }

    private static TypeSerie depuisLibelle(String libelle) {
      for (TypeSerie t : values()) {
        if (t.libelle.equalsIgnoreCase(libelle))
          return t;
      }
      throw new IllegalArgumentException("Type de serie inconnu :" + libelle);
    }
  }

  public void afficher() {
    System.out.println(" type:" + type.libelle + " poids:" + poids + " repetitions:" + repetition + " distance:" + distance + " temps:" + LocalTime.ofSecondOfDay(Long.valueOf(secondes)) + " rpe:" + rpe + " notes:" + note);
  }

  // Getters and setters

  public String getType() {
    return this.type.libelle;
  }

  public void setType(String type) {
    this.type = TypeSerie.depuisLibelle(type);
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public String getSecondes() {
    return secondes;
  }

  public void setSecondes(String secondes) {
    this.secondes = secondes;
  }

  public int getRpe() {
    return rpe;
  }

  public void setRpe(int rpe) {
    this.rpe = rpe;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public int getRepetition() {
    return repetition;
  }

  public void setRepetition(int repetition) {
    this.repetition = repetition;
  }

  public float getPoids() {
    return poids;
  }

  public void setPoids(float poids) {
    this.poids = poids;
  }

}
