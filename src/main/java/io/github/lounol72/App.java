package io.github.lounol72;

import io.github.lounol72.Seances.Importer;

/**
 * Class App
 *
 */
public class App {
  private static final String ASSET_FOLDER = "assets/";

  /**
   * Main function for the application
   */
  public static void main(String[] args) {
    System.out.println("Hello World!");
    try {
      Importer.impoterSeance(ASSET_FOLDER + "liftoff_workout_data.csv");
    } catch (Exception e) {
      System.err.println(" Affichage du fichier impossible : " + e);
    }
  }
}
