# SWE 472 – Test Automation (Student 3)

Function under test: `Teller.checksOutArticlesFrom(ShoppingCart)` (which calls `ShoppingCart.handleOffers`).
Framework: JUnit 5.8 on Gradle 9.4.1 (wrapper), JDK 21. Coverage: JaCoCo 0.8.12.

## Run
```
./run-tests.sh                 # everything, for the live demo
./gradlew test                 # main suite + JaCoCo report
./gradlew defectTest           # defect tests (expected to FAIL)
```

## Test classes (src/test/java/dojo/supermarket/model)
| Class | Contents | Traces to |
|---|---|---|
| `IspCheckoutTest` | 13 parameterised cases ISP-01 … ISP-13 | Report §4 ISP table |
| `GraphCoverageTest` | GC-01 … GC-10, one method per path | Report §5.3 Table 5.3 (P1–P10) |
| `SupplementaryCheckoutTest` | SUP-01 cart aggregation | Program Overview |
| `KnownDefectsTest` (`@Tag("defect")`) | DEF-01, DEF-02 | Observations and Results |
| `CheckoutFixture` | shared fixture: product at €3.00, offer tokens | – |
| `SupermarketTest` | original kata test (unchanged) | – |

Oracles: `getTotalPrice()`, `getDiscounts().size()`, line total = qty × €3.00, discount
description / product / sign / amount (description identifies which definition d1–d5 of
`discount` reached its use at N18).

## Results (latest run)
* Main suite: **25 / 25 passed** (13 ISP + 10 GC + 1 SUP + 1 original).
* Defect suite: **2 / 2 fail as expected** (DEF-01, DEF-02).
* JaCoCo, full suite:
  * `handleOffers`: instructions 192/192, lines 31/31, branches **24/24 (100%)**
  * `checksOutArticlesFrom`: instructions 48/48, lines 11/11, branches 2/2 (100%)
* JaCoCo, subsets (confirms Student 2's tool note):
  * NC set {GC-03,05,07,09}: lines 31/31 (100%), branches 19/24
  * EC set {GC-02,03,05,06,07,09}: lines 31/31, branches 22/24. The 2 missed are
    clause-level branches at lines 60 and 67 (`ThreeForTwo && q>2` with q≤2;
    `FiveForAmount && q>=5` with q<5), which JaCoCo counts separately. They are covered by
    GC-04 and GC-08.
  * ISP tests alone: branches 24/24.

## Defects
* **DEF-01** – 10% offer with quantity 0 adds a €0.00 "10.0% off" line. Root cause: no
  quantity guard at ShoppingCart line 64 (N13→N14). Faulty logic.
* **DEF-02** – 3.5 kg under 3-for-2 at €3.00/kg costs €6.00 instead of €7.50: the 0.5 kg is
  given away. Root cause: `(int) quantity` at line 42 drops the fractional part, and the
  discount formula subtracts it. Faulty logic plus an ISP gap (only whole units were modelled).

Evidence: `evidence/` (logs, JaCoCo CSV, screenshots).
