#!/usr/bin/env bash
# SWE 472 - live demo script (Student 3). Requires JDK 17+.
set -u
cd "$(dirname "$0")"
echo "=== 1) Main suite: ISP + graph coverage + supplementary tests (with JaCoCo) ==="
./gradlew clean test --console=plain
echo
echo "=== 2) Known-defect tests (these are EXPECTED to fail) ==="
./gradlew defectTest --console=plain || true
echo
echo "Reports:"
echo "  Tests   : build/reports/tests/test/index.html"
echo "  Defects : build/reports/defect-tests/index.html"
echo "  Coverage: build/reports/jacoco/test/html/index.html"
