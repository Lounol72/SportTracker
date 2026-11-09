package io.github.lounol72;

import io.github.lounol72.Seances.Importer;
import io.github.lounol72.Seances.Seance;
import java.util.List;

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
    try {
      List<Seance> seances = Importer.importerSeances(ASSET_FOLDER + "liftoff_workout_data.csv");
      for (Seance s : seances) {
        s.afficherSeance();
      }
      System.out.println(seances.size() + " séances importées");
    } catch (Exception e) {
      System.err.println(" Import du fichier impossible : " + e);
    }
  }
}
