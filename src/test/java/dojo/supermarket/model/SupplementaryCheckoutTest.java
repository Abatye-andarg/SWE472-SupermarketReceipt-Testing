package dojo.supermarket.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dojo.supermarket.model.CheckoutFixture.EPS;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Supplementary tests added during implementation (Student 3).
 * They do not change ISP or graph coverage but check behaviour described in the
 * Program Overview: ShoppingCart combines repeated additions of the same product
 * before offers are evaluated.
 */
@DisplayName("Supplementary - cart aggregation")
class SupplementaryCheckoutTest {

    @Test
    @DisplayName("SUP-01: same product added twice (2 + 1) under 3-for-2 -> combined qty 3 -> EUR 6.00, 1 discount")
    void sup01_repeatedAdditionsAreCombinedBeforeOffers() {
        CheckoutFixture f = new CheckoutFixture().withOffer("THREE_FOR_TWO").withApples(2).withApples(1);

        Receipt r = f.checkout();

        assertEquals(2, r.getItems().size(), "two separate receipt lines");
        assertEquals(6.00, r.getTotalPrice(), EPS);
        assertEquals(1, r.getDiscounts().size());
    }
}
