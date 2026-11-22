#!/usr/bin/env bash
# Serve the HTML reports over http://127.0.0.1 and open them in the browser.
# (Opening them as file:// breaks styling and links in sandboxed browsers such as Flatpak/Snap.)
set -u
cd "$(dirname "$0")/build/reports" || { echo "No reports yet - run ./gradlew test first."; exit 1; }

PORT=${PORT:-8765}
while python3 -c "import socket,sys; s=socket.socket(); sys.exit(0 if s.connect_ex(('127.0.0.1',$PORT))==0 else 1)"; do
  PORT=$((PORT+1))
done

python3 -m http.server "$PORT" --bind 127.0.0.1 >/dev/null 2>&1 &
SERVER_PID=$!
trap 'kill $SERVER_PID 2>/dev/null' EXIT
sleep 1

BASE="http://127.0.0.1:$PORT"
echo "Reports are served at $BASE"
for page in tests/test/index.html defect-tests/index.html jacoco/test/html/index.html; do
  [ -f "$page" ] || continue
  echo "  $BASE/$page"
  xdg-open "$BASE/$page" >/dev/null 2>&1 || open "$BASE/$page" >/dev/null 2>&1 || true
  sleep 0.5
done
echo
read -r -p "Press Enter to stop the report server... " _
