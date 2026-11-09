package io.github.lounol72.Seances;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Importe l'export CSV de Liftoff et le transforme en séances. */
public class Importer {

  private static final double KG_PAR_LIVRE = 0.45359237;
  private static final Pattern DUREE =
      Pattern.compile("(\\d+) hours (\\d+) minutes (\\d+) seconds");

  // Colonnes de l'export Liftoff
  private static final int DATE = 0;
  private static final int DUREE_SEANCE = 1;
  private static final int EXERCICE = 3;
  private static final int POIDS = 5;
  private static final int REPS = 6;
  private static final int DISTANCE = 7;
  private static final int SECONDES = 8;
  private static final int RPE = 9;
  private static final int NOTES = 10;

  private Importer() {}

  /**
   * Lit un export Liftoff et regroupe ses lignes (une par série) en séances.
   *
   * <p>Une séance est identifiée par sa date. Dans une séance, toutes les séries d'un même
   * exercice sont regroupées dans un seul {@link Exercice}, même si l'exercice apparaît en
   * plusieurs morceaux (superset).
   *
   * @param fic chemin du fichier CSV
   * @return les séances, de la plus ancienne à la plus récente
   * @throws IOException si le fichier est illisible
   */
  public static List<Seance> importerSeances(String fic) throws IOException {
    List<String[]> lignes;
    try (BufferedReader br = Files.newBufferedReader(Path.of(fic), StandardCharsets.UTF_8)) {
      lignes = lireCsv(br);
    }
    if (lignes.isEmpty()) {
      return new ArrayList<>();
    }
    lignes.remove(0); // en-tête

    List<Seance> seances = new ArrayList<>();
    Seance seance = null;
    Map<String, Exercice> exercices = new LinkedHashMap<>();

    for (String[] ligne : lignes) {
      if (seance == null || !seance.getNom().equals(ligne[DATE])) {
        terminer(seance, exercices, seances);
        seance = new Seance(ligne[DATE], lireDuree(ligne[DUREE_SEANCE]));
        exercices = new LinkedHashMap<>();
      }
      exercices.computeIfAbsent(ligne[EXERCICE], Exercice::new).ajouterSerie(creerSerie(ligne));
    }
    terminer(seance, exercices, seances);

    // Le format "yyyy-MM-dd HH:mm:ss" se trie correctement comme du texte
    seances.sort(Comparator.comparing(Seance::getNom));
    return seances;
  }

  /** Ajoute les exercices à la séance, puis la séance à la liste. */
  private static void terminer(
      Seance seance, Map<String, Exercice> exercices, List<Seance> seances) {
    if (seance == null) {
      return;
    }
    exercices.values().forEach(seance::ajouterExercice);
    seances.add(seance);
  }

  private static Serie creerSerie(String[] ligne) {
    String rpe = champ(ligne, RPE);
    return new Serie(
        "classique",
        livresEnKilos(champ(ligne, POIDS)),
        Integer.parseInt(champ(ligne, REPS)),
        Float.parseFloat(champ(ligne, DISTANCE)),
        champ(ligne, SECONDES),
        rpe.isEmpty() ? 0 : Integer.parseInt(rpe),
        champ(ligne, NOTES));
  }

  /** Renvoie la colonne demandée, ou une chaîne vide si la ligne est trop courte. */
  private static String champ(String[] ligne, int index) {
    return index < ligne.length ? ligne[index].trim() : "";
  }

  /** Lit une durée Liftoff, par exemple {@code 01 hours 10 minutes 00 seconds}. */
  static Duration lireDuree(String texte) {
    Matcher m = DUREE.matcher(texte.trim());
    if (!m.matches()) {
      throw new IllegalArgumentException("Durée illisible : " + texte);
    }
    return Duration.ofHours(Long.parseLong(m.group(1)))
        .plusMinutes(Long.parseLong(m.group(2)))
        .plusSeconds(Long.parseLong(m.group(3)));
  }

  /** Convertit un poids en livres en kilos, arrondi au centième. */
  static float livresEnKilos(String livres) {
    double kilos = Double.parseDouble(livres) * KG_PAR_LIVRE;
    return (float) (Math.round(kilos * 100) / 100.0);
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
}
