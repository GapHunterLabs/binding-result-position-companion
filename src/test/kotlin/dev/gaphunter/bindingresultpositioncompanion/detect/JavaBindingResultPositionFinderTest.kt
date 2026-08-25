package dev.gaphunter.bindingresultpositioncompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaBindingResultPositionFinderTest : BasePlatformTestCase() {

    fun `test a parameter inserted between Valid and BindingResult is flagged`() {
        val file = myFixture.configureByText(
            "OrderController.java",
            """
            class OrderController {
                String createOrder(@Valid OrderForm form, Model model, BindingResult bindingResult) {
                    return "orders/new";
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaBindingResultPositionFinder.findAll(file).size)
    }

    fun `test BindingResult immediately after Valid is not flagged`() {
        val file = myFixture.configureByText(
            "OrderController.java",
            """
            class OrderController {
                String createOrder(@Valid OrderForm form, BindingResult bindingResult, Model model) {
                    return "orders/new";
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaBindingResultPositionFinder.findAll(file).isEmpty())
    }

    fun `test Valid with no BindingResult anywhere is not flagged`() {
        val file = myFixture.configureByText(
            "OrderController.java",
            """
            class OrderController {
                String createOrder(@Valid OrderForm form, Model model) {
                    return "orders/new";
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaBindingResultPositionFinder.findAll(file).isEmpty())
    }

    fun `test Errors immediately after Validated is not flagged`() {
        val file = myFixture.configureByText(
            "OrderController.java",
            """
            class OrderController {
                String createOrder(@Validated OrderForm form, Errors errors) {
                    return "orders/new";
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaBindingResultPositionFinder.findAll(file).isEmpty())
    }
}
