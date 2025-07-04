package io.apidocx.parse.util;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import java.util.Arrays;
import java.util.Optional;

public class PsiUtils {

    private PsiUtils() {
    }

    /**
     * 获取需要解析的实体字段
     */
    public static PsiField[] getFields(PsiClass t) {
        PsiField[] fields = t.getAllFields();
        return Arrays.stream(fields).filter(PsiUtils::isNeedField).toArray(PsiField[]::new);
    }


    public static boolean isNeedField(PsiField field) {
        return !field.hasModifier(JvmModifier.STATIC);
    }

    /**
     * 获取枚举类字段
     */
    public static PsiField[] getEnumFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields())
                .filter(field -> field instanceof PsiEnumConstant)
                .toArray(PsiField[]::new);
    }

    /**
     * 获取需要解析的实体字段
     */
    public static PsiField[] getStaticOrFinalFields(PsiClass t) {
        PsiField[] fields = t.getAllFields();
        return Arrays.stream(fields)
                .filter(f -> f.hasModifier(JvmModifier.STATIC) || f.hasModifier(JvmModifier.FINAL))
                .toArray(PsiField[]::new);
    }

    /**
     * 根据类短名来获取PsiClass, 而非类全限定名
     * 优先从当前模块依赖, 其次当前工程作用域
     */
    public static PsiClass findPsiClassByShortName(Project project, Module module, String shortName) {
        PsiClass psiClass = null;
        if (module != null) {
            psiClass = Optional.ofNullable(PsiShortNamesCache.getInstance(project)
                            .getClassesByName(shortName, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false)))
                    .filter(it -> it.length >= 1)
                    .map(it -> it[0])
                    .orElse(null);
        }
        if (psiClass == null) {
            psiClass = Optional.ofNullable(PsiShortNamesCache.getInstance(project)
                            .getClassesByName(shortName, GlobalSearchScope.projectScope(project)))
                    .filter(it -> it.length >= 1)
                    .map(it -> it[0])
                    .orElse(null);
        }
        return psiClass;
    }

    /**
     * 根据类全限定名获取PsiClass
     * 优先从当前模块依赖, 其次当前工程作用域
     */
    public static PsiClass findPsiClass(Project project, Module module, String qualifiedName) {
        PsiClass psiClass = null;
        if (module != null) {
            psiClass = JavaPsiFacade.getInstance(project)
                    .findClass(qualifiedName, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module));
        }
        if (psiClass == null) {
            psiClass = JavaPsiFacade.getInstance(project).findClass(qualifiedName, GlobalSearchScope.allScope(project));
        }
        if (psiClass != null && psiClass.canNavigate()) {
            psiClass = (PsiClass) psiClass.getNavigationElement();
        }
        return psiClass;
    }

    /**
     * 获取Getter方法
     */
    public static PsiMethod[] getGetterMethods(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllMethods()).filter(method -> {
            String methodName = method.getName();
            PsiType returnType = method.getReturnType();
            PsiParameterList parameterList = method.getParameterList();
            PsiModifierList modifierList = method.getModifierList();
            return !modifierList.hasModifierProperty(PsiModifier.STATIC)
                    && !methodName.equals("getClass")
                    && (parameterList.getParametersCount() == 0 && (methodName.startsWith("get") && methodName.length() > 3 && returnType != null)
                    || (parameterList.getParametersCount() == 0 && methodName.startsWith("is") && methodName.length() > 2 && returnType != null && returnType
                    .getCanonicalText().equals("boolean"))
            );
        }).toArray(PsiMethod[]::new);
    }

    /**
     * 获取Getter方法
     *
     * @param psiClass  目标类
     * @param fieldName 字段名
     * @param fieldType 字段类型
     * @return 对应Getter方法，可能为null。
     */
    public static PsiMethod getGetterMethod(PsiClass psiClass, String fieldName, PsiType fieldType) {
        String prefix = fieldType.getCanonicalText().equals("boolean") ? "is" : "get";
        String methodName = prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        PsiMethod targetMethod = JavaPsiFacade.getElementFactory(psiClass.getProject())
                .createMethod(methodName, fieldType);
        return psiClass.findMethodBySignature(targetMethod, true);
    }

}
