package com.yourorg;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.*;

@Value
@EqualsAndHashCode(callSuper = false)
public class AddBaseClass extends Recipe {

    @Override
    public String getDisplayName() {
        return "Add `extends` to a class";
    }

    @Override
    public String getDescription() {
        return "Add `extends` to a class.";
    }

    @Option(displayName = "Fully qualified class name",
            description = "A fully-qualified class name to be extended with.",
            example = "com.yourorg.MyBaseClass")
    String fullyQualifiedClassName;

    @Option(displayName = "Fully qualified interface name",
            description = "A full-qualified interface name to be implemented with.",
            example = "com.yourorg.MyInterface")
    String fullyQualifiedInterfaceName;


    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {

        return new JavaIsoVisitor<ExecutionContext>() {

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl,
                                                            ExecutionContext ctx) {
                J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, ctx);
                if (cd.getExtends() == null) {
                    JavaTemplate
                            .builder(JavaType.ShallowClass.build(fullyQualifiedClassName).getClassName())
                            .imports(fullyQualifiedClassName)
                            .build()
                            .apply(
                                    getCursor(),
                                    cd.getCoordinates().addImplementsClause()
                            );
                }
                if (cd.getImplements() == null) {
                    JavaTemplate
                            .builder(JavaType.ShallowClass.build(fullyQualifiedInterfaceName).getClassName())
                            .imports(fullyQualifiedInterfaceName)
                            .build()
                            .apply(
                                    getCursor(),
                                    cd.getCoordinates().addImplementsClause()
                            );
                }
                return cd;
            }
        };
    }
}