package com.wuzhizhan.mybatis.dom.converter;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import com.wuzhizhan.mybatis.alias.AliasClassReference;
import com.wuzhizhan.mybatis.alias.AliasFacade;
import com.wuzhizhan.mybatis.util.MybatisConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class AliasConverter extends ConverterAdaptor<PsiClass> implements CustomReferenceConverter<PsiClass> {

    @Nullable
    @Override
    public PsiClass fromString(@Nullable @NonNls String s, ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(s)) return null;
        if (!s.contains(MybatisConstants.DOT_SEPARATOR)) {
            return AliasFacade.getInstance(context.getProject()).findPsiClass(context.getXmlElement(), s).orElse(null);
        }
        return DomJavaUtil.findClass(s.trim(), context.getFile(), context.getModule(), GlobalSearchScope.allScope(context.getProject()));
    }

    @Nullable
    @Override
    public String toString(@Nullable PsiClass psiClass, ConvertContext context) {
        return psiClass == null ? null : psiClass.getQualifiedName();
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        if (((XmlAttributeValue) element).getValue().contains(MybatisConstants.DOT_SEPARATOR)) {
            JavaClassReferenceProvider provider = new JavaClassReferenceProvider() {
                @Override
                public GlobalSearchScope getScope(Project project) {
                    return context.getSearchScope();
                }
            };
            provider.setSoft(true);
            return provider.getReferencesByElement(element);
        } else {
            return new PsiReference[]{new AliasClassReference((XmlAttributeValue) element)};
        }
    }
}
