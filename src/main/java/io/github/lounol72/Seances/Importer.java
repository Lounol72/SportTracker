package io.github.lounol72.Seances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Importer {

  /**
   * Affiche un fichier à partir de son path
   *
   * @param fic String
   * @throws IOException If file not found
   */
  public static Seance impoterSeance(String fic) throws IOException {
    System.out.println(" Fichier " + fic + ":\n");
    BufferedReader br = new BufferedReader(new FileReader(fic));
    List<String[]> seanceTableau = null;
    try {

      seanceTableau = lireCsv(br);
    } catch (Exception e) {
      return null;
    }
    seanceTableau.remove(0); // Enlever la premiere ligne

    System.out.println("Transformation en kg avant affichage");
    for (int i = 0; i < seanceTableau.size(); i++) {
      seanceTableau.get(i)[5] = lbsTokilos(seanceTableau.get(i)[5]);
    }
    System.out.println("Transformation en kg terminée");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H 'hours' m 'minutes' s 'seconds'", Locale.FRANCE);
    Seance seance = new Seance(seanceTableau.get(0)[0], LocalTime.parse(seanceTableau.get(0)[1], formatter));
    Exercice exo = new Exercice(seanceTableau.get(0)[3]);

    for (int i = 0; seance.getNom().equals(seanceTableau.get(i)[0]); i++) {
      if (!exo.getNom().equals(seanceTableau.get(i)[3])) {
        seance.ajouterExercice(exo);
        exo = new Exercice(seanceTableau.get(i)[3]);
      }
      if (exo.getNom().equals(seanceTableau.get(i)[3])) {
        Serie serie_exo = new Serie("classique",
            Float.parseFloat(seanceTableau.get(i)[5]),
            Integer.parseInt(seanceTableau.get(i)[6]), Float.parseFloat(seanceTableau.get(i)[7]),
            seanceTableau.get(i)[8],
            (seanceTableau.get(i).length <= 9) ? -1
                : seanceTableau.get(i)[9].equals("") ? 0 : Integer.parseInt(seanceTableau.get(i)[9]),
            (seanceTableau.get(i).length <= 10) ? "" : seanceTableau.get(i)[10]);
        exo.ajouterSerie(serie_exo);
      }
    }

    seance.ajouterExercice(exo);
    seance.afficherSeance();

    return seance;
  }

  private static List<String[]> lireCsv(BufferedReader br) throws IOException {
    List<String[]> lignes = new ArrayList<>();
    List<String> champs = new ArrayList<>();
    StringBuilder champ = new StringBuilder();
    boolean entreGuillemets = false;
    int c;
    while ((c = br.read()) != -1) {
      char ch = (char) c;
      if (entreGuillemets) {
        if (ch == '"') {
          br.mark(1);
          if (br.read() == '"') {
            champ.append('"'); // "" dans un champ = un guillemet littéral
          } else {
            br.reset();
            entreGuillemets = false; // fin du champ entre guillemets
          }
        } else {
          champ.append(ch); // virgules et retours à la ligne gardés tels quels
        }
      } else if (ch == '"') {
        entreGuillemets = true;
      } else if (ch == ',') {
        champs.add(champ.toString());
        champ.setLength(0);
      } else if (ch == '\n') {
        champs.add(champ.toString());
        champ.setLength(0);
        lignes.add(champs.toArray(new String[0]));
        champs.clear();
      } else if (ch != '\r') {
        champ.append(ch);
      }
    }
    if (champ.length() > 0 || !champs.isEmpty()) { // dernière ligne sans \n final
      champs.add(champ.toString());
      lignes.add(champs.toArray(new String[0]));
    }
    return lignes;
  }

  private static String lbsTokilos(String input) {
    return String.format("%.2f", (Float.parseFloat(input) * 0.453592));
  }
}
