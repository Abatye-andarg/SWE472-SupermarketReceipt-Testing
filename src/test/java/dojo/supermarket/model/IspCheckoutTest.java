package dojo.supermarket.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static dojo.supermarket.model.CheckoutFixture.EPS;
import static dojo.supermarket.model.CheckoutFixture.UNIT_PRICE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Part 2 - Input Space Partitioning tests (Report Section 4, Table "Test case design").
 *
 * Criterion: Each Choice Coverage (ECC) over the characteristics
 *   Q (quantity vs. thresholds), G (grouping in threes), M (pricing mechanism), T (min quantity),
 * extended with below / at / above values around every bundle threshold.
 *
 * Each CSV row is one ISP test case; the row's first column is its Test ID so that
 * the JUnit report traces directly back to the ISP table.
 */
@DisplayName("Part 2 - ISP test cases (Teller.checksOutArticlesFrom)")
class IspCheckoutTest {

    @ParameterizedTest(name = "{0}: qty={1}, offer={2} -> total EUR {3}, discounts={4} [{6}]")
    @CsvSource({
        // id,     qty, offer,                 total, discounts, description, blocks covered
        "ISP-01,   0,   NONE,                  0.00,  0,         '',          'Q1 G1 M1 T1'",
        "ISP-02,   3,   NONE,                  9.00,  0,         '',          'Q4 G2 M1 T1'",
        "ISP-03,   2,   THREE_FOR_TWO,         6.00,  0,         '',          'Q3 G3 M2 T4 (below)'",
        "ISP-04,   3,   THREE_FOR_TWO,         6.00,  1,         '3 for 2',   'Q4 G2 M2 T4 (at)'",
        "ISP-05,   4,   THREE_FOR_TWO,         9.00,  1,         '3 for 2',   'Q5 G3 M2 T4 (above)'",
        "ISP-06,   6,   THREE_FOR_TWO,        12.00,  1,         '3 for 2',   'Q7 G2 M2 T4 (two bundles)'",
        "ISP-07,   1,   TWO_FOR_AMOUNT:5.00,   3.00,  0,         '',          'Q2 G3 M3 T3 (below)'",
        "ISP-08,   2,   TWO_FOR_AMOUNT:5.00,   5.00,  1,         '2 for 5.0', 'Q3 G3 M3 T3 (at)'",
        "ISP-09,   3,   TWO_FOR_AMOUNT:5.00,   8.00,  1,         '2 for 5.0', 'Q4 G2 M3 T3 (above)'",
        "ISP-10,   4,   FIVE_FOR_AMOUNT:11.00, 12.00, 0,         '',          'Q5 G3 M3 T5 (below)'",
        "ISP-11,   5,   FIVE_FOR_AMOUNT:11.00, 11.00, 1,         '5 for 11.0','Q6 G3 M3 T5 (at)'",
        "ISP-12,   6,   FIVE_FOR_AMOUNT:11.00, 14.00, 1,         '5 for 11.0','Q7 G2 M3 T5 (above)'",
        "ISP-13,   1,   TEN_PERCENT:10,        2.70,  1,         '10.0% off', 'Q2 G3 M4 T2'",
    })
    void ispTestCase(String id, double qty, String offer, double expectedTotal,
                     int expectedDiscounts, String expectedDescription, String blocks) {
        CheckoutFixture f = new CheckoutFixture().withOffer(offer).withApples(qty);

        Receipt receipt = f.checkout();

        assertAll(id,
            // Oracle 1: final amount due
            () -> assertEquals(expectedTotal, receipt.getTotalPrice(), EPS, id + " total"),
            // Oracle 2: number of discount lines on the receipt
            () -> assertEquals(expectedDiscounts, receipt.getDiscounts().size(), id + " discount entries"),
            // Oracle 3: the cart line is always priced at the regular price (qty x unit price)
            () -> assertEquals(1, receipt.getItems().size(), id + " receipt lines"),
            () -> assertEquals(qty * UNIT_PRICE, receipt.getItems().get(0).getTotalPrice(), EPS, id + " line total"),
            // Oracle 4: when a discount applies, it is the right kind, for the right product, and negative
            () -> {
                if (expectedDiscounts == 1) {
                    Discount d = receipt.getDiscounts().get(0);
                    assertEquals(expectedDescription, d.getDescription(), id + " discount description");
                    assertEquals(f.apple, d.getProduct(), id + " discount product");
                    assertTrue(d.getDiscountAmount() < 0, id + " discount must be negative");
                    assertEquals(expectedTotal - qty * UNIT_PRICE, d.getDiscountAmount(), EPS, id + " discount amount");
                }
            });
    }
}
