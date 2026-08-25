package dev.gaphunter.bindingresultpositioncompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.bindingresultpositioncompanion.detect.JavaBindingResultPositionFinder
import dev.gaphunter.bindingresultpositioncompanion.detect.KotlinBindingResultPositionFinder
import dev.gaphunter.bindingresultpositioncompanion.model.MisplacedBindingResultHit
import dev.gaphunter.bindingresultpositioncompanion.review.ReviewPrompt

class MisplacedBindingResultLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "@Valid parameter's BindingResult not immediately next"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaBindingResultPositionFinder.findAll(file)
            "kotlin" -> KotlinBindingResultPositionFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.anchorElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: MisplacedBindingResultHit): LineMarkerInfo<PsiElement> {
        val tooltip = "This parameter's BindingResult/Errors exists in the method signature, but isn't the " +
            "very next parameter -- Spring's own ErrorsMethodArgumentResolver javadoc says the Errors argument " +
            "is expected to appear immediately after the model attribute, or it can't be resolved for it"
        return LineMarkerInfo(
            hit.anchorElement,
            hit.anchorElement.textRange,
            MisplacedBindingResultIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
