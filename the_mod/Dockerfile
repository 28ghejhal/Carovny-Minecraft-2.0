# =====================================================================
# Dockerfile pro Minecraft mód (Java 17 => Minecraft 1.18 - 1.20.4)
#
# Používá "multi-stage build": několik oddělených fází (stages) v jednom
# souboru. Každá fáze má jiný účel a výsledný image obsahuje jen to, co
# je potřeba. Konkrétní fázi lze vybrat přes `--target` (nebo v compose
# přes `build.target`).
#
# Fáze:
#   1. build  - sestaví mód pomocí Gradle (jen dočasná, do výsledku nejde)
#   2. client - prostředí pro Minecraft klienta (volitelné)
#   3. server - Minecraft server s tvým módem (výchozí, protože je poslední)
# =====================================================================


# ---------------------------------------------------------------------
# 1. FÁZE: BUILD MÓDU
# ---------------------------------------------------------------------

# Základ: image s Gradlem 8 a JDK 17.
# Potřebujeme JDK (ne jen JRE), protože se kompiluje kód. Gradle je
# součástí image, proto není potřeba gradlew (Gradle wrapper).
FROM gradle:8-jdk17 AS build

# Image "gradle" běží běžně pod uživatelem "gradle", který nemusí mít
# právo zapisovat do /app. Pod rootem build proběhne bez problémů s
# oprávněním.
USER root

# Pevné umístění Gradle cache. Odtud ji bere i --mount=type=cache níže.
ENV GRADLE_USER_HOME=/root/.gradle

# Pracovní složka uvnitř kontejneru. Všechny další příkazy se spouští tady.
WORKDIR /app

# Nejdřív kopírujeme jen konfigurační soubory buildu. Docker ukládá každý
# krok do cache, takže pokud se tyto soubory nezměnily, použije se
# předchozí výsledek. Kdybys používal složku gradle/ (např. gradle/libs.versions.toml),
# přidej řádek: COPY gradle ./gradle
COPY build.gradle settings.gradle gradle.properties ./

# Zdrojový kód kopírujeme až poté. Změna v kódu tak neznehodnotí cache
# předchozích kroků (a ty se nemusí provádět znovu).
COPY src ./src

# Samotný build módu.
#  --mount=type=cache  uloží ~/.gradle (stažený Minecraft, mappingy,
#                      knihovny, stovky MB) mezi buildy, takže se
#                      nestahují znovu. Vyžaduje BuildKit (dnes výchozí).
#  --no-daemon         Gradle daemon v kontejneru nemá smysl, běží jednou
#                      a kontejner končí.
#  -x test             přeskočí testy (zrychlí build; pokud je chceš
#                      spouštět, tuto volbu odstraň).
RUN --mount=type=cache,target=/root/.gradle \
    gradle build --no-daemon -x test

# Vybereme jen hlavní jar. Gradle vedle něj vytváří i další soubory
# (-sources, -dev, -javadoc). Kdyby se dostaly do /mods, server by je
# zkusil načíst jako mód a skončil by chybou.
RUN mkdir /out && \
    cp build/libs/*.jar /out/ && \
    rm -f /out/*-sources.jar /out/*-dev.jar /out/*-javadoc.jar


# ---------------------------------------------------------------------
# 2. FÁZE: KLIENT (volitelná, spouští se jen když chceš hru vidět)
# ---------------------------------------------------------------------

# Základ: JDK 17 na Ubuntu (Jammy). Klient se spouští přes `gradlew runClient`,
# takže potřebuje JDK. Ubuntu zvolené kvůli dostupnosti grafických balíčků.
FROM eclipse-temurin:17-jdk-jammy AS client

# Okno hry se zobrazuje přímo na ploše Windows přes WSLg, který dodává
# displej (X server), správu oken, myš i zvuk. V kontejneru proto NENÍ
# potřeba Xvfb, VNC ani noVNC, stačí knihovny, které Minecraft potřebuje
# k vytvoření okna a vykreslování:
#   libgl1-mesa-dri, libglx-mesa0, libgl1
#                      OpenGL (Mesa). Ve výchozím stavu softwarový render,
#                      volitelně lze použít GPU (viz docker-compose.yml)
#   libopenal1         zvuk (bez něj Minecraft někdy spadne při startu)
#   libpulse0          OpenAL přes něj posílá zvuk do WSLg (PulseServer)
#   libx*              X11 knihovny, na které se dynamicky linkuje
#                      nativní část LWJGL (okno, myš, klávesnice)
# --no-install-recommends vynechá nepovinné balíčky (menší image).
# Smazání /var/lib/apt/lists/* odstraní cache balíčků (menší image).
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1-mesa-dri libglx-mesa0 libgl1 libopenal1 libpulse0 \
    libxrandr2 libxxf86vm1 libxcursor1 libxi6 libxext6 libxrender1 \
    && rm -rf /var/lib/apt/lists/*

# Sem se přes volume připojí tvůj projekt (viz docker-compose.yml).
WORKDIR /project

# Příkaz pro start klienta se zadává v docker-compose.yml
# (command: /project/docker/start-client.sh).


# ---------------------------------------------------------------------
# 3. FÁZE: SERVER S TVÝM MÓDEM (výchozí, protože je poslední)
# ---------------------------------------------------------------------

# Základ: hotový image Minecraft serveru s Javou 17. Při prvním startu si
# sám stáhne Minecraft server a loader (Forge/Fabric) podle proměnných
# TYPE a VERSION, takže potřebuje internet. Má vlastní ENTRYPOINT, proto
# ho nepřepisujeme.
FROM itzg/minecraft-server:java17 AS server

# Z fáze "build" přenese jen hotový jar módu. Všechno ostatní (Gradle,
# zdrojáky, cache) zůstane ve fázi build a do výsledného image se
# nedostane. Image itzg při startu zkopíruje obsah /mods do /data/mods.
COPY --from=build /out/*.jar /mods/

# Výchozí nastavení serveru (lze přepsat v docker-compose.yml):
#  EULA=TRUE         souhlas s licencí Minecraftu; bez něj se server
#                    odmítne spustit
#  ONLINE_MODE=FALSE vypne ověřování účtů u Mojangu, aby se mohl připojit
#                    testovací klient (jméno Player123) bez platného účtu
#  MEMORY=2G         paměť pro Javu
# TYPE a VERSION se záměrně nastavují v docker-compose.yml, protože
# závisí na tom, pro co mód stavíš.
ENV EULA=TRUE \
    ONLINE_MODE=FALSE \
    MEMORY=2G

# Dokumentuje port Minecraft serveru. Sám port nezpřístupní, to dělá
# `ports:` v docker-compose.yml.
EXPOSE 25565