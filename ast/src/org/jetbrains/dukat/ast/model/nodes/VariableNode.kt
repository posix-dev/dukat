package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class VariableNode(
        val name: String,
        val type: ParameterValueDeclaration,

        val annotations: MutableList<AnnotationNode>,

        var immutable: Boolean,
        var owner: DocumentRootNode?,
        val uid: String
) : TopLevelDeclaration