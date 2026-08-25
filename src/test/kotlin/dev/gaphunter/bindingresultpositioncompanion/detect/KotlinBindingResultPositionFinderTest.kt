package dev.gaphunter.bindingresultpositioncompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinBindingResultPositionFinderTest : BasePlatformTestCase() {

    fun `test a parameter inserted between Valid and BindingResult is flagged`() {
        val file = myFixture.configureByText(
            "OrderController.kt",
            """
            class OrderController {
                fun createOrder(@Valid form: OrderForm, model: Model, bindingResult: BindingResult): String {
                    return "orders/new"
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinBindingResultPositionFinder.findAll(file).size)
    }

    fun `test BindingResult immediately after Valid is not flagged`() {
        val file = myFixture.configureByText(
            "OrderController.kt",
            """
            class OrderController {
                fun createOrder(@Valid form: OrderForm, bindingResult: BindingResult, model: Model): String {
                    return "orders/new"
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinBindingResultPositionFinder.findAll(file).isEmpty())
    }

    fun `test Valid with no BindingResult anywhere is not flagged`() {
        val file = myFixture.configureByText(
            "OrderController.kt",
            """
            class OrderController {
                fun createOrder(@Valid form: OrderForm, model: Model): String {
                    return "orders/new"
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinBindingResultPositionFinder.findAll(file).isEmpty())
    }
}
