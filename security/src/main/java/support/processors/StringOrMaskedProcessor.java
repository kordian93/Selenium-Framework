package support.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import support.annotations.StringOrMasked;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("support.annotations.StringOrMasked")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class StringOrMaskedProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedParam : roundEnv.getElementsAnnotatedWith(StringOrMasked.class)) {
            if (!(annotatedParam.getKind() == ElementKind.PARAMETER)) {
                continue;
            }

            ExecutableElement method = (ExecutableElement) annotatedParam.getEnclosingElement();
            TypeElement classElement = (TypeElement) method.getEnclosingElement();

            String originalClassName = classElement.getSimpleName().toString();
            String validatedClassName = originalClassName + "_Validated";

            MethodSpec.Builder newMethod = MethodSpec
                    .methodBuilder(method.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(method.getReturnType()));

            StringBuilder argumentListBuilder = new StringBuilder();
            for (VariableElement param : method.getParameters()) {
                TypeName type = TypeName.get(param.asType());
                String name = param.getSimpleName().toString();
                newMethod.addParameter(type, name);

                if (!argumentListBuilder.isEmpty()) {
                    argumentListBuilder.append(", ");
                }
                argumentListBuilder.append(name);

                if (param.equals(annotatedParam)) {
                    newMethod
                            .beginControlFlow("if (!($N instanceof String || $N instanceof support.security.MaskedString))", name, name)
                            .addStatement("throw new IllegalArgumentException(\"@StringOrMasked requires String or MaskedString: \" + $N.getClass())", name)
                            .endControlFlow();
                }
            }

            String argumentList = argumentListBuilder.toString();

            newMethod.addStatement("return new $T().$N($L);", classElement, method.getSimpleName(), argumentList);

            TypeSpec validatedClass = TypeSpec
                    .classBuilder(validatedClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(newMethod.build())
                    .build();

            JavaFile javaFile = JavaFile
                    .builder(processingEnv.getElementUtils().getPackageOf(classElement).toString(), validatedClass)
                    .build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write validated class: " + e.getMessage());
            }
        }
        return true;
    }
}
