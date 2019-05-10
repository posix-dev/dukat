package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.transform
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration


fun buildUidTable(docRoot: DocumentRootNode, map: MutableMap<String, AstTopLevelEntity> = mutableMapOf()): Map<String, AstTopLevelEntity> {
    map[docRoot.uid] = docRoot

    docRoot.declarations.forEach { declaration ->
        when (declaration) {
            is InterfaceNode -> map[declaration.uid] = declaration
            is ClassNode -> map[declaration.uid] = declaration
            is FunctionNode -> map[declaration.uid] = declaration
            is VariableNode -> map[declaration.uid] = declaration
            is DocumentRootNode -> buildUidTable(declaration, map)
            else -> Unit
        }
    }

    return map
}

fun introduceExportAnnotations(docRoot: DocumentRootNode, uidTable: Map<String, AstTopLevelEntity>, turnOff: MutableSet<NameNode>, exportedModules: MutableMap<String, NameNode?>): DocumentRootNode {

    val declarations = docRoot.declarations.mapNotNull { declaration ->
        when (declaration) {
            is DocumentRootNode -> introduceExportAnnotations(declaration, uidTable, turnOff, exportedModules)

            is ExportAssignmentDeclaration -> {
                val defaultAnnotation = AnnotationNode("JsName", listOf(IdentifierNode("default")))


                if (!declaration.isExportEquals) {
                    uidTable.get(declaration.name)?.let { entity ->
                        when (entity) {
                            is FunctionNode -> {
                                if (!entity.export) {
                                    entity.annotations.add(defaultAnnotation)
                                } else Unit
                            }
                            is VariableNode -> entity.annotations.add(defaultAnnotation)
                            else -> Unit
                        }
                    }

                    null
                } else {
                    val entity = uidTable.get(declaration.name)

                    when (entity) {
                        is DocumentRootNode -> {

                            docRoot.qualifiedNode?.let { qualifiedNode ->
                                exportedModules[entity.uid] = qualifiedNode
                            }

                            null
                        }
                        is ClassNode -> {

                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                docRoot.qualifiedNode?.let { qualifiedNode ->
                                    entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                                }
                            }
                            null
                        }
                        is InterfaceNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }

                            if (docRoot.owner != null) {
                                docRoot.qualifiedNode?.let { qualifiedNode ->
                                    entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                                }
                            }
                            null
                        }
                        is FunctionNode -> {
                            entity.owner?.let { ownerModule ->
                                turnOff.add(ownerModule.fullPackageName)

                                ownerModule.declarations.filterIsInstance(DocumentRootNode::class.java).firstOrNull() { submodule ->
                                    submodule.packageName == entity.name.translate()
                                }?.let { eponymousDeclaration ->
                                    exportedModules.put(eponymousDeclaration.uid, ownerModule.qualifiedNode)
                                }
                            }

                            //TODO: investigate how set annotations only at FunctionNode only
                            docRoot.declarations.filterIsInstance(FunctionNode::class.java).forEach {
                                if (it != entity) {
                                    docRoot.qualifiedNode?.let { qualifiedNode ->
                                        it.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                                    }
                                }
                            }

                            if (docRoot.owner != null) {
                                docRoot.qualifiedNode?.let { qualifiedNode ->
                                    entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                                }
                            }
                            null
                        }
                        is VariableNode -> {
                            entity.owner?.let {
                                turnOff.add(it.fullPackageName)
                            }


                            if (docRoot.uid == entity.owner?.uid) {
                                docRoot.qualifiedNode?.let { qualifiedNode ->
                                    entity.name = qualifiedNode
                                }
                            }

                            if (docRoot.owner != null) {
                                docRoot.qualifiedNode?.let { qualifiedNode ->
                                    entity.annotations.add(AnnotationNode("JsModule", listOf(qualifiedNode)))
                                }
                                entity.immutable = true
                            }

                            null
                        }
                        else -> declaration
                    }
                }

            }

            else -> declaration
        }
    }

    return docRoot.copy(declarations = declarations)
}



private fun DocumentRootNode.turnOff(turnOffData: MutableSet<NameNode>): DocumentRootNode {
    if (turnOffData.contains(fullPackageName)) {
        showQualifierAnnotation = false
    }

    val declarations = declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> {
            declaration.turnOff(turnOffData)
            }
            else -> declaration
        }
    }

    return copy(declarations = declarations)
}

private fun DocumentRootNode.markModulesAsExported(exportedModulesData: Map<String, NameNode?>): DocumentRootNode {
    if (exportedModulesData.containsKey(uid)) {
        qualifiedNode = exportedModulesData.getValue(uid)
        isQualifier = false
    }

    val declarations = declarations.map { declaration ->
        when (declaration) {
            is DocumentRootNode -> {
                declaration.markModulesAsExported(exportedModulesData)
            }
            else -> declaration
        }
    }

    return copy(declarations = declarations)
}

fun DocumentRootNode.introduceExportAnnotations(): DocumentRootNode {
    val uidTable = org.jetbrains.dukat.nodeIntroduction.buildUidTable(this)
    val turnOffData = mutableSetOf<NameNode>()
    val exportedModulesData = mutableMapOf<String, NameNode?>()
    val docRoot = org.jetbrains.dukat.nodeIntroduction.introduceExportAnnotations(this, uidTable, turnOffData, exportedModulesData)

    return docRoot.turnOff(turnOffData).markModulesAsExported(exportedModulesData)
}

fun SourceSetNode.introduceExportAnnotations() = transform { it.introduceExportAnnotations() }