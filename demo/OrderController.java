// Demo data for Binding Result Position Companion -- used with
// `./gradlew runIde` to capture the real Marketplace screenshot. Open
// this file, the warning should appear on the @Valid parameter.

class OrderController {

    String createOrderUnsafely(@Valid OrderForm form, Model model, BindingResult bindingResult) {
        // BindingResult exists, but Model sits between it and @Valid
        // -- FLAGGED. Spring can't resolve the binding result for
        // "form" at all.
        if (bindingResult.hasErrors()) {
            return "orders/new";
        }
        return "orders/success";
    }

    String createOrderSafely(@Valid OrderForm form, BindingResult bindingResult, Model model) {
        // BindingResult immediately after @Valid -- NOT flagged.
        if (bindingResult.hasErrors()) {
            return "orders/new";
        }
        return "orders/success";
    }
}
