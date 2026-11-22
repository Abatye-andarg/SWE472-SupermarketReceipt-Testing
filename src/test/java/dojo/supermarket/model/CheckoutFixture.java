package dojo.supermarket.model;

/**
 * Shared test fixture for the SWE 472 test suite.
 *
 * All ISP and graph-coverage tests use one product sold by the unit at EUR 3.00
 * (Report Sections 4 and 5.3). A second product, also EUR 3.00 and with no offer,
 * is used by GC-10 to drive two iterations of the handleOffers loop.
 */
final class CheckoutFixture {

    static final double UNIT_PRICE = 3.00;
    /** Tolerance for comparing money values held as double. */
    static final double EPS = 0.001;

    final SupermarketCatalog catalog = new FakeCatalog();
    final Product apple = new Product("apple", ProductUnit.Each);
    final Product bread = new Product("bread", ProductUnit.Each);
    final Teller teller;
    final ShoppingCart cart = new ShoppingCart();

    CheckoutFixture() {
        catalog.addProduct(apple, UNIT_PRICE);
        catalog.addProduct(bread, UNIT_PRICE);
        teller = new Teller(catalog);
    }

    /**
     * Installs the offer described by an ISP "offer" token on the apple product.
     * Tokens: NONE, THREE_FOR_TWO, TWO_FOR_AMOUNT:5.00, FIVE_FOR_AMOUNT:11.00, TEN_PERCENT:10
     */
    CheckoutFixture withOffer(String token) {
        String[] parts = token.split(":");
        switch (parts[0]) {
            case "NONE":
                break;
            case "THREE_FOR_TWO":
                teller.addSpecialOffer(SpecialOfferType.ThreeForTwo, apple, 0.0);
                break;
            case "TWO_FOR_AMOUNT":
                teller.addSpecialOffer(SpecialOfferType.TwoForAmount, apple, Double.parseDouble(parts[1]));
                break;
            case "FIVE_FOR_AMOUNT":
                teller.addSpecialOffer(SpecialOfferType.FiveForAmount, apple, Double.parseDouble(parts[1]));
                break;
            case "TEN_PERCENT":
                teller.addSpecialOffer(SpecialOfferType.TenPercentDiscount, apple, Double.parseDouble(parts[1]));
                break;
            default:
                throw new IllegalArgumentException("Unknown offer token: " + token);
        }
        return this;
    }

    CheckoutFixture withApples(double quantity) {
        cart.addItemQuantity(apple, quantity);
        return this;
    }

    /** Calls the function under test: Teller.checksOutArticlesFrom(ShoppingCart). */
    Receipt checkout() {
        return teller.checksOutArticlesFrom(cart);
    }
}
