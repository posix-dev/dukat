package org.jetbrains.dukat.compiler.lowerings

import cartesian
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.UnionTypeDeclaration
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DynamicTypeNode

private fun specifyArguments(params: List<ParameterDeclaration>): List<List<ParameterDeclaration>> {
    return params.map { param ->
        val type = param.type
        if (type is DynamicTypeNode) {
            val projectedType = type.projectedType
            if (projectedType is UnionTypeDeclaration) {
                projectedType.params.map { param.copy(type = it) }
            } else listOf(param)
        } else listOf(param)
    }
}


private class SpecifyDynamicTypesLowering : IdentityLowering {

    fun generateParams(params: List<ParameterDeclaration>): List<List<ParameterDeclaration>> {
        val specifyParams = specifyArguments(params)
        return cartesian(*specifyParams.toTypedArray())
    }

    fun generateFunctionDeclarations(declaration: FunctionDeclaration): List<FunctionDeclaration> {
        return generateParams(declaration.parameters).map { params ->
            declaration.copy(parameters = params)
        }
    }

    fun generateConstructors(declaration: ConstructorNode): List<ConstructorNode> {
        val hasDynamic = declaration.parameters.any { (it.type is DynamicTypeNode) }

        return generateParams(declaration.parameters).map { params ->
            declaration.copy(parameters = params, generated = hasDynamic)
        }
    }

    override fun lowerClassNode(declaration: ClassNode): ClassNode {
        val members = declaration.members.map {member ->
            when(member) {
                is ConstructorNode -> generateConstructors(member)
                else -> listOf(member)
            }
        }.flatten()
        return declaration.copy(members = members)
    }

    fun lowerTopLevelDeclarationList(declaration: TopLevelDeclaration): List<TopLevelDeclaration> {
        return when (declaration) {
            is VariableDeclaration -> listOf(lowerVariableDeclaration(declaration))
            is FunctionDeclaration -> generateFunctionDeclarations(declaration)
            is ClassNode -> listOf(lowerClassNode(declaration))
            is InterfaceDeclaration -> listOf(lowerInterfaceDeclaration(declaration))
            is DocumentRootDeclaration -> listOf(lowerDocumentRoot(declaration))
            is TypeAliasDeclaration -> listOf(lowerTypeAliasDeclaration(declaration))
            else -> listOf(declaration)
        }
    }

    override fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.flatMap { declaration ->
            lowerTopLevelDeclarationList(declaration)
        }
    }

}


fun DocumentRootDeclaration.specifyDynamicTypes(): DocumentRootDeclaration {
    return SpecifyDynamicTypesLowering().lowerDocumentRoot(this)
}