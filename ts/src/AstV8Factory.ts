declare class AstFactoryV8 implements AstFactory {
    createPropertyAccessDeclaration(name: IdentifierDeclaration, expression: HeritageSymbol): PropertyAccessDeclaration;
    createCallSignatureDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): CallSignatureDeclaration;
    createClassDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<ClassLikeDeclaration>, modifiers: Array<ModifierDeclaration>, uid: string): ClassDeclaration;
    createConstructorDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>): ConstructorDeclaration;
    createDocumentRoot(packageName: string, declarations: Declaration[], modifiers: Array<ModifierDeclaration>, uid: string): DocumentRoot;
    createEnumDeclaration(name: String, values: Array<EnumTokenDeclaration>): EnumDeclaration;
    createEnumTokenDeclaration(value: String, meta: String): EnumTokenDeclaration;
    createExportAssignmentDeclaration(name: string, isExportEquals: boolean): ExportAssignmentDeclaration;
    createExpression(kind: TypeDeclaration, meta: string): Expression;
    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, uid: String): FunctionDeclaration;
    createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValue): FunctionTypeDeclaration;
    createHeritageClauseDeclaration(name: IdentifierDeclaration, typeArguments: Array<TokenDeclaration>, extending: boolean): HeritageClauseDeclaration;
    createIdentifierDeclaration(value: string): IdentifierDeclaration;
    createImportEqualsDeclaration(name: String, moduleReference: ModuleReferenceDeclaration): ImportEqualsDeclaration;
    createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: ParameterValue): IndexSignatureDeclaration;
    createInterfaceDeclaration(name: string, methods: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<InterfaceDeclaration>, uid: String): InterfaceDeclaration;
    createIntersectionTypeDeclaration(params: Array<ParameterValue>): IntersectionTypeDeclaration;
    createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>): FunctionDeclaration;
    createMethodSignatureDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): MethodSignatureDeclaration;
    createModifierDeclaration(name: string): ModifierDeclaration;
    createObjectLiteral(methods: Array<MemberDeclaration>): ObjectLiteral;
    createParameterDeclaration(name: string, type: ParameterValue, initializer: Expression | null, vararg: boolean, optional: boolean): ParameterDeclaration;
    createQualifiedNameDeclaration(left: ParameterValue, right: IdentifierDeclaration): QualifierDeclaration;
    createStringTypeDeclaration(tokens: Array<string>): StringTypeDeclaration;
    createThisTypeDeclaration(): ThisTypeDeclaration;
    createTokenDeclaration(value: string): TokenDeclaration;
    createTypeAliasDeclaration(aliasName: string, typeParams: Array<TokenDeclaration>, typeReference: ParameterValue): TypeAliasDeclaration;
    createTypeDeclaration(value: string, params: Array<ParameterValue>): TypeDeclaration;
    createTypeParam(name: string, constraints: Array<ParameterValue>): TypeParameter;
    createUnionTypeDeclaration(params: Array<ParameterValue>): UnionTypeDeclatation;
    declareProperty(value: string, type: ParameterValue, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): PropertyDeclaration;
    declareVariable(value: string, type: ParameterValue, modifiers: Array<ModifierDeclaration>, uid: String): VariableDeclaration;
}
