package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

interface DeclarationLowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration
    fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration
    fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration): UnionTypeDeclaration
    fun lowerIntersectionTypeDeclatation(declaration: IntersectionTypeDeclaration): IntersectionTypeDeclaration
    fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration
    fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration)
            is UnionTypeDeclaration -> lowerUnionTypeDeclation(declaration)
            is IntersectionTypeDeclaration -> lowerIntersectionTypeDeclatation(declaration)
            else -> declaration
        }
    }


    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is ClassDeclaration -> lowerClassDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is DocumentRootDeclaration -> lowerDocumentRoot(declaration)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerDocumentRoot(documentRoot: DocumentRootDeclaration): DocumentRootDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations))
    }

}