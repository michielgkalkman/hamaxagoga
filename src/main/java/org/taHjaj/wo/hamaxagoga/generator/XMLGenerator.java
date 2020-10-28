package org.taHjaj.wo.hamaxagoga.generator;

/*
 * Copyright 2008 Michiel Kalkma�n
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.math3.util.Precision;
import org.apache.xerces.impl.xpath.regex.RegexGenerator;
import org.apache.xerces.impl.xpath.regex.XMLChar;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.support.XSElementDeclSupport;
import org.taHjaj.wo.hamaxagoga.support.XSSupport;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class XMLGenerator {
	private int prefixCount = 0;

	private Map<String, String> predefinedNamespacePrefixes;

	// Record which prefix is used for a given namespace.
	// For now, reuse when possible.
	private Map<String, String> namespaceURI2prefix = new HashMap<String, String>();

	// private Writer writer;
	private CountingOutputStream countingOutputStream;

	private final Params params;

	private final Random random;

	private final long maxFileSize;

	private boolean fQuitting = false;

	public XMLGenerator(final Params params) {
		super();

		this.params = params;
		this.random = params.getRandom();

		this.maxFileSize = params.getMaxFileSize();
		this.predefinedNamespacePrefixes = params
				.getPredefinedNamespacePrefixes();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				fQuitting = true;
			}
		});
	}

	public void createXMLDocument(final OutputStream outputStream, final XSModel xsmodel)
			throws SAXException, HamaxagogaException, IOException {

		final XSNamedMap namedMap = getNamedMap( xsmodel);
		final int nrRootElements = namedMap.getLength();
		if (nrRootElements == 0) {
			throw new HamaxagogaException(
					"There is no root element available in the provided XML schemas"
							+ " to create XML instances from");
		}

		countingOutputStream = null;
		try {
			countingOutputStream = new CountingOutputStream(  outputStream);
			final String rootElementName = params.getRootElementName();
			final XMLSerializer serializer = getXMLSerializer(countingOutputStream);
			if (StringUtils.isBlank(rootElementName)) {
				processRandomRootElement(serializer, namedMap);
			} else {
				processRootElement(serializer, params.getRootElementName(),
						namedMap);
			}
		} finally {
			if (countingOutputStream != null) {
				countingOutputStream.close();
			}
		}
	}

	/**
	 * 
	 * @param serializer
	 * @param namedMap
	 *            a non-empty map of root elements.
	 * @throws SAXException
	 * @throws HamaxagogaException
	 */
	private void processRandomRootElement(final XMLSerializer serializer,
			final XSNamedMap namedMap) throws SAXException, HamaxagogaException, IOException	 {
		final int nrRootElements = namedMap.getLength();
		int rootElementIndex = random.nextInt(nrRootElements);

		final XSElementDecl elementDecl = (XSElementDecl) namedMap
				.item(rootElementIndex);
		processRootElement(serializer, elementDecl.getName(), namedMap);
	}

	private void checkFile() throws HamaxagogaException {
		try {
			getCountingOutputStream().flush();
		} catch (final IOException exception) {
			throw new HamaxagogaException( exception);
		}
		final long byteCount = getCountingOutputStream().getByteCount();
		if (!fQuitting && maxFileSize > 0 && byteCount > maxFileSize) {
			fQuitting = true;
		}
		if (log.isDebugEnabled()) {
			log.debug("quitting = " + fQuitting + ",maxfilesize = "
					+ maxFileSize + ",filelength = " + byteCount + ",filelength = " + getCountingOutputStream().getCount());
		}
	}

	private XMLSerializer getXMLSerializer(final OutputStream outputStream) {
		final OutputFormat outputFormat = new OutputFormat("XML", params.getEncoding(), true); // Serialize

		final XMLSerializer serializer = new XMLSerializer(outputStream,
				outputFormat);
		return serializer;
	}

	private void processRootElement(final XMLSerializer serializer,
			final String rootElementName, final XSNamedMap namedMap)
			throws HamaxagogaException, SAXException, IOException {

		XSElementDecl elementDecl = null;

		final int length = namedMap.getLength();
		{
			for (int i = 0; i < length; i++) {
				XSObject object = namedMap.item(i);
				if (rootElementName.equals(object.getName())) {
					final String namespace = object.getNamespace();

					elementDecl = (XSElementDecl) namedMap.itemByName(
							namespace, rootElementName);

					if (elementDecl != null) {
						serializer.startDocument();
						serializer.comment( "Generated with Hamaxagoga");
						xmlSchemaObjects.push(new XSElementDeclSupport(
								elementDecl));
						processStack(serializer);
						serializer.endDocument();
						break;
					}
				}
			}
		}

		if (elementDecl == null) {
			final StringBuffer rootElements = new StringBuffer();

			rootElements.append("(the following rootElements are available: ");
			boolean fFirst = true;
			for (int i = 0; i < length; i++) {
				if (!fFirst) {
					rootElements.append(", ");
				}
				rootElements.append(namedMap.item(i));
				fFirst = false;
			}
			rootElements.append(')');

			final String errorMsg = "'" + rootElementName
					+ "' is not a rootelement " + rootElements.toString();

			log.error(errorMsg);
			throw new HamaxagogaException(errorMsg);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// Stack stuff
	// ////////////////////////////////////////////////////////////////////////////////////////
	private final Stack<XSSupport> xmlSchemaObjects = new Stack<XSSupport>();

	private void processStack(final XMLSerializer serializer)
			throws SAXException, HamaxagogaException {
		while (!xmlSchemaObjects.isEmpty()) {
			final XSSupport support = xmlSchemaObjects.peek();
			support.process(serializer, this);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// Stack stuff END - END - END - END - END - END - END - END - END - END -
	// END - END
	// ////////////////////////////////////////////////////////////////////////////////////////

	private XSNamedMap getNamedMap( final XSModel xsmodel) throws  HamaxagogaException {
		if (xsmodel == null) {
			final String errorMsg = "Could not generate a schema model from "
					+ params.getXsds();
			log.error(errorMsg);
			throw new HamaxagogaException(errorMsg);
		}

		return xsmodel.getComponents(XSConstants.ELEMENT_DECLARATION);
	}

	public static XSModel getXsModel( final Params params) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		System.setProperty(DOMImplementationRegistry.PROPERTY,
				"org.apache.xerces.dom.DOMXSImplementationSourceImpl");
		final DOMImplementationRegistry registry = DOMImplementationRegistry
				.newInstance();

		final XSImplementation impl = (XSImplementation) registry
				.getDOMImplementation("XS-Loader");

		final XSLoader schemaLoader = impl.createXSLoader(null);

		final XsdStringList xsdStringList = new XsdStringList(params.getXsds());

		final XSModel xsmodel = schemaLoader.loadURIList(xsdStringList);
		
		return xsmodel;
	}

	public String generatePrefix(final String namespaceURI) {
		final String prefix;
		if (predefinedNamespacePrefixes.containsKey(namespaceURI)) {
			prefix = predefinedNamespacePrefixes.get(namespaceURI);
		} else if (namespaceURI2prefix.containsKey(namespaceURI)) {
			// NOTE: we could elect to randomly select whether
			// a new prefix should be generated instead of using an old
			// one, but that does not seem to work always ...
			prefix = namespaceURI2prefix.get(namespaceURI);
		} else {
			prefixCount++;
			prefix = "ns" + prefixCount;
		}
		return prefix;
	}

	private void toSerializer(final XMLSerializer serializer, final String value)
			throws SAXException {
		log.debug("SERIALIZE: characters");
		serializer.characters(value.toCharArray(), 0, value.length());
	}

	public void processSimpleContent(final XMLSerializer serializer,
			final XSComplexTypeDefinition complexTypeDefinition)
			throws SAXException {
		{
			final XSSimpleTypeDefinition simpleTypeDefinition = complexTypeDefinition
					.getSimpleType();

			toSerializer(serializer,
					processSimpleTypeDefinition(simpleTypeDefinition));
		}
	}

	public Attributes getAttributes(final XSObjectList objectList) {

		final Map<String, String> namespaceURI2prefix = new HashMap<String, String>();
		
		final AttributesImpl attributesImpl;

		if (objectList.getLength() > 0) {

			attributesImpl = new AttributesImpl();

			for (int i = 0; i < objectList.getLength(); i++) {
				final XSAttributeUse attributeUse = (XSAttributeUse) objectList
						.item(i);

				if (attributeUse.getRequired() || random.nextBoolean()) {
					final XSAttributeDeclaration attributeDeclaration = attributeUse
							.getAttrDeclaration();

					final XSSimpleTypeDefinition simpleTypeDefinition = attributeDeclaration
							.getTypeDefinition();

					final String localName = attributeDeclaration.getName();

					final String value;
					if( attributeUse.getConstraintValue() != null) {
						value = attributeUse.getConstraintValue();
					} else {
						value = processSimpleTypeDefinition(simpleTypeDefinition);
					}
					if (value != null) {
						final String type = attributeDeclaration
								.getTypeDefinition().getName();

						final String namespaceURI;
						{
							final String _namespaceURI = attributeDeclaration
									.getNamespace();
							if (_namespaceURI == null) {
								final XSComplexTypeDefinition complexTypeDefinition = attributeDeclaration
										.getEnclosingCTDefinition();
								if (complexTypeDefinition == null) {
									namespaceURI = null;
								} else {
									// @TODO: check this !
									namespaceURI = null; // complexTypeDefinition.getNamespace();
								}
							} else {
								namespaceURI = _namespaceURI;
							}
						}

						final String qName;
						if (namespaceURI == null) {
							qName = localName;
						} else {
							if( namespaceURI2prefix.containsKey( namespaceURI)) {
								qName = namespaceURI2prefix.get( namespaceURI) + ':' + localName;
							} else {
								final String prefix = generatePrefix(namespaceURI);
								if (prefix == null) {
									qName = localName;
								} else {
									qName = prefix + ':' + localName;
								}
								namespaceURI2prefix.put( namespaceURI, prefix);
							}
						}

						attributesImpl.addAttribute(namespaceURI, localName,
								qName, type, value);
					}
				}
			}
		} else {
			attributesImpl = null;
		}

		return attributesImpl;
	}

	public String processSimpleTypeDefinition(
			final XSSimpleTypeDefinition simpleTypeDefinition)
			throws HamaxagogaException {
		final XSObjectList members = simpleTypeDefinition.getMemberTypes();

		final String valuePrimitiveDatatype;
		if (members != null && members.getLength() > 0) {
			final int chance = random.nextInt(members.getLength());

			valuePrimitiveDatatype = processSimpleTypeDefinition((XSSimpleTypeDefinition) members
					.item(chance));
		} else {
			valuePrimitiveDatatype = processPrimitiveDatatype(simpleTypeDefinition);
		}

		return valuePrimitiveDatatype;
	}

	private String processPrimitiveDatatype(
			final XSSimpleTypeDefinition simpleTypeDefinition)
			throws HamaxagogaException {
		final XSObjectList objectList = simpleTypeDefinition.getFacets();
		final Facet facet = new Facet(objectList);
		return processPrimitiveDatatype(simpleTypeDefinition,
				simpleTypeDefinition, facet);
	}

	private String processPrimitiveDatatype(
			final XSSimpleTypeDefinition simpleTypeDefinition,
			final XSTypeDefinition typeDefinition, final Facet facet) {
		final String value;

		final String name = typeDefinition.getName();

		if ( typeDefinition.getBaseType() == null) {
			// appearently, some attributes don't require types.
			// Just return "" and cross our fingers.
			value = "";
		} else if ("string".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#string
			value = getStringValue(simpleTypeDefinition, facet);
		} else if ("boolean".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#boolean
			value = getBooleanValue();
		} else if ("decimal".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#decimal
			value = getDecimalValue(simpleTypeDefinition, facet);
		} else if ("precisionDecimal".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#precisionDecimal
			// http://www.w3.org/TR/xmlschema11-2/#precisionDecimal
			throw new NotImplementedException(
					"Not implemented: precisionDecimal (http://www.w3.org/TR/xmlschema11-2/#precisionDecimal)");
		} else if ("float".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#float
			value = getFloatValue();
		} else if ("double".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#double
			value = getDoubleValue();
		} else if ("duration".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#duration
			value = getDurationValue();
		} else if ("dateTime".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#dateTime
			value = getDateTimeValue();
		} else if ("time".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#time
			value = getTimeValue();
		} else if ("date".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#date
			value = getDateValue();
		} else if ("gYearMonth".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#gYearMonth
			value = getYearMonthValue();
		} else if ("gYear".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#gYear
			value = getYearValue();
		} else if ("gMonthDay".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#gMonthDay
			value = getMonthDayValue();
		} else if ("gDay".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#gDay
			value = getDayValue();
		} else if ("gMonth".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#gMonth
			value = getMonthValue();
		} else if ("hexBinary".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#hexBinary
			value = getHexBinaryValue(simpleTypeDefinition, facet);
		} else if ("base64Binary".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#base64Binary
			value = getBase64BinaryValue();
		} else if ("anyURI".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#anyURI
			value = getURIValue();
		} else if ("QName".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#QName
			value = getQNameValue();
		} else if ("NOTATION".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#NOTATION
			value = getNotationValue();
		} else if ("normalizedString".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#normalizedString
			value = getNormalizedStringValue(simpleTypeDefinition, facet);
		} else if ("token".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#token
			value = getTokenValue(simpleTypeDefinition, facet);
		} else if ("language".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#language
			value = getLanguageValue(simpleTypeDefinition, facet);
		} else if ("NMTOKEN".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#NMTOKEN
			value = getNMTOKENValue(simpleTypeDefinition, facet);
		} else if ("NMTOKENS".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#NMTOKENS
			throw new NotImplementedException(
					"Not implemented: NMTOKENS (http://www.w3.org/TR/xmlschema11-2/#NMTOKENS)");
		} else if ("Name".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#Name
			value = getNameValue( simpleTypeDefinition, facet);
		} else if ("NCName".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#NCName
			throw new NotImplementedException(
					"Not implemented: NCName (http://www.w3.org/TR/xmlschema11-2/#NCName)");
		} else if ("ID".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#ID
			value = getIDValue();
		} else if ("IDREF".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#IDREF
			value = getIDRefValue();
			// throw new NotImplementedException( "Not implemented: IDREF
			// (http://www.w3.org/TR/xmlschema11-2/#IDREF)");
		} else if ("IDREFS".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#IDREFS
			throw new NotImplementedException(
					"Not implemented: IDREFS (http://www.w3.org/TR/xmlschema11-2/#IDREFS)");
		} else if ("ENTITY".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#ENTITY
			throw new NotImplementedException(
					"Not implemented: ENTITY (http://www.w3.org/TR/xmlschema11-2/#ENTITY)");
		} else if ("ENTITIES".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#ENTITIES
			throw new NotImplementedException(
					"Not implemented: ENTITIES (http://www.w3.org/TR/xmlschema11-2/#ENTITIES)");
		} else if ("integer".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#integer
			value = getIntegerValue(simpleTypeDefinition, facet);
		} else if ("nonPositiveInteger".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#nonPositiveInteger
			value = getNonPositiveIntegerValue( simpleTypeDefinition, facet);
		} else if ("negativeInteger".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#negativeInteger
			value = getNegativeIntegerValue( simpleTypeDefinition, facet);
		} else if ("long".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#long
			return getLongValue( simpleTypeDefinition, facet);
		} else if ("int".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#int
			return getIntValue( simpleTypeDefinition, facet);
		} else if ("short".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#short
			return getShortValue( simpleTypeDefinition, facet);
		} else if ("byte".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#byte
			value = getByteValue( simpleTypeDefinition, facet);
		} else if ("nonNegativeInteger".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#nonNegativeInteger
			value = getNonNegativeInteger(simpleTypeDefinition, facet);
		} else if ("unsignedLong".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#unsignedLong
			value = getUnsignedLongValue( simpleTypeDefinition, facet);
		} else if ("unsignedInt".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#unsignedInt
			value = getUnsignedIntValue( simpleTypeDefinition, facet);
		} else if ("unsignedShort".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#unsignedShort
			value = getUnsignedByteValue( simpleTypeDefinition, facet);
		} else if ("unsignedByte".equals(name)) {
			// http://www.w3.org/TR/xmlschema11-2/#unsignedByte
			value = getUnsignedByteValue( simpleTypeDefinition, facet);
		} else if ("positiveInteger".equals(name)) {
			// http://www.w3.org/TR/xmlschema-2#integer
			value = getPositiveIntegerValue(simpleTypeDefinition, facet);
		} else if ("yearMonthDuration".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#yearMonthDuration
			throw new NotImplementedException(
					"Not implemented: yearMonthDuration (http://www.w3.org/TR/xmlschema11-2/#yearMonthDuration)");
		} else if ("dayTimeDuration".equals(name)) {
			// @TODO http://www.w3.org/TR/xmlschema11-2/#dayTimeDuration
			throw new NotImplementedException(
					"Not implemented: dayTimeDuration (http://www.w3.org/TR/xmlschema11-2/#dayTimeDuration)");
		} else {

			log.debug("Not a primitive datatype: " + name);
			final XSTypeDefinition baseType = typeDefinition.getBaseType();

			value = processPrimitiveDatatype(simpleTypeDefinition, baseType,
					facet);
		}

		return value;
	}
	
	private String getNonNegativeIntegerValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getIntegerValue( simpleTypeDefinition, facet);
	}	
	
	private String getUnsignedLongValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getNonNegativeIntegerValue( simpleTypeDefinition, facet);
	}
	
	private String getUnsignedIntValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getUnsignedLongValue( simpleTypeDefinition, facet);
	}
	
	private String getUnsignedShortValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getUnsignedIntValue( simpleTypeDefinition, facet);
	}
	
	private String getUnsignedByteValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getUnsignedShortValue( simpleTypeDefinition, facet);
	}
	
	private String getByteValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getShortValue( simpleTypeDefinition, facet);
	}

	private String getShortValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getIntValue( simpleTypeDefinition, facet);
	}

	private String getIntValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getLongValue( simpleTypeDefinition, facet);
	}

	private String getLongValue( 
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getIntegerValue( simpleTypeDefinition, facet);
	}

	private XSTypeDefinition getBaseType(final XSTypeDefinition typeDefinition) {
		final XSTypeDefinition baseType = typeDefinition.getBaseType();
		if ( "anySimpleType".equals(baseType.getName())) {
			return typeDefinition;
		}

		return baseType;
	}

	private static final FastDateFormat ISO_8601_YEAR_MONTH = FastDateFormat
			.getInstance("yyyy-MM");

	private static final FastDateFormat ISO_8601_YEAR = FastDateFormat
			.getInstance("yyyy");

	private static final FastDateFormat ISO_8601_MONTH_DAY = FastDateFormat
			.getInstance("--MM-dd");

	private static final FastDateFormat ISO_8601_DAY = FastDateFormat
			.getInstance("---dd");

	private static final FastDateFormat ISO_8601_MONTH = FastDateFormat
			.getInstance("--MM");

	private String getMonthValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = ISO_8601_MONTH.format(gregorianCalendar);
		return value;
	}

	private final long firstLight = new GregorianCalendar( 1, 0, 0).getTimeInMillis();
	private final long lastLight = new GregorianCalendar( 9999, 0, 0).getTimeInMillis();
	private final long era = lastLight - firstLight;
	
	private GregorianCalendar getRandomCalendar() {
		final GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTimeInMillis( Math.abs( firstLight + (long)random.nextInt( Math.abs((int)era))));
		return gregorianCalendar;
	}

	private String getDayValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = ISO_8601_DAY.format(gregorianCalendar);
		return value;
	}

	private String getMonthDayValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = ISO_8601_MONTH_DAY.format(gregorianCalendar);
		return value;
	}

	private String getYearValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = ISO_8601_YEAR.format(gregorianCalendar);
		return value;
	}

	private String getYearMonthValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = ISO_8601_YEAR_MONTH.format(gregorianCalendar);
		return value;
	}

	private String getTimeValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(gregorianCalendar);
		return value;
	}

	private String getDateValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		final GregorianCalendar gregorianCalendar = getRandomCalendar();

		value = DateFormatUtils.ISO_DATE_FORMAT.format(gregorianCalendar);
		return value;
	}

	private String getDateTimeValue() {
		final String value;
		// http://www.w3.org/TR/xmlschema-2/#date
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		DatatypeFactory datatypeFactory;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (final DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		
		final GregorianCalendar gregorianCalendar = getRandomCalendar();
		
		final XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory
				.newXMLGregorianCalendar( gregorianCalendar);

		value = xmlGregorianCalendar.toXMLFormat();
		return value;
	}

	private String getDurationValue() {
		final String value;
		System.setProperty(DatatypeFactory.DATATYPEFACTORY_PROPERTY,
				DatatypeFactoryImpl.class.getName());
		DatatypeFactory datatypeFactory;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (final DatatypeConfigurationException datatypeConfigurationException) {
			// TODO Auto-generated catch block
			log.error(datatypeConfigurationException);
			throw new HamaxagogaException(datatypeConfigurationException);
		}
		// @TODO more options ...
		// There appears to be an upperlimit for durations ...
		// So, we use a still large number to generate one.
		final Duration duration = datatypeFactory.newDuration(Math.abs(random
				.nextInt(1000000000)));

		value = duration.toString();
		return value;
	}

	private int counter;

	private String id;

	private String getIDValue() {
		final String value;
		value = "a" + counter;
		counter++;
		id = value;
		return value;
	}

	private String getIDRefValue() {
		final String value;
		if (id == null) {
			value = null;
		} else {
			value = id;
		}
		return value;
	}

	private String getDoubleValue() {
		final String value;
		value = getFloatValue();
		return value;
	}

	private String getFloatValue() {
		final String value;
		final String[] options = { "1E4", "1267.43233E12", "12.78e-2", "12",
				"-0", "0", "INF" };
		value = options[random.nextInt(options.length)];
		return value;
	}

	private String getBooleanValue() {
		return BooleanUtils.toStringTrueFalse(random.nextBoolean());
	}

	private String getPositiveIntegerValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getNonNegativeInteger( simpleTypeDefinition, facet);
	}

	private String getNonNegativeInteger(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getIntegerValue( simpleTypeDefinition, facet);
	}

	private String getDecimalValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		final String value;
		// http://www.w3.org/TR/xmlschema-2#decimal

		// Future inspiration
		// final String[] options = { "-1.23", "12678967.543233",
		// "+100000.00", "210" };
		// value = options[random.nextInt(options.length)];
		if( StringUtils.isEmpty( facet.getMinExclusive()) &&
				StringUtils.isEmpty( facet.getMinInclusive()) &&
				StringUtils.isEmpty( facet.getMaxInclusive()) &&
				StringUtils.isEmpty( facet.getMaxExclusive()) &&
				facet.getTotalDigits() > 0 &&
				facet.getFractionDigits() > 0) {
			// How many fraction digits ?
			final int nrFractionDigits = random.nextInt(facet.getFractionDigits() + 1);
			final int nrNonFractionDigits = 
					nrFractionDigits == 0 ?
					// -. is illegal; -3. is legal
					random.nextInt( facet.getTotalDigits() + 1)
					: random.nextInt( facet.getTotalDigits() - nrFractionDigits + 1);
			
			final String prefix;
			switch( random.nextInt(3)) {
				case 0: prefix = "-"; break;
				case 1: prefix = "+"; break;
				default: prefix = ""; break;
			}

			final String leadingZeroes = StringUtils.repeat( "0", random.nextInt( params.getLeadingZeroes()));
			final String trailingZeroes = StringUtils.repeat( "0", random.nextInt( params.getTrailingZeroes()));
			
			final String decimal;
			
			if( nrNonFractionDigits + nrFractionDigits == 0) {
				if( random.nextBoolean()) {
					// No fraction part.
					decimal = prefix + "0" + leadingZeroes + (random.nextBoolean() ? "." : "");
				} else {
					// with fraction part
					decimal = prefix + leadingZeroes + "." + "0" + trailingZeroes;
				} 
			} else if( nrFractionDigits == 0) {
				final String leadingPart = RandomStringUtils.randomNumeric( nrNonFractionDigits);
				if( random.nextBoolean()) {
					// No fraction part.
					decimal = prefix
								+ leadingZeroes
								+ leadingPart;
				} else {
					// with fraction part
					decimal = prefix
								+ leadingZeroes
								+ leadingPart
								+ "."
								+ RandomStringUtils.randomNumeric( nrFractionDigits)
								+ trailingZeroes;
				}
			} else {
				final String leadingPart = RandomStringUtils.randomNumeric( nrNonFractionDigits);
				decimal = prefix
						+ leadingZeroes
						+ leadingPart
						+ "."
						+ RandomStringUtils.randomNumeric( nrFractionDigits)
						+ trailingZeroes;
			}

			return decimal;
		}
		

		final StringList lexicalEnumerations = simpleTypeDefinition
				.getLexicalEnumeration();
		if (lexicalEnumerations != null && lexicalEnumerations.getLength() > 0) {
			value = getRandomEnumeration(lexicalEnumerations);
		} else {
			value = getDecimalValue(facet);
		}
		return value;
	}

	public String getDecimalValue(final Facet facet) {
		final String value;
		final int fractionDigits = facet.getFractionDigits();
		if (fractionDigits > 0) {
			// including '.'
			value = getDoubleValue(facet, fractionDigits);
		} else {
			value = getIntegerValue(facet);
		}
		return value;
	}

	private String getRandomEnumeration(final StringList lexicalEnumerations) {
		final String value;
		final int size = lexicalEnumerations.getLength();
		value = lexicalEnumerations.item(random.nextInt(size));
		return value;
	}

	private int getRandomLength(final Facet facet) {
		final int maxLength = (facet.getMaxLength() == 0 ? params
				.getMaxStringLength() : (facet.getMaxLength() > params
				.getMaxStringLength() ? params.getMaxStringLength() : facet
				.getMaxLength()));
		final int minLength = (facet.getMinLength() > params
				.getMinStringLength() ? facet.getMinLength() : params
				.getMinStringLength());

		final int randomLength;

		if (minLength == maxLength) {
			randomLength = random.nextInt() + minLength;
		} else {
			randomLength = random.nextInt(maxLength - minLength + 1)
					+ minLength;
		}

		return randomLength;
	}

	private String getIntegerValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {

		final String value;
		// http://www.w3.org/TR/xmlschema-2#integer

		final StringList lexicalEnumerations = simpleTypeDefinition
				.getLexicalEnumeration();
		if (lexicalEnumerations != null && lexicalEnumerations.getLength() > 0) {
			value = getRandomEnumeration(lexicalEnumerations);
		} else {
			final StringList stringList = simpleTypeDefinition
					.getLexicalPattern();
			if (stringList != null && stringList.getLength() > 1) {
				// Pattern.
				// Somehow, the last one, is the one we don't want.
				final String lexicalPattern = stringList.item(random
						.nextInt(stringList.getLength() - 1));

				value = getRandomString(lexicalPattern);
			} else {
				value = getIntegerValue(facet);
			}
		}

		return value;
	}

	public String getDoubleValue(final Facet facet, final int fractionDigits) {
		final String value;

		double min = -9999;
		double max = 9999;

		final String minExclusive = facet.getMinExclusive();

		if (minExclusive != null) {
			min = Double.parseDouble(minExclusive) + 1;
		} else {
			final String minInclusive = facet.getMinInclusive();
			if (minInclusive != null) {
				min = Double.parseDouble(minInclusive);
			}
		}

		final String maxExclusive = facet.getMaxExclusive();
		if (maxExclusive != null) {
			max = Double.parseDouble(maxExclusive) - 1;
		} else {
			final String maxInclusive = facet.getMaxInclusive();
			if (maxInclusive != null) {
				max = Double.parseDouble(maxInclusive);
			}
		}

		final double doubleValue = min + random.nextDouble() * (max - min);
		final double roundedDoubleValue = Precision.round(doubleValue, fractionDigits, RoundingMode.HALF_UP.ordinal());
		value = new BigDecimal(Double.toString(roundedDoubleValue)).toPlainString();

		return value;
	}

	private String getIntegerValue(final Facet facet) {
		int min = -9999;
		int max = 9999;

		return getIntegerValue(facet, min, max);
	}

	private String getIntegerValue(final Facet facet, int min, int max) {
		final String value;
		
		if (facet.getMinExclusive() != null) {
			min = Integer.parseInt(facet.getMinExclusive()) + 1;
		} else if (facet.getMinInclusive() != null) {
			min = (int) Double.parseDouble(facet.getMinInclusive());
		}

		if (facet.getMaxExclusive() != null) {
			max = Integer.parseInt(facet.getMaxExclusive()) - 1;
		} else if (facet.getMaxInclusive() != null) {
			max = (int) Double.parseDouble(facet.getMaxInclusive());
		}

		if (facet.getTotalDigits() > 0) {
			final int minGivenTotalDigits = (int) -(Math.pow(10.0, facet
					.getTotalDigits())) + 1;

			if (min < minGivenTotalDigits) {
				min = minGivenTotalDigits;
			}

			final int maxGivenTotalDigits = (int) (Math.pow(10.0, facet
					.getTotalDigits())) - 1;

			if (max > maxGivenTotalDigits) {
				max = maxGivenTotalDigits;
			}
		}

		value = Integer.toString(((int) (min + Math.floor(random.nextDouble()
				* (max - min)))));

		return value;
	}

	private String getStringValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		// http://www.w3.org/TR/xmlschema-2/#string
		final String value;

		final StringList stringList = simpleTypeDefinition.getLexicalPattern();
		if (stringList != null && stringList.getLength() > 0) {
			value = getRandomString(stringList);
		} else {
			final StringList lexicalEnumerations = simpleTypeDefinition
					.getLexicalEnumeration();
			if (lexicalEnumerations != null
					&& lexicalEnumerations.getLength() > 0) {
				value = getRandomEnumeration(lexicalEnumerations);
			} else {
				final int size;
				if (facet.getMaxLength() > 0) {
					size = random.nextInt(facet.getMaxLength()
							- facet.getMinLength()+1)
							+ facet.getMinLength();
				} else {
					size = getRandomLength(facet);
				}
				value = getRandomString( params.getLexicalPattern() + "{" + size + "}");
			}
		}
		return value;
	}

	private String getRandomString(final StringList stringList) {
		final String lexicalPattern = stringList.item(random.nextInt(stringList
				.getLength()));

		if ("[\\i-[:]][\\c-[:]]*".equals(lexicalPattern)) {
			// Regex cannot handle the above regular expression.
			// Use one that will succeed.
			return getRandomString("[#x0041-#x005A]");
		}

		return getRandomString(lexicalPattern);
	}

	// Map lexical pattern on regexgenerator
	private static final Map<String, RegexGenerator> regexGenerators = new HashMap<String, RegexGenerator>();

	private String getRandomString(final String lexicalPattern) {
		final String value;

		final RegexGenerator regexGenerator;
		RegexGenerator tmpRegexGenerator = regexGenerators.get(lexicalPattern);
		if (tmpRegexGenerator != null) {
			regexGenerator = tmpRegexGenerator;
		} else {
			regexGenerator = new RegexGenerator(random, lexicalPattern);
			regexGenerators.put(lexicalPattern, regexGenerator);
		}

		value = regexGenerator.generateXMLString();
		return value;
	}

	private String getHexBinaryValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		final String value;

		final StringList stringList = simpleTypeDefinition.getLexicalPattern();
		if (stringList != null && stringList.getLength() > 0) {
			value = getRandomString(stringList);
		} else {
			final StringList lexicalEnumerations = simpleTypeDefinition
					.getLexicalEnumeration();
			if (lexicalEnumerations != null
					&& lexicalEnumerations.getLength() > 0) {
				value = getRandomEnumeration(lexicalEnumerations);
			} else {
				final int length = facet.getLength();

				final int selectedLength;
				if (length > 0) {
					selectedLength = length;
				} else {
					selectedLength = getRandomLength(facet);
				}

				final StringBuffer stringBuffer = new StringBuffer(
						selectedLength * 2);

				for (int i = 0; i < selectedLength; i++) {
					stringBuffer.append(getRandomString("[0-9a-fA-F]{2,2}"));
				}

				value = stringBuffer.toString();
			}
		}

		return value;
	}

	private String getBase64BinaryValue() {
		return new String(Base64.encodeBase64("0FB7".getBytes()));
	}

	private String getURIValue() {
		return "http://not/random/yet";
	}

	private String getQNameValue() {
		return "ns1:QName";
	}

	private String getNotationValue() {
		// http://www.w3.org/TR/xmlschema11-2/#NOTATION
		// NOTATION is not supported by Hamaxagoga, because only
		// datatypes that are derived from NOTATION by specifying
		// a value for �enumeration� can be used in a schema.
		throw new NotImplementedException(
				"Notations (http://www.w3.org/TR/xmlschema-2/#NOTATION) are not supported.");
	}

	/**
	 * {@linktourl http://www.w3.org/TR/xmlschema11-2/#normalizedString}
	 * 
	 * @return
	 */
	private String getNormalizedStringValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		final String value;

		final StringList stringList = simpleTypeDefinition.getLexicalPattern();
		if (stringList != null && stringList.getLength() > 0) {
			value = getRandomString(stringList);
		} else {
			final StringList lexicalEnumerations = simpleTypeDefinition
					.getLexicalEnumeration();
			if (lexicalEnumerations != null
					&& lexicalEnumerations.getLength() > 0) {
				value = getRandomEnumeration(lexicalEnumerations);
			} else {
				if (facet.getMaxLength() > 0) {
					int chance = random.nextInt(facet.getMaxLength()
							- facet.getMinLength())
							+ facet.getMinLength();
					value = XMLChar.random(random, chance);
				} else {
					value = "";
				}
			}
		}
		return value;
	}

	/**
	 * {@linktourl }http://www.w3.org/TR/xmlschema11-2/#negativeInteger}
	 * 
	 * @return
	 */
	private String getNegativeIntegerValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getNonPositiveIntegerValue( simpleTypeDefinition, facet);
	}

	/**
	 * {@linktourl http://www.w3.org/TR/xmlschema11-2/#nonPositiveInteger}
	 *
	 * @return
	 */
	private String getNonPositiveIntegerValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getIntegerValue( simpleTypeDefinition, facet);
	}
	
	/**
	 * {@linktourl http://www.w3.org/TR/xmlschema11-2/#token}
	 * 
	 * @return
	 */
	private String getTokenValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		final String value;

		final StringList stringList = simpleTypeDefinition.getLexicalPattern();
		if (stringList != null && stringList.getLength() > 0) {
			value = getRandomString(stringList);
		} else {
			final StringList lexicalEnumerations = simpleTypeDefinition
					.getLexicalEnumeration();
			if (lexicalEnumerations != null
					&& lexicalEnumerations.getLength() > 0) {
				value = getRandomEnumeration(lexicalEnumerations);
			} else {
				if (facet.getMaxLength() > 0) {
					int chance = random.nextInt(facet.getMaxLength()
							- facet.getMinLength())
							+ facet.getMinLength();
					value = XMLChar.random(random, chance);
				} else {
					value = "";
				}
			}
		}
		return value;
	}

	/**
	 * {@linktourl http://www.w3.org/TR/xmlschema11-2/#language}
	 * @todo implement all facets
	 * @return
	 */
	private String getLanguageValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		final String value;

		value = getRandomString("[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");

		return value;
	}

	
	/**
	 * {@linktourl http://www.w3.org/TR/xmlschema11-2/#name}
	 * 
	 * @return
	 */
	private String getNameValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		return getTokenValue( simpleTypeDefinition, facet);
	}
	
	/**
	 * {@linktourl http://www.w3.org/TR/xmlschema11-2/#token}
	 * 
	 * @return
	 */
	private String getNMTOKENValue(
			final XSSimpleTypeDefinition simpleTypeDefinition, final Facet facet) {
		final String value;

		final StringList stringList = simpleTypeDefinition.getLexicalPattern();
		if (stringList != null && stringList.getLength() > 0) {
			value = getRandomString(stringList);
		} else {
			final StringList lexicalEnumerations = simpleTypeDefinition
					.getLexicalEnumeration();
			if (lexicalEnumerations != null
					&& lexicalEnumerations.getLength() > 0) {
				value = getRandomEnumeration(lexicalEnumerations);
			} else {
				if (facet.getMaxLength() > 0) {
					int chance = random.nextInt(facet.getMaxLength()
							- facet.getMinLength())
							+ facet.getMinLength();
					value = XMLChar.random(random, chance);
				} else {
					value = "";
				}
			}
		}
		return value;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	private CountingOutputStream getCountingOutputStream() {
		return countingOutputStream;
	}

	public Stack<XSSupport> getXmlSchemaObjects() {
		return xmlSchemaObjects;
	}

	public Params getParams() {
		return params;
	}

	public Random getRandom() {
		return random;
	}

	public boolean isFQuitting() throws HamaxagogaException {
		checkFile();
		return fQuitting;
	}

	public void setFQuitting(boolean quitting) {
		fQuitting = quitting;
	}

	public Map<String, String> getNamespaceURI2prefix() {
		return namespaceURI2prefix;
	}
}
