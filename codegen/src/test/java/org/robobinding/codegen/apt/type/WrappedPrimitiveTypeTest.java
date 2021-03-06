package org.robobinding.codegen.apt.type;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;

import org.jmock.Expectations;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.robobinding.codegen.ClassMockery;

import com.google.testing.compile.CompilationRule;

/**
 * @since 1.0
 * @author Cheng Wei
 *
 */
@RunWith(Theories.class)
public class WrappedPrimitiveTypeTest {
	@ClassRule public static final CompilationRule compilation = new CompilationRule();
	
	@Test
	public void givenBooleanType_whenIsBoolean_thenReturnTrue() {
		PrimitiveType booleanType = primitiveTypeOf(TypeKind.BOOLEAN);
		WrappedPrimitiveType type = wrappedPrimitiveType(booleanType);
		
		assertThat(type.isBoolean(), equalTo(true));
	}

	private static PrimitiveType primitiveTypeOf(TypeKind kind) {
		return compilation.getTypes().getPrimitiveType(kind);
	}

	private WrappedPrimitiveType wrappedPrimitiveType(PrimitiveType booleanType) {
		return new WrappedPrimitiveType(booleanType, compilation.getTypes(), null);
	}
	
	@Theory
	public void givenNonBooleanType_whenIsBoolean_thenReturnFalse(
			@FromDataPoints("nonBooleanPrimitiveTypes") PrimitiveType nonBooleanPrimitiveType) {
		WrappedPrimitiveType type = wrappedPrimitiveType(nonBooleanPrimitiveType);
		
		assertThat(type.isBoolean(), equalTo(false));
	}
	
	@DataPoints("nonBooleanPrimitiveTypes")
	public static PrimitiveType[] nonBooleanPrimitiveTypes() {
		return new PrimitiveType[]{primitiveTypeOf(TypeKind.INT), primitiveTypeOf(TypeKind.LONG)};
	}
	
	@Theory
	public void shouldGetCorrectClassName(
			@FromDataPoints("primitiveClassNames") PrimitiveTypeToClassName typeToClassName) {
		WrappedPrimitiveType type = wrappedPrimitiveType(typeToClassName.type);
		
		assertThat(type.className(), equalTo(typeToClassName.className));
	}
	
	@DataPoints("primitiveClassNames")
	public static PrimitiveTypeToClassName[] primitiveClassNames() {
		return new PrimitiveTypeToClassName[] {
				primitiveType(TypeKind.INT).itsClassName("int"),
				primitiveType(TypeKind.BOOLEAN).itsClassName("boolean")};
	}
	
	private static PrimitiveTypeToClassName primitiveType(TypeKind kind) {
		return new PrimitiveTypeToClassName(primitiveTypeOf(kind));
	}
	
	@Theory
	public void shouldGetCorrectBoxedClassName(
			@FromDataPoints("primitiveBoxedClassNames") PrimitiveTypeToClassName typeToBoxedClassName) {
		ClassMockery context = new ClassMockery();
		final TypeMirrorWrapper wrapper = context.mock(TypeMirrorWrapper.class);
		final WrappedDeclaredType integerDeclaredType = context.mock(WrappedDeclaredType.class);
		final WrappedDeclaredType booleanDeclaredType = context.mock(WrappedDeclaredType.class);
		
		context.checking(new Expectations() {{
			allowing(integerDeclaredType).className(); will(returnValue(Integer.class.getName()));
			allowing(wrapper).wrap(declaredTypeOf(Integer.class)); will(returnValue(integerDeclaredType));
		    
			allowing(booleanDeclaredType).className(); will(returnValue(Boolean.class.getName()));
			allowing(wrapper).wrap(declaredTypeOf(Boolean.class)); will(returnValue(booleanDeclaredType));
		}});
		
		
		WrappedPrimitiveType type = new WrappedPrimitiveType(typeToBoxedClassName.type, compilation.getTypes(), wrapper);
		
		assertThat(type.boxedClassName(), equalTo(typeToBoxedClassName.className));
	}
	
	private DeclaredType declaredTypeOf(Class<?> type) {
		return (DeclaredType)compilation.getElements().getTypeElement(type.getName()).asType();
	}
	
	@DataPoints("primitiveBoxedClassNames")
	public static PrimitiveTypeToClassName[] primitiveBoxedClassNames() {
		return new PrimitiveTypeToClassName[] {
				primitiveType(TypeKind.INT).itsClassName(Integer.class.getName()),
				primitiveType(TypeKind.BOOLEAN).itsClassName(Boolean.class.getName())};
	}
	
	private static class PrimitiveTypeToClassName {
		public final PrimitiveType type;
		public String className;
		
		public PrimitiveTypeToClassName(PrimitiveType type) {
			this.type = type;
		}
		
		public PrimitiveTypeToClassName itsClassName(String name) {
			this.className = name;
			return this;
		}
	}
	
}
