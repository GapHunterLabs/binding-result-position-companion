package dev.gaphunter.bindingresultpositioncompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import dev.gaphunter.bindingresultpositioncompanion.model.MisplacedBindingResultHit

/**
 * Finds a Spring MVC controller method parameter annotated `@Valid`/
 * `@Validated` whose matching `BindingResult`/`Errors` parameter
 * exists somewhere in the method signature, but isn't the parameter
 * immediately following it -- Spring's own
 * `ErrorsMethodArgumentResolver` javadoc is explicit: "An Errors
 * method argument is expected to appear immediately after the model
 * attribute in the method signature." When another parameter is
 * inserted between them, Spring can't resolve the binding result for
 * that attribute at all -- it either silently fails to bind, or (in
 * newer Spring versions) throws at startup, neither of which the
 * `@Valid` annotation or a normal compile warns about.
 *
 * **v0.1 scope, stated honestly:** only flags the case where a
 * `BindingResult`/`Errors` parameter DOES exist somewhere in the
 * method but isn't immediately next -- a `@Valid` parameter with no
 * `BindingResult`/`Errors` at all is a legitimate, common pattern
 * (Spring throws `MethodArgumentNotValidException` automatically in
 * that case) and is never flagged. Matches by simple annotation/type
 * name, not real type resolution.
 */
object JavaBindingResultPositionFinder {

    private val VALIDATION_ANNOTATIONS = setOf("Valid", "Validated")
    private val BINDING_RESULT_TYPES = setOf("BindingResult", "Errors")

    fun findAll(file: PsiFile): List<MisplacedBindingResultHit> {
        val hits = mutableListOf<MisplacedBindingResultHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                hits += hitsInMethod(method)
            }
        })
        return hits
    }

    private fun hitsInMethod(method: PsiMethod): List<MisplacedBindingResultHit> {
        val parameters = method.parameterList.parameters
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

    private fun isValidationAnnotated(parameter: PsiParameter): Boolean =
        parameter.modifierList?.annotations.orEmpty().any { annotation ->
            annotation.nameReferenceElement?.referenceName in VALIDATION_ANNOTATIONS
        }

    private fun isBindingResultType(parameter: PsiParameter): Boolean =
        parameter.type.presentableText.substringBefore('<') in BINDING_RESULT_TYPES
}
