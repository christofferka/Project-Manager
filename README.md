# Project Manager

Et værktøj til at planlægge projekter, holde styr på timer og regne budgettet ud.
Du opretter et projekt, deler det op i delprojekter, laver opgaver under hver af dem,
og tilføjer omkostninger som licenser eller konsulenttimer. Appen samler det hele
og viser dig status og pris på én side.

## Hvad kan appen

* Oprette projekter med kunde, startdato, slutdato og timepris
* Opdele projekter i delprojekter og opgaver (og delopgaver under opgaverne)
* Tildele opgaver til personer og sætte deadlines
* Registrere både estimerede og faktiske timer, så man kan se afvigelser
* Tilføje omkostninger (licenser, hardware, konsulenter) der lægges oveni timerne
* Vise fremdrift visuelt med farvede progress bars og en roadmap-side
* Give brugere forskellige roller: Administrator, Editor eller Viewer

## Kør det selv (demo)

Du behøver ikke en database. Appen kan starte i et demo mode der bruger en
in-memory database (H2) og auto-logger dig ind som admin. Perfekt til at
teste eller tage screenshots.

Åbn en terminal i projektmappen og skriv:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Åbn så [http://localhost:8080](http://localhost:8080) i din browser.
Du er logget ind automatisk, så du kan klikke rundt på alle sider med det samme.

Hvis du hellere vil logge ind manuelt, kan du bruge:

* Brugernavn: `demo`
* Kodeord: `demo`

Der ligger også tre andre brugere (`anna`, `bo`, `clara`) med samme kodeord.

### Kører du fra IntelliJ?

Så skal du sætte profilen i din Run Configuration:

1. Åbn **Edit Configurations**
2. Find feltet **Active profiles** og skriv `demo`
3. Start appen med den grønne pil

## Teknologier

* Java 21
* Spring Boot 3.5 (MVC)
* Thymeleaf til HTML-skabeloner
* JDBC og JdbcTemplate til databasekald
* H2 til demo mode, MySQL til produktion
* Vanilla CSS (ingen frameworks)

## Mappestruktur, kort

```
src/main/java/com/example/projectcalctool/
  controller/   Håndterer URL'er og kalder services
  service/      Forretningslogik (beregninger, regler)
  repository/   SQL mod databasen
  model/        Dataklasser (Project, Task osv.)
  config/       Spring-opsætning + DemoAuthFilter

src/main/resources/
  templates/    Thymeleaf HTML-filer
  static/css/   Design (app.css)
  sql/          SQL-filer til at oprette tabeller og testdata
```

## Historie

Projektet startede som en gruppeopgave på 2. semester af datamatiker.
Oprindeligt lå det på et andet repo sammen med de andre i gruppen.
Senere er den her version blevet bygget om med nyt design, bedre UX,
og demo-mode til at vise det frem i min portfolio.
