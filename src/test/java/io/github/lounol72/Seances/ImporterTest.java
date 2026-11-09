package io.github.lounol72.Seances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImporterTest {

  private static final String EN_TETE =
      "Date,Durée,Nom de la séance,Nom de l'exercice,Ordre des séries,Poids,Réps,Distance,"
          + "Secondes,RPE,Notes\n";

  @TempDir Path dossier;

  private List<Seance> importer(String contenu) throws IOException {
    Path csv = dossier.resolve("export.csv");
    Files.writeString(csv, EN_TETE + contenu, StandardCharsets.UTF_8);
    return Importer.importerSeances(csv.toString());
  }

  private static int nombreDeSeries(List<Seance> seances) {
    return seances.stream()
        .flatMap(s -> s.getExercices().stream())
        .mapToInt(e -> e.getSeries().size())
        .sum();
  }

  @Test
  void importeToutLExportLiftoff() throws IOException {
    List<Seance> seances = Importer.importerSeances("assets/liftoff_workout_data.csv");

    assertEquals(68, seances.size());
    assertEquals(1386, nombreDeSeries(seances));
    assertEquals("2026-05-20 21:22:07", seances.getFirst().getNom());
    assertEquals("2026-11-05 13:45:42", seances.getLast().getNom());
  }

  @Test
  void garderLaPremiereSerieDeChaqueSeanceEtLaDerniereSeance() throws IOException {
    List<Seance> seances =
        importer(
            """
            2026-06-02 10:00:00,01 hours 00 minutes 00 seconds,,Squat,0,220.462262185,5,0,0,,
            2026-06-02 10:00:00,01 hours 00 minutes 00 seconds,,Squat,1,220.462262185,5,0,0,,
            2026-06-01 10:00:00,00 hours 45 minutes 00 seconds,,Squat,0,110.2311310925,8,0,0,,
            2026-06-01 10:00:00,00 hours 45 minutes 00 seconds,,Tractions,0,0,6,0,0,8,
            """);

    assertEquals(2, seances.size());
    Seance premiere = seances.get(0);
    Seance seconde = seances.get(1);
    assertEquals("2026-06-01 10:00:00", premiere.getNom());
    assertEquals(List.of("Squat", "Tractions"), noms(premiere));
    assertEquals(50f, premiere.getExercices().get(0).getSeries().get(0).getPoids());
    assertEquals(8, premiere.getExercices().get(1).getSeries().get(0).getRpe());
    // le Squat de la séance du 2 n'est pas mélangé avec celui du 1er
    assertEquals(List.of("Squat"), noms(seconde));
    assertEquals(2, seconde.getExercices().get(0).getSeries().size());
    assertEquals(100f, seconde.getExercices().get(0).getSeries().get(0).getPoids());
  }

  @Test
  void regrouperUnExerciceCoupeEnDeux() throws IOException {
    List<Seance> seances =
        importer(
            """
            2026-06-01 10:00:00,00 hours 30 minutes 00 seconds,,Curl,0,0,10,0,0,,
            2026-06-01 10:00:00,00 hours 30 minutes 00 seconds,,Dips,0,0,10,0,0,,
            2026-06-01 10:00:00,00 hours 30 minutes 00 seconds,,Curl,1,0,10,0,0,,
            """);

    assertEquals(List.of("Curl", "Dips"), noms(seances.getFirst()));
    assertEquals(2, seances.getFirst().getExercices().getFirst().getSeries().size());
  }

  @Test
  void lireUneNoteAvecVirgulesEtRetourALaLigne() throws IOException {
    List<Seance> seances =
        importer(
            "2026-06-01 10:00:00,00 hours 30 minutes 00 seconds,,Curl,0,0,10,0,0,,"
                + "\"lent, contrôlé\nbonne séance\"\n");

    assertEquals(
        "lent, contrôlé\nbonne séance",
        seances.getFirst().getExercices().getFirst().getSeries().getFirst().getNote());
  }

  @Test
  void lireUneDureeDePlusDe24Heures() {
    assertEquals(
        Duration.ofHours(24).plusMinutes(37).plusSeconds(36),
        Importer.lireDuree("24 hours 37 minutes 36 seconds"));
    assertThrows(IllegalArgumentException.class, () -> Importer.lireDuree("1h10"));
  }

  @Test
  void convertirLesLivresEnKilos() {
    assertEquals(50f, Importer.livresEnKilos("110.2311310925"));
    assertEquals(0f, Importer.livresEnKilos("0"));
  }

  private static List<String> noms(Seance seance) {
    return seance.getExercices().stream().map(Exercice::getNom).toList();
  }
}
