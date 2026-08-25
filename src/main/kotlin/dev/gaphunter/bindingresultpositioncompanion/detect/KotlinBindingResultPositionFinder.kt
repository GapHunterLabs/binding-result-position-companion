package dev.gaphunter.bindingresultpositioncompanion.detect

import com.intellij.psi.PsiFile
import dev.gaphunter.bindingresultpositioncompanion.model.MisplacedBindingResultHit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaBindingResultPositionFinder]. */
object KotlinBindingResultPositionFinder {

    private val VALIDATION_ANNOTATIONS = setOf("Valid", "Validated")
    private val BINDING_RESULT_TYPES = setOf("BindingResult", "Errors")

    fun findAll(file: PsiFile): List<MisplacedBindingResultHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<MisplacedBindingResultHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                hits += hitsInFunction(function)
            }
        })
        return hits
    }

    private fun hitsInFunction(function: KtNamedFunction): List<MisplacedBindingResultHit> {
        val parameters = function.valueParameters
        val hits = mutableListOf<MisplacedBindingResultHit>()

        for ((index, parameter) in parameters.withIndex()) {
            if (!isValidationAnnotated(parameter)) continue

            val bindingResultIndex = parameters.indexOfFirst { isBindingResultType(it) }
            if (bindingResultIndex == -1) continue // no BindingResult/Errors anywhere -- legitimate, not flagged
            if (bindingResultIndex == index + 1) continue // correctly placed immediately after

            hits += MisplacedBindingResultHit(parameter)
        }
        return hits
    }

    private fun isValidationAnnotated(parameter: KtParameter): Boolean =
        parameter.annotationEntries.any { it.shortName?.asString() in VALIDATION_ANNOTATIONS }

    private fun isBindingResultType(parameter: KtParameter): Boolean =
        parameter.typeReference?.text?.substringBefore('<') in BINDING_RESULT_TYPES
}
