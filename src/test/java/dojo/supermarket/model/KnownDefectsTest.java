package dojo.supermarket.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static dojo.supermarket.model.CheckoutFixture.EPS;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that document defects found in the program. They assert the CORRECT
 * behaviour and therefore FAIL against the current implementation.
 *
 * Tagged "defect": excluded from the normal `./gradlew test` run and executed with
 *   ./gradlew defectTest
 */
@Tag("defect")
@DisplayName("Known defects (expected to FAIL)")
class KnownDefectsTest {

    /**
     * DEF-01 (found by Student 2 while probing the CFG, path P9 with quantity 0).
     * Root cause: faulty logic - ShoppingCart.handleOffers line 64 (node N13/N14) creates
     * the "% off" Discount with no quantity guard, unlike the other three offer types.
     */
    @Test
    @DisplayName("DEF-01: 10% offer, qty 0 -> expect no discount line (actual: one EUR 0.00 '10.0% off' line)")
    void def01_tenPercentOnZeroQuantityAddsEmptyDiscountLine() {
        Receipt r = new CheckoutFixture().withOffer("TEN_PERCENT:10").withApples(0).checkout();

        assertEquals(0.00, r.getTotalPrice(), EPS, "total is correct");
        assertEquals(0, r.getDiscounts().size(), "a zero-value discount line should not be printed");
    }

    /**
     * DEF-02 (found by Student 3). Products sold by weight (ProductUnit.Kilo) accept
     * fractional quantities, but handleOffers truncates them to int (line 42,
     * quantityAsInt = (int) quantity) and only re-prices the whole-unit remainder.
     * The fractional part is charged in the regular line but then refunded by the discount,
     * so it is given away for free. Root cause: faulty logic, and a gap in the ISP,
     * which only modelled whole quantities of products sold by the unit.
     */
    @Test
    @DisplayName("DEF-02: 3.5 kg at EUR 3.00/kg under 3-for-2 -> expect EUR 7.50 (actual: EUR 6.00, 0.5 kg free)")
    void def02_fractionalQuantityIsGivenAwayByThreeForTwo() {
        CheckoutFixture f = new CheckoutFixture();
        Product cheese = new Product("cheese", ProductUnit.Kilo);
        f.catalog.addProduct(cheese, 3.00);
        f.teller.addSpecialOffer(SpecialOfferType.ThreeForTwo, cheese, 0.0);
        f.cart.addItemQuantity(cheese, 3.5);

        Receipt r = f.checkout();

        // 3.5 kg = 10.50 regular; one bundle of 3 kg saves one kg (3.00); 0.5 kg remains at full price.
        assertEquals(7.50, r.getTotalPrice(), EPS, "only one full kilo should be free");
    }
}
