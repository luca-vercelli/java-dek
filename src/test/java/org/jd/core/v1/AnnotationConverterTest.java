/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.jd.core.v1.impl.loader.ZipLoader;
import org.jd.core.v1.model.classfile.ClassFile;
import org.jd.core.v1.model.classfile.attribute.Annotations;
import org.jd.core.v1.model.javasyntax.reference.AnnotationReference;
import org.jd.core.v1.model.javasyntax.reference.AnnotationReferences;
import org.jd.core.v1.model.javasyntax.reference.BaseAnnotationReference;
import org.jd.core.v1.model.javasyntax.reference.ElementValuePairs;
import org.jd.core.v1.model.message.Message;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.AnnotationConverter;
import org.jd.core.v1.service.converter.classfiletojavasyntax.util.TypeMaker;
import org.jd.core.v1.service.deserializer.classfile.DeserializeClassFileProcessor;
import org.junit.Test;

public class AnnotationConverterTest {

	@Test
	public void test() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/zip/data-java-jdk-1.7.0.zip");
		ZipLoader loader = new ZipLoader(is);
		TypeMaker typeMaker = new TypeMaker(loader);
		AnnotationConverter converter = new AnnotationConverter(typeMaker);
		DeserializeClassFileProcessor deserializer = DeserializeClassFileProcessor.getInstance();

		Message message = new Message();
		message.setMainInternalTypeName("org/jd/core/test/AnnotatedClass");
		message.setLoader(loader);

		deserializer.process(message);

		ClassFile classFile = message.getClassFile();

		// Check class
		assertNotNull(classFile);

		Annotations visibles = classFile.getAttribute("RuntimeVisibleAnnotations");
		Annotations invisibles = classFile.getAttribute("RuntimeInvisibleAnnotations");
		BaseAnnotationReference annotationReferences = converter.convert(visibles, invisibles);

		assertNotNull(annotationReferences);
		assertTrue(annotationReferences instanceof AnnotationReferences);

		AnnotationReferences annotationReferenceList = (AnnotationReferences) annotationReferences;

		assertEquals(2, annotationReferenceList.size());

		AnnotationReference annotationReference0 = annotationReferenceList.getFirst();

		assertEquals("org/jd/core/test/annotation/Quality", annotationReference0.getType().getInternalName());
		assertEquals("Quality", annotationReference0.getType().getName());
		assertNotNull(annotationReference0.getElementValue());
		assertNull(annotationReference0.getElementValuePairs());
		assertEquals("ExpressionElementValue(" //
				+ "FieldReferenceExpression("
				+ "type=InnerObjectType(ObjectType(org/jd/core/test/annotation/Quality).Lorg/jd/core/test/annotation/Quality$Level;), "
				+ "expression=ObjectTypeReferenceExpression(InnerObjectType(ObjectType(org/jd/core/test/annotation/Quality).Lorg/jd/core/test/annotation/Quality$Level;)), "
				+ "name=HIGH, " //
				+ "descriptor=Lorg/jd/core/test/annotation/Quality$Level;)" //
				+ ")", annotationReference0.getElementValue().toString());

		AnnotationReference annotationReference1 = annotationReferenceList.get(1);

		assertEquals("org/jd/core/test/annotation/Author", annotationReference1.getType().getInternalName());
		assertEquals("Author", annotationReference1.getType().getName());
		assertNull(annotationReference1.getElementValue());
		assertNotNull(annotationReference1.getElementValuePairs());
		assertTrue(annotationReference1.getElementValuePairs() instanceof ElementValuePairs);

		ElementValuePairs elementValuePairArrayList = (ElementValuePairs) annotationReference1.getElementValuePairs();

		assertEquals(2, elementValuePairArrayList.size());
		assertEquals("value", elementValuePairArrayList.getFirst().getName());
		assertEquals("AnnotationElementValue(" //
				+ "ObjectType(org/jd/core/test/annotation/Name), " //
				+ "null" //
				+ ")", elementValuePairArrayList.get(0).getElementValue().toString());
		assertEquals("contributors", elementValuePairArrayList.get(1).getName());
		assertEquals("ElementValueArrayInitializerElementValue(" //
				+ "ElementValues([" //
				+ "AnnotationElementValue(" //
				+ "ObjectType(org/jd/core/test/annotation/Name), "
				+ "ExpressionElementValue(StringConstantExpression(\"Huey\"))), " //
				+ "AnnotationElementValue(" //
				+ "ObjectType(org/jd/core/test/annotation/Name), "
				+ "ExpressionElementValue(StringConstantExpression(\"Dewey\"))), " //
				+ "AnnotationElementValue(" //
				+ "ObjectType(org/jd/core/test/annotation/Name), "
				+ "ExpressionElementValue(StringConstantExpression(\"Louie\")))" //
				+ "]))", elementValuePairArrayList.get(1).getElementValue().toString());
	}
}
