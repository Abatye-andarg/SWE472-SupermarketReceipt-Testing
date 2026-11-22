package dojo.supermarket.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dojo.supermarket.model.CheckoutFixture.EPS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Part 3 - Graph coverage tests (Report Section 5.3, Table 5.3 path-to-test mapping).
 *
 * CFGs: Figure 6 (Teller.checksOutArticlesFrom, nodes T1-T4) and
 *       Figure 7 (ShoppingCart.handleOffers, nodes N1-N19, edges e1-e28).
 *
 * Node coverage  : {GC-03, GC-05, GC-07, GC-09}
 * Edge coverage  : {GC-02, GC-03, GC-05, GC-06, GC-07, GC-09}
 * Specified path : all of GC-01 .. GC-10 (8 iteration paths + loop 0/1/2 times)
 * All-Uses (discount): {GC-03, GC-04, GC-05, GC-07, GC-09}
 *
 * Every test enters through the function under test, Teller.checksOutArticlesFrom.
 * Oracles: receipt.getTotalPrice() and receipt.getDiscounts().size(), plus the discount
 * description, which identifies which definition of 'discount' (d1-d5) reached the use at N18.
 */
@DisplayName("Part 3 - Graph coverage test paths (CFG of handleOffers)")
class GraphCoverageTest {

    private static void assertReceipt(String id, Receipt r, double total, int discounts) {
        assertAll(id,
            () -> assertEquals(total, r.getTotalPrice(), EPS, id + " total"),
            () -> assertEquals(discounts, r.getDiscounts().size(), id + " discount entries"));
    }

    @Test
    @DisplayName("GC-01 / P1: 1-2-19 | empty cart, loop 0 times -> EUR 0.00, 0 discounts")
    void gc01_p1_emptyCart_loopZeroIterations() {
        CheckoutFixture f = new CheckoutFixture().withOffer("THREE_FOR_TWO");

        Receipt r = f.checkout();

        assertReceipt("GC-01", r, 0.00, 0);
        assertEquals(0, r.getItems().size(), "GC-01 no receipt lines (T2 false on first test)");
    }

    @Test
    @DisplayName("GC-02 / P2 (=ISP-02): 1-2-3-2-19 | 3 units, no offer -> EUR 9.00, 0 [covers e5]")
    void gc02_p2_productWithoutOffer() {
        Receipt r = new CheckoutFixture().withOffer("NONE").withApples(3).checkout();
        assertReceipt("GC-02", r, 9.00, 0);
    }

    @Test
    @DisplayName("GC-03 / P3 (=ISP-04): ...4-5-9-11-12-13-15-17-18... | 3 units, 3-for-2 -> EUR 6.00, 1 [du d3]")
    void gc03_p3_threeForTwoApplies() {
        Receipt r = new CheckoutFixture().withOffer("THREE_FOR_TWO").withApples(3).checkout();
        assertReceipt("GC-03", r, 6.00, 1);
        assertEquals("3 for 2", r.getDiscounts().get(0).getDescription(), "GC-03 d3 reaches N18");
        assertEquals(-3.00, r.getDiscounts().get(0).getDiscountAmount(), EPS);
    }

    @Test
    @DisplayName("GC-04 / P4 (=ISP-03): ...4-5-9-11-13-15-17-2... | 2 units, 3-for-2 -> EUR 6.00, 0 [du d1, e27]")
    void gc04_p4_threeForTwoBelowThreshold() {
        Receipt r = new CheckoutFixture().withOffer("THREE_FOR_TWO").withApples(2).checkout();
        assertReceipt("GC-04", r, 6.00, 0);
    }

    @Test
    @DisplayName("GC-05 / P5 (=ISP-08): ...4-6-7-8-9-11-13-15-17-18... | 2 units, 2 for EUR 5 -> EUR 5.00, 1 [du d2]")
    void gc05_p5_twoForAmountApplies() {
        Receipt r = new CheckoutFixture().withOffer("TWO_FOR_AMOUNT:5.00").withApples(2).checkout();
        assertReceipt("GC-05", r, 5.00, 1);
        assertEquals("2 for 5.0", r.getDiscounts().get(0).getDescription(), "GC-05 d2 reaches N18");
        assertEquals(-1.00, r.getDiscounts().get(0).getDiscountAmount(), EPS);
    }

    @Test
    @DisplayName("GC-06 / P6 (=ISP-07): ...4-6-7-9-11-13-15-17-2... | 1 unit, 2 for EUR 5 -> EUR 3.00, 0 [covers e12, e27]")
    void gc06_p6_twoForAmountBelowThreshold() {
        Receipt r = new CheckoutFixture().withOffer("TWO_FOR_AMOUNT:5.00").withApples(1).checkout();
        assertReceipt("GC-06", r, 3.00, 0);
    }

    @Test
    @DisplayName("GC-07 / P7 (=ISP-11): ...4-6-9-10-11-13-15-16-17-18... | 5 units, 5 for EUR 11 -> EUR 11.00, 1 [du d5]")
    void gc07_p7_fiveForAmountApplies() {
        Receipt r = new CheckoutFixture().withOffer("FIVE_FOR_AMOUNT:11.00").withApples(5).checkout();
        assertReceipt("GC-07", r, 11.00, 1);
        assertEquals("5 for 11.0", r.getDiscounts().get(0).getDescription(), "GC-07 d5 reaches N18");
        assertEquals(-4.00, r.getDiscounts().get(0).getDiscountAmount(), EPS);
    }

    @Test
    @DisplayName("GC-08 / P8 (=ISP-10): ...4-6-9-10-11-13-15-17-2... | 4 units, 5 for EUR 11 -> EUR 12.00, 0 [du d1]")
    void gc08_p8_fiveForAmountBelowThreshold() {
        Receipt r = new CheckoutFixture().withOffer("FIVE_FOR_AMOUNT:11.00").withApples(4).checkout();
        assertReceipt("GC-08", r, 12.00, 0);
    }

    @Test
    @DisplayName("GC-09 / P9 (=ISP-13): ...4-6-9-11-13-14-15-17-18... | 1 unit, 10% off -> EUR 2.70, 1 [du d4]")
    void gc09_p9_tenPercentDiscount() {
        Receipt r = new CheckoutFixture().withOffer("TEN_PERCENT:10").withApples(1).checkout();
        assertReceipt("GC-09", r, 2.70, 1);
        assertEquals("10.0% off", r.getDiscounts().get(0).getDescription(), "GC-09 d4 reaches N18");
        assertEquals(-0.30, r.getDiscounts().get(0).getDiscountAmount(), EPS);
    }

    @Test
    @DisplayName("GC-10 / P10: loop 2 times | A: 3 units 3-for-2, B: 1 unit no offer -> EUR 9.00, 1")
    void gc10_p10_twoProductsTwoLoopIterations() {
        CheckoutFixture f = new CheckoutFixture().withOffer("THREE_FOR_TWO").withApples(3);
        f.cart.addItemQuantity(f.bread, 1);

        Receipt r = f.checkout();

        // handleOffers iterates a HashMap key set, so product order is not asserted (Report 5.3 note).
        assertReceipt("GC-10", r, 9.00, 1);
        assertEquals(2, r.getItems().size(), "GC-10 two receipt lines (T2-T3 loop runs twice)");
        assertTrue(r.getDiscounts().stream().allMatch(d -> d.getProduct().equals(f.apple)),
            "GC-10 only the product with an offer receives a discount");
    }
}
