package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

data class GeneratedInterfaceReferenceDeclaration(
        val name: String,

        override val nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration