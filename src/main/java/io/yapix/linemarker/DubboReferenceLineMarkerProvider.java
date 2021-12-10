package io.yapix.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons.Gutter;
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.searches.DirectClassInheritorsSearch;
import com.intellij.util.Query;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author lifeng
 */
public class DubboReferenceLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    public String getName() {
        return "DubboReference";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Gutter.ImplementedMethod;
    }


    @Override
    public RelatedItemLineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiField) {
            PsiField psiField = (PsiField) element;
            PsiAnnotation reference = psiField.getAnnotation("com.alibaba.dubbo.config.annotation.Reference");
            if (reference != null) {
                Collection<PsiClass> psiClasses = Optional.of(psiField)
                        .map(PsiVariable::getTypeElement)
                        .map(PsiTypeElement::getInnermostComponentReferenceElement)
                        .map(PsiReference::resolve)
                        .map(p -> (PsiClass) p)
                        .filter(PsiClass::isInterface)
                        .map(DirectClassInheritorsSearch::search)
                        .map(Query::findAll)
                        .orElse(Collections.emptyList());
                if (psiClasses.isEmpty()) {
                    return null;
                }
                return NavigationGutterIconBuilder
                        .create(Gutter.ImplementedMethod)
                        .setAlignment(Alignment.CENTER)
                        .setTargets(psiClasses)
                        .setTooltipText("DubboReference")
                        .createLineMarkerInfo(psiField.getNameIdentifier());
            }
        }
        return null;
    }

}
