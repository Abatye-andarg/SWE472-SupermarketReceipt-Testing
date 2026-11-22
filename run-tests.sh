#!/usr/bin/env bash
# SWE 472 - live demo script. Requires JDK 17+.
# Usage: ./run-tests.sh            run everything and open the HTML reports in the browser
#        ./run-tests.sh --no-open  run everything, terminal output only
set -u
cd "$(dirname "$0")"

echo "=== 1) Main suite: ISP + graph coverage + supplementary tests (with JaCoCo) ==="
./gradlew clean test --console=plain
echo
echo "=== 2) Known-defect tests (these are EXPECTED to fail) ==="
./gradlew defectTest --console=plain || true

TESTS=build/reports/tests/test/index.html
DEFECTS=build/reports/defect-tests/index.html
COVERAGE=build/reports/jacoco/test/html/index.html
echo
echo "Reports:"
echo "  Tests   : $TESTS"
echo "  Defects : $DEFECTS"
echo "  Coverage: $COVERAGE"

if [ "${1:-}" != "--no-open" ]; then
  echo
  ./show-reports.sh
fi
