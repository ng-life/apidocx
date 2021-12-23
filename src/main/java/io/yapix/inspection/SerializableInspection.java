package io.yapix.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiTypeElement;
import com.siyeh.ig.fixes.DelegatingFixFactory;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;

/**
 * 序列化检查
 *
 * @author lifeng
 */
public class SerializableInspection extends LocalInspectionTool implements Serializable {

    @Override
    public @NotNull
    PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PsiClass) {
                    PsiClass psiClass = (PsiClass) element;
                    if (psiClass == null) {
                        return;
                    }
                    String className = psiClass.getName();
                    if (className == null) {
                        return;
                    }
                    if (className.endsWith("DTO") || className.endsWith("VO")) {
                        for (PsiClass classInterface : psiClass.getInterfaces()) {
                            if (classInterface.getQualifiedName().equals(Serializable.class.getName())) {
                                return;
                            }
                        }
                        holder.registerProblem(psiClass.getNameIdentifier(), "Should implements Serializable", ProblemHighlightType.ERROR, DelegatingFixFactory.createMakeSerializableFix(psiClass));
                    }
                } else if (element instanceof PsiTypeElement) {
                    PsiJavaCodeReferenceElement referenceElement = ((PsiTypeElement) element).getInnermostComponentReferenceElement();
                    if (referenceElement == null) {
                        return;
                    }
                    PsiElement psiElement = referenceElement.resolve();
                    PsiClass psiClass = (PsiClass) psiElement;
                    if (psiClass == null) {
                        return;
                    }
                    String className = psiClass.getName();
                    if (className == null) {
                        return;
                    }
                    if (className.endsWith("DTO") || className.endsWith("VO")) {
                        for (PsiClass classInterface : psiClass.getInterfaces()) {
                            if (classInterface.getQualifiedName().equals(Serializable.class.getName())) {
                                return;
                            }
                        }
                        holder.registerProblem(element, "Type should implements Serializable", ProblemHighlightType.ERROR, DelegatingFixFactory.createMakeSerializableFix(psiClass));
                    }
                }
            }
        };
    }

}
