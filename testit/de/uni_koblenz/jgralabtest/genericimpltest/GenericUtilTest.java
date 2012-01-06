package de.uni_koblenz.jgralabtest.genericimpltest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.impl.generic.GenericUtil;
import de.uni_koblenz.jgralab.schema.*;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public class GenericUtilTest {
	
	// Create a schema and various domains for testing purposes
	private static Schema schema = new SchemaImpl("TempAttributeTest", "testprefix");
	private static EnumDomain enumDomain;				// EnumDomain TestEnumDomain ( FIRST, SECOND, THIRD );
	private static SetDomain simpleSetDomain;			// Set<Boolean>
	private static ListDomain simpleListDomain;			// List<String>
	private static MapDomain simpleMapDomain;			// Map<Integer, Long>
	private static RecordDomain simpleRecordDomain;		// RecordDomain SimpleRecordDomain (BoolComponent: Boolean, DoubleComponent: Double, EnumComponent: TestEnumDomain, IntComponent: Integer, ListComponent: List<String>, LongComponent: Long, MapComponent: Map<Integer, Long>, SetComponent: Set<Boolean>, StringComponent: String);
	private static SetDomain complexSetDomain;			// Set<Map<Integer, Long>>
	private static ListDomain complexListDomain;		// List<Map<Integer, Long>>
	private static MapDomain complexMapDomain;			// Map<List<String>, List<Map<Integer, Long>>>
	private static MapDomain complexMapDomain2;			// Map<String, *SimpleRecordDomain*>
	private static RecordDomain complexRecordDomain;	// RecordDomain ComplexRecordDomain (ListComponent: List<Map<Integer, Long>>, MapComponent: Map<List<String>, List<Map<Integer, Long>>>, RecordComponent: *SimpleRecordDomain*, SetComponent: Set<Map<Integer, Long>>);
	static {
		enumDomain = schema.createEnumDomain("TestEnumDomain");
		enumDomain.addConst("FIRST");
		enumDomain.addConst("SECOND");
		enumDomain.addConst("THIRD");
		simpleSetDomain = schema.createSetDomain(schema.getBooleanDomain());
		simpleListDomain = schema.createListDomain(schema.getStringDomain());
		simpleMapDomain = schema.createMapDomain(schema.getIntegerDomain(), schema.getLongDomain());
		simpleRecordDomain = schema.createRecordDomain("SimpleRecordDomain");
		simpleRecordDomain.addComponent("BoolComponent", schema.getBooleanDomain());
		simpleRecordDomain.addComponent("IntComponent", schema.getIntegerDomain());
		simpleRecordDomain.addComponent("LongComponent", schema.getLongDomain());
		simpleRecordDomain.addComponent("DoubleComponent", schema.getDoubleDomain());
		simpleRecordDomain.addComponent("StringComponent", schema.getStringDomain());
		simpleRecordDomain.addComponent("EnumComponent", enumDomain);
		simpleRecordDomain.addComponent("SetComponent", simpleSetDomain);
		simpleRecordDomain.addComponent("ListComponent", simpleListDomain);
		simpleRecordDomain.addComponent("MapComponent", simpleMapDomain);
		complexSetDomain = schema.createSetDomain(simpleMapDomain);
		complexListDomain = schema.createListDomain(simpleMapDomain);
		complexMapDomain = schema.createMapDomain(simpleListDomain, simpleMapDomain);
		complexMapDomain2 = schema.createMapDomain(schema.getStringDomain(), simpleRecordDomain);
		complexRecordDomain = schema.createRecordDomain("ComplexRecordDomain");
		complexRecordDomain.addComponent("SetComponent", complexSetDomain);
		complexRecordDomain.addComponent("ListComponent", complexListDomain);
		complexRecordDomain.addComponent("MapComponent", complexMapDomain);
		complexRecordDomain.addComponent("RecordComponent", simpleRecordDomain);
	}

	// Serialized values to test serializing and parsing
	private static String serializedBoolDomainValue1 = "t";
	private static String serializedBoolDomainValue2 = "f";
	private static String serializedIntDomainMinValue = Integer.toString(Integer.MIN_VALUE);
	private static String serializedIntDomainMaxValue = Integer.toString(Integer.MAX_VALUE);
	private static String serializedIntDomainValue = "42";
	private static String serializedLongDomainMinValue = Long.toString(Long.MIN_VALUE);
	private static String serializedLongDomainMaxValue = Long.toString(Long.MAX_VALUE);
	private static String serializedLongDomainValue = "111";
	private static String serializedDoubleDomainValue1 = Double.toString(Double.MIN_VALUE);
	private static String serializedDoubleDomainValue2 = Double.toString(Double.MAX_VALUE);
	private static String serializedDoubleDomainValue3 = Double.toString(42.123456);
	private static String serializedStringDomainEmptyValue = "\"\"";
	private static String serializedStringDomainValue1 = "\"abcd efgh +-#'*~<>(){}[]?!\"";
	private static String serializedEnumDomainValue1 = "FIRST";
	private static String serializedEnumDomainValue2 = "SECOND";
	private static String serializedSetDomainValue1 = "{t f}";	// simpleSetDomain
	private static String serializedSetDomainValue2 = "{{123 - 4561 321 - 6541} {243 - 4151 312 - 6451}}";	// complexSetDomain
	private static String serializedSetDomainEmptyValue = "{}";
	private static String serializedListDomainValue1 = "[\"some string\" \"another one\"]";	// simpleListDomain
	private static String serializedListDomainValue2 = "[{} {123 - 4561 321 - 6541} {243 - 4151 312 - 6451}]";	// complexListDomain
	private static String serializedListDomainEmptyValue = "[]";
	private static String serializedMapDomainValue1 = "{24 - 0 -1000 - 9876543210 14 - 1234567890}";	// simpleMapDomain
	private static String serializedMapDomainValue2 = "{[\"a\" \"b\" \"c\"] - {1 - 123456 42 - 435782} [\"d\" \"e\" \"f\"] - {} [] - {} [\"abc\"] - {73 - 192837 84 - 46565}}";	// complexMapDomain:   Map<List<String>, Map<Integer, Long>>
	private static String serializedMapDomainEmptyValue = "{}";
	private static String serializedRecordDomainValue1 = "(t 3.1 SECOND 42 [\"some string\" \"another one\"] 1016 {42 - 1016 1 - 39215 7 - 1234567890} {t f} \"somestring\")";	// simpleRecordDomain
//	private static String serializedMapDomainValue3 = "{\"key one\" - " + serializedRecordDomainValue1 + " \"key two\" - " + GraphIO.NULL_LITERAL + "}";	// complexMapDomain2: Map<String, SimpleRecordDomain>
	private static String serializedRecordDomainValue2 = "(" +				// complexRecordDomain
			"[{} {123 - 4561 321 - 6541} {243 - 4151 312 - 6451}] " +		//ListComponent: List<Map<Integer, Long>>
			"{[\"a\" \"b\" \"c\"] - {1 - 123456 42 - 435782 7 - 832 8 - 112345} [\"d\" \"e\" \"f\"] - {} [] - {73 - 192837 84 - 46565}} " +	// MapComponent: Map<List<String>, Map<Integer, Long>>
			serializedRecordDomainValue1 + " " +		// RecordComponent: *SimpleRecordDomain*
			"{{123 - 4561 321 - 6541} {243 - 4151 312 - 6451}}" +	// SetComponent: Set<Map<Integer, Long>>
			")";
//	private static String serializedRecordDomainValue3 = "(" + GraphIO.NULL_LITERAL + " " + GraphIO.NULL_LITERAL + " " + GraphIO.NULL_LITERAL + " " + GraphIO.NULL_LITERAL + ")";	// complexRecordDomain - all components are null
	private static String serializedNullValue = GraphIO.NULL_LITERAL;
	
	// Values of various domains to test serializing and parsing
	private static Object nullValue = null;
	private static Object boolDomainValue1 = new Boolean(true);
	private static Object boolDomainValue2 = new Boolean(false);
	private static Object intDomainMinValue = new Integer(Integer.MIN_VALUE);
	private static Object intDomainMaxValue = new Integer(Integer.MAX_VALUE);
	private static Object intDomainValue = new Integer(42);
	private static Object longDomainMinValue = new Long(Long.MIN_VALUE);
	private static Object longDomainMaxValue = new Long(Long.MAX_VALUE);
	private static Object longDomainValue = new Long(111);
	private static Object doubleDomainValue1 = new Double(Double.MIN_VALUE);
	private static Object doubleDomainValue2 = new Double(Double.MAX_VALUE);
	private static Object doubleDomainValue3 = new Double(42.123456);
	private static Object stringDomainEmptyValue = "";
	private static Object stringDomainValue1 = "abcd efgh +-#'*~<>(){}[]?!";
	private static Object enumDomainValue1 = "FIRST";
	private static Object enumDomainValue2 = "SECOND";
	private static Object setDomainValue1 = JGraLab.set().plus(true).plus(false);
	private static Object setDomainValue2 = JGraLab.set().plus(JGraLab.map().plus(123, 4561l).plus(321, 6541l)).plus(JGraLab.map().plus(243, 4151l).plus(312, 6451l));
	private static Object setDomainEmptyValue = JGraLab.set();
	private static Object listDomainValue1 = JGraLab.vector().plus("some string").plus("another one");
	private static Object listDomainValue2 = JGraLab.vector()
			.plus(JGraLab.map())
			.plus(JGraLab.map().plus(new Integer(123), new Long(4561)).plus(new Integer(321), new Long(6541)))
			.plus(JGraLab.map().plus(new Integer(243), new Long(4151)).plus(new Integer(312), new Long(6451)));
	private static Object listDomainEmptyValue = JGraLab.vector();
	private static Object mapDomainValue1 = JGraLab.map()
			.plus(new Integer(24), new Long(0))
			.plus(new Integer(-1000), new Long(9876543210l))
			.plus(new Integer(14), new Long(1234567890l));
	private static Object mapDomainValue2 = JGraLab.map()
			.plus(JGraLab.vector().plus("a").plus("b").plus("c"), JGraLab.map().plus(new Integer(1), new Long(123456)).plus(new Integer(42), new Long(435782))) // "{[\"a\" \"b\" \"c\"] - {1 - 123456 42 - 435782} [\"d\" \"e\" \"f\"] - {} [] - {} [\"abc\"] - {73 - 192837 84 - 46565}}
			.plus(JGraLab.vector().plus("d").plus("e").plus("f"), JGraLab.map()) // [\"d\" \"e\" \"f\"] - {}
			.plus(JGraLab.vector(), JGraLab.map())	// [] - {}	
			.plus(JGraLab.vector().plus("abc"), JGraLab.map().plus(new Integer(73), new Long(192837)).plus(new Integer(84), new Long(46565)));	// [\"abc\"] - {73 - 192837 84 - 46565}
	private static Object mapDomainEmptyValue = JGraLab.map();
	private static Object recordDomainValue1 = de.uni_koblenz.jgralab.impl.RecordImpl.empty()
			.plus("BoolComponent", true)
			.plus("DoubleComponent", 3.1)
			.plus("EnumComponent", "SECOND")
			.plus("IntComponent", 42)
			.plus("ListComponent", JGraLab.vector().plus("some string").plus("another one"))
			.plus("LongComponent", 1016l)
			.plus("MapComponent", JGraLab.map().plus(42, 1016l).plus(1, 39215l).plus(7, 1234567890l))
			.plus("SetComponent", JGraLab.set().plus(true).plus(false))
			.plus("StringComponent", "somestring");
//	private static Object mapDomainValue3 = JGraLab.map().plus("key one", recordDomainValue1).plus("key two", nullValue);	// TODO  "Can't add null to an ArrayPVector" Intended behavior?
	private static Object recordDomainValue2 = de.uni_koblenz.jgralab.impl.RecordImpl.empty()
			.plus("ListComponent", JGraLab.vector()
					.plus(JGraLab.map())
					.plus(JGraLab.map().plus(123, 4561l).plus(321, 6541l))
					.plus(JGraLab.map().plus(243, 4151l).plus(312, 6451l)))
			.plus("MapComponent", JGraLab.map()
					.plus(JGraLab.vector().plus("a").plus("b").plus("c"), JGraLab.map().plus(1, 123456l).plus(42, 435782l).plus(7, 832l).plus(8, 112345l))
					.plus(JGraLab.vector().plus("d").plus("e").plus("f"), JGraLab.map())
					.plus(JGraLab.vector(), JGraLab.map().plus(73, 192837l).plus(84, 46565l)))
			.plus("RecordComponent", recordDomainValue1)
			.plus("SetComponent", JGraLab.set()
					.plus(JGraLab.map().plus(123, 4561l).plus(321, 6541l))
					.plus(JGraLab.map().plus(243, 4151l).plus(312, 6451l)));
//	public static Object recordDomainValue3 = de.uni_koblenz.jgralab.impl.RecordImpl.empty()	// TODO Can't components in records be null? ArrayPVector prevents it!
//			.plus("ListComponent", null)
//			.plus("MapComponent", null)
//			.plus("RecordComponent", null)
//			.plus("SetComponent", null);
	
	
	@Test
	public void testSerializeAttribute() {
		try {
			GraphIO io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getBooleanDomain(), boolDomainValue1);
			assertEquals(serializedBoolDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getBooleanDomain(), boolDomainValue2);
			assertEquals(serializedBoolDomainValue2, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getIntegerDomain(), intDomainMaxValue);
			assertEquals(serializedIntDomainMaxValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getIntegerDomain(), intDomainMinValue);
			assertEquals(serializedIntDomainMinValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getIntegerDomain(), intDomainValue);
			assertEquals(serializedIntDomainValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getLongDomain(), longDomainMaxValue);
			assertEquals(serializedLongDomainMaxValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getLongDomain(), longDomainMinValue);
			assertEquals(serializedLongDomainMinValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getLongDomain(), longDomainValue);
			assertEquals(serializedLongDomainValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getDoubleDomain(), doubleDomainValue1);
			assertEquals(serializedDoubleDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getDoubleDomain(), doubleDomainValue2);
			assertEquals(serializedDoubleDomainValue2, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getDoubleDomain(), doubleDomainValue3);
			assertEquals(serializedDoubleDomainValue3, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getStringDomain(), stringDomainValue1);
			assertEquals(serializedStringDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, schema.getStringDomain(), stringDomainEmptyValue);
			assertEquals(serializedStringDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, enumDomain, enumDomainValue1);
			assertEquals(serializedEnumDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, enumDomain, enumDomainValue2);
			assertEquals(serializedEnumDomainValue2, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleSetDomain, setDomainValue1);
			assertEquals(serializedSetDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexSetDomain, setDomainValue2);
			assertEquals(serializedSetDomainValue2, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleSetDomain, setDomainEmptyValue);
			assertEquals(serializedSetDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexSetDomain, setDomainEmptyValue);
			assertEquals(serializedSetDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleListDomain, listDomainValue1);
			assertEquals(serializedListDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexListDomain, listDomainValue2);
			assertEquals(serializedListDomainValue2, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleListDomain, listDomainEmptyValue);
			assertEquals(serializedListDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexListDomain, listDomainEmptyValue);
			assertEquals(serializedListDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleMapDomain, mapDomainValue1);
			assertEquals(serializedMapDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexMapDomain, mapDomainValue2);
			assertEquals(serializedMapDomainValue2, io.getStringWriterResult());
//			io = GraphIO.createStringWriter(schema);
//			GenericUtil.serializeGenericAttribute(io, complexMapDomain2, mapDomainValue3);		// TODO see above
//			assertEquals(serializedMapDomainValue3, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleMapDomain, mapDomainEmptyValue);
			assertEquals(serializedMapDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexMapDomain, mapDomainEmptyValue);
			assertEquals(serializedMapDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexMapDomain2, mapDomainEmptyValue);
			assertEquals(serializedMapDomainEmptyValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleRecordDomain, recordDomainValue1);
			assertEquals(serializedRecordDomainValue1, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexRecordDomain, recordDomainValue2);
			assertEquals(serializedRecordDomainValue2, io.getStringWriterResult());
			
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, enumDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleSetDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexSetDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleListDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexListDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleMapDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexMapDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexMapDomain2, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, simpleRecordDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
			io = GraphIO.createStringWriter(schema);
			GenericUtil.serializeGenericAttribute(io, complexRecordDomain, nullValue);
			assertEquals(serializedNullValue, io.getStringWriterResult());
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (GraphIOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testParseAttribute() {
		try {
			Object result = GenericUtil.parseGenericAttribute(schema.getBooleanDomain(), GraphIO.createStringReader(serializedBoolDomainValue1, schema));
			assertEquals(boolDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(schema.getBooleanDomain(), GraphIO.createStringReader(serializedBoolDomainValue2, schema));
			assertEquals(boolDomainValue2, result);
			result = GenericUtil.parseGenericAttribute(schema.getIntegerDomain(), GraphIO.createStringReader(serializedIntDomainMinValue, schema));
			assertEquals(intDomainMinValue, result);
			result = GenericUtil.parseGenericAttribute(schema.getIntegerDomain(), GraphIO.createStringReader(serializedIntDomainMaxValue, schema));
			assertEquals(intDomainMaxValue, result);
			result = GenericUtil.parseGenericAttribute(schema.getIntegerDomain(), GraphIO.createStringReader(serializedIntDomainValue, schema));
			assertEquals(intDomainValue, result);
			result = GenericUtil.parseGenericAttribute(schema.getLongDomain(), GraphIO.createStringReader(serializedLongDomainMaxValue, schema));
			assertEquals(longDomainMaxValue, result);
			result = GenericUtil.parseGenericAttribute(schema.getLongDomain(), GraphIO.createStringReader(serializedLongDomainMinValue, schema));
			assertEquals(longDomainMinValue, result);
			result = GenericUtil.parseGenericAttribute(schema.getLongDomain(), GraphIO.createStringReader(serializedLongDomainValue, schema));
			assertEquals(longDomainValue, result);
			result = GenericUtil.parseGenericAttribute(schema.getDoubleDomain(), GraphIO.createStringReader(serializedDoubleDomainValue1, schema));
			assertEquals(doubleDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(schema.getDoubleDomain(), GraphIO.createStringReader(serializedDoubleDomainValue2, schema));
			assertEquals(doubleDomainValue2, result);
			result = GenericUtil.parseGenericAttribute(schema.getDoubleDomain(), GraphIO.createStringReader(serializedDoubleDomainValue3, schema));
			assertEquals(doubleDomainValue3, result);
			result = GenericUtil.parseGenericAttribute(schema.getStringDomain(), GraphIO.createStringReader(serializedStringDomainValue1, schema));
			assertEquals(stringDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(schema.getStringDomain(), GraphIO.createStringReader(serializedStringDomainEmptyValue, schema));
			assertEquals(stringDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(enumDomain, GraphIO.createStringReader(serializedEnumDomainValue1, schema));
			assertEquals(enumDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(enumDomain, GraphIO.createStringReader(serializedEnumDomainValue2, schema));
			assertEquals(enumDomainValue2, result);
			result = GenericUtil.parseGenericAttribute(simpleSetDomain, GraphIO.createStringReader(serializedSetDomainValue1, schema));
			assertEquals(setDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(complexSetDomain, GraphIO.createStringReader(serializedSetDomainValue2, schema));
			assertEquals(setDomainValue2, result);
			result = GenericUtil.parseGenericAttribute(simpleSetDomain, GraphIO.createStringReader(serializedSetDomainEmptyValue, schema));
			assertEquals(setDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(complexSetDomain, GraphIO.createStringReader(serializedSetDomainEmptyValue, schema));
			assertEquals(setDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(simpleListDomain, GraphIO.createStringReader(serializedListDomainValue1, schema));
			assertEquals(listDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(complexListDomain, GraphIO.createStringReader(serializedListDomainValue2, schema));
			assertEquals(listDomainValue2, result);
			result = GenericUtil.parseGenericAttribute(simpleListDomain, GraphIO.createStringReader(serializedListDomainEmptyValue, schema));
			assertEquals(listDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(complexListDomain, GraphIO.createStringReader(serializedListDomainEmptyValue, schema));
			assertEquals(listDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(simpleMapDomain, GraphIO.createStringReader(serializedMapDomainValue1, schema));
			assertEquals(mapDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(complexMapDomain, GraphIO.createStringReader(serializedMapDomainValue2, schema));
			assertEquals(mapDomainValue2, result);
//			result = GenericUtil.parseGenericAttribute(complexMapDomain2, GraphIO.createStringReader(serializedMapDomainValue3, schema));	// TODO see above
//			assertEquals(mapDomainValue3, result);
			result = GenericUtil.parseGenericAttribute(simpleMapDomain, GraphIO.createStringReader(serializedMapDomainEmptyValue, schema));
			assertEquals(mapDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(complexMapDomain, GraphIO.createStringReader(serializedMapDomainEmptyValue, schema));
			assertEquals(mapDomainEmptyValue, result);
			result = GenericUtil.parseGenericAttribute(simpleRecordDomain, GraphIO.createStringReader(serializedRecordDomainValue1, schema));
			assertEquals(recordDomainValue1, result);
			result = GenericUtil.parseGenericAttribute(complexRecordDomain, GraphIO.createStringReader(serializedRecordDomainValue2, schema));
			assertEquals(recordDomainValue2, result);
			
			result = GenericUtil.parseGenericAttribute(simpleSetDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(complexSetDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(simpleListDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(complexListDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(simpleMapDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(complexMapDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(complexMapDomain2, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(simpleRecordDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
			result = GenericUtil.parseGenericAttribute(complexRecordDomain, GraphIO.createStringReader(serializedNullValue, schema));
			assertEquals(nullValue, result);
		} catch (GraphIOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testConformsToDomain() {
		Domain[] testDomains = new Domain[] {
			schema.getBooleanDomain(),
			schema.getIntegerDomain(),
			schema.getLongDomain(),
			schema.getDoubleDomain(),
			schema.getStringDomain(),
			enumDomain,
			simpleSetDomain,
			complexSetDomain,
			simpleListDomain,
			complexListDomain,
			simpleMapDomain,
			complexMapDomain,
			complexMapDomain2,
			simpleRecordDomain,
			complexRecordDomain
		};

		try {
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 0) {
					assertTrue(GenericUtil.conformsToDomain(boolDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(boolDomainValue2, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(boolDomainValue1, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(boolDomainValue2, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 1) {
					assertTrue(GenericUtil.conformsToDomain(intDomainMaxValue, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(intDomainMinValue, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(intDomainValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(intDomainMaxValue, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(intDomainMinValue, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(intDomainValue, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 2) {
					assertTrue(GenericUtil.conformsToDomain(longDomainMaxValue, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(longDomainMinValue, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(longDomainValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(longDomainMaxValue, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(longDomainMinValue, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(longDomainValue, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 3) {
					assertTrue(GenericUtil.conformsToDomain(doubleDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(doubleDomainValue2, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(doubleDomainValue3, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(doubleDomainValue1, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(doubleDomainValue2, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(doubleDomainValue3, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 4) {
					assertTrue(GenericUtil.conformsToDomain(stringDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(stringDomainEmptyValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(stringDomainValue1, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(stringDomainEmptyValue, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 4 || i == 5) {	// Enum-values are represented by Strings in the generic implementation
					assertTrue(GenericUtil.conformsToDomain(enumDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(enumDomainValue2, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(enumDomainValue1, testDomains[i]));
					assertFalse(GenericUtil.conformsToDomain(enumDomainValue2, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 6) {
					assertTrue(GenericUtil.conformsToDomain(setDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(setDomainValue1, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 7) {
					assertTrue(GenericUtil.conformsToDomain(setDomainValue2, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(setDomainValue2, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 8) {
					assertTrue(GenericUtil.conformsToDomain(listDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(listDomainValue1, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 9) {
					assertTrue(GenericUtil.conformsToDomain(listDomainValue2, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(listDomainValue2, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 10) {
					assertTrue(GenericUtil.conformsToDomain(mapDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(mapDomainValue1, testDomains[i]));
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 11) {
					assertTrue(GenericUtil.conformsToDomain(mapDomainValue2, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					assertFalse(GenericUtil.conformsToDomain(mapDomainValue2, testDomains[i]));
				}
			}
//			for(int i = 0; i < testDomains.length; i++) {	// TODO see above
//				if(i == 12) {
//					assertTrue(GenericUtil.conformsToDomain(mapDomainValue3, testDomains[i]));
//					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
//				}
//				else {
//					assertFalse(GenericUtil.conformsToDomain(setDomainValue3, testDomains[i]));
//				}
//			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 13) {
					assertTrue(GenericUtil.conformsToDomain(recordDomainValue1, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					try {
						assertFalse(GenericUtil.conformsToDomain(recordDomainValue1, testDomains[i]));
					}
					catch(NoSuchAttributeException e) {} // Desired behavior, if a record has components it shouldn't have.
				}
			}
			for(int i = 0; i < testDomains.length; i++) {
				if(i == 14) {
					assertTrue(GenericUtil.conformsToDomain(recordDomainValue2, testDomains[i]));
					assertTrue(GenericUtil.conformsToDomain(nullValue, testDomains[i]));
				}
				else {
					try {
						assertFalse(GenericUtil.conformsToDomain(recordDomainValue2, testDomains[i]));
					}
					catch(NoSuchAttributeException e) {} // Desired behavior, if a record has components it shouldn't have.
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}
}