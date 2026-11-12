#!/bin/bash
# Klient Minecraftu zobrazený přes WSLg (okno přímo na ploše Windows).
# Displej, správu oken, myš i zvuk dodává WSLg, takže tu nespouštíme
# žádný vlastní X server ani VNC.

export DISPLAY=:0                         # X server poskytovaný WSLg
export LIBGL_ALWAYS_SOFTWARE=1            # softwarové OpenGL (spolehlivé)
export MESA_GL_VERSION_OVERRIDE=4.5       # Minecraft vyžaduje OpenGL >= 3.3
export MESA_GLSL_VERSION_OVERRIDE=450

# Kontrola, že WSLg opravdu je k dispozici, aby chyba byla srozumitelná.
if [ ! -e /tmp/.X11-unix/X0 ]; then
  echo "CHYBA: nenalezen displej WSLg (/tmp/.X11-unix/X0)."
  echo "Zkontroluj Windows 11 / 'wsl --update', Docker Desktop s WSL2"
  echo "a volumes v docker-compose.yml."
  exit 1
fi

cd /project
sed -i 's/\r$//' gradlew      # odstraní Windows konce řádků
chmod +x gradlew              # zajistí spustitelnost
./gradlew runClient