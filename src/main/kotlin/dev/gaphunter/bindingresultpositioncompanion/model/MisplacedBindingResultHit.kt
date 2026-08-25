package dev.gaphunter.bindingresultpositioncompanion.model

import com.intellij.psi.PsiElement

/** One @Valid/@Validated parameter whose BindingResult/Errors parameter exists but isn't the very next one. */
data class MisplacedBindingResultHit(val anchorElement: PsiElement)
