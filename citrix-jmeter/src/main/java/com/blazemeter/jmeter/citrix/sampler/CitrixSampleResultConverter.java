// com.blazemeter.jmeter.citrix.sampler.CitrixSampleResultConverter
package com.blazemeter.jmeter.citrix.sampler;

import java.awt.Rectangle;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.converters.SampleResultConverter;

import com.blazemeter.jmeter.citrix.clause.CheckType;
import com.blazemeter.jmeter.citrix.clause.Clause;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * SampleResultConverter implementation for fields related to
 * {@link CitrixSampleResult}
 */
public class CitrixSampleResultConverter extends SampleResultConverter {

	private static final String ATT_PFX = "bzm_";
	private static final String CLAUSE_PFX = ATT_PFX + "clause_";
	private static final String CLAUSE_SELECTION_PFX = CLAUSE_PFX + "selection_";

	private static final String ATT_X = "x";
	private static final String ATT_Y = "y";
	private static final String ATT_W = "w";
	private static final String ATT_H = "h";

	private static final String ATT_CLAUSE_TYPE = "type";
	private static final String ATT_CLAUSE_EXPECTEDVALUE = "expected_value";
	private static final String ATT_CLAUSE_TIMEOUT = "timeout";
	private static final String ATT_CLAUSE_USE_REGEX = "use_regex";

	private static final String JAVA_LANG_STRING = "java.lang.String"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private static final String TAG_CITRIX_DATA = "citrixData";

	private static Rectangle readRectangle(HierarchicalStreamReader reader, String prefix) {
		Rectangle rectangle = null;
		String x = reader.getAttribute(prefix + ATT_X);
		if (!StringUtils.isEmpty(x)) {
			rectangle = new Rectangle((int) Double.parseDouble(x),
					(int) Double.parseDouble(reader.getAttribute(prefix + ATT_Y)),
					(int) Double.parseDouble(reader.getAttribute(prefix + ATT_W)),
					(int) Double.parseDouble(reader.getAttribute(prefix + ATT_H)));
		}
		return rectangle;
	}

	private static void writeRectangle(HierarchicalStreamWriter writer, Rectangle rectangle, String prefix) {
		if (rectangle != null) {
			writer.addAttribute(prefix + ATT_X, Double.toString(rectangle.getX()));
			writer.addAttribute(prefix + ATT_Y, Double.toString(rectangle.getY()));
			writer.addAttribute(prefix + ATT_W, Double.toString(rectangle.getWidth()));
			writer.addAttribute(prefix + ATT_H, Double.toString(rectangle.getHeight()));
		}
	}

	/**
	 * @param mapper the mapper
	 */
	public CitrixSampleResultConverter(Mapper mapper) {
		super(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) { // superclass
																			// does
																			// not
																			// support
																			// types
		return CitrixSampleResult.class.equals(clazz);
	}

	@Override
	protected void retrieveAttributes(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res) {
		super.retrieveAttributes(reader, context, res);
		CitrixSampleResult citrixSampleResult = (CitrixSampleResult) res;

		// Restore foreground area
		citrixSampleResult.setFgWindowArea(readRectangle(reader, ATT_PFX));

		// Restore end clause
		String checkType = reader.getAttribute(CLAUSE_PFX + ATT_CLAUSE_TYPE);
		if (!StringUtils.isEmpty(checkType)) {
			Rectangle selection = readRectangle(reader, CLAUSE_SELECTION_PFX);
			boolean useRegex = Boolean.parseBoolean(reader.getAttribute(CLAUSE_PFX + ATT_CLAUSE_USE_REGEX));
			Clause clause = new Clause(Enum.valueOf(CheckType.class, checkType),
					reader.getAttribute(CLAUSE_PFX + ATT_CLAUSE_EXPECTEDVALUE), useRegex, selection);
			clause.setTimeout(Long.parseLong(reader.getAttribute(CLAUSE_PFX + ATT_CLAUSE_TIMEOUT)));
			citrixSampleResult.setEndClause(clause);
		}
	}

	@Override
	protected void setAttributes(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
			SampleSaveConfiguration save) {
		CitrixSampleResult citrixSampleResult = (CitrixSampleResult) res;
		super.setAttributes(writer, context, res, save);

		// Save foreground area if it exists
		writeRectangle(writer, citrixSampleResult.getFgWindowArea(), ATT_PFX);

		// Save end clause if is exists
		Clause clause = citrixSampleResult.getEndClause();
		if (clause != null) {
			writer.addAttribute(CLAUSE_PFX + ATT_CLAUSE_TYPE, clause.getCheckType().name());
			writer.addAttribute(CLAUSE_PFX + ATT_CLAUSE_EXPECTEDVALUE, clause.getExpectedValue());
			writer.addAttribute(CLAUSE_PFX + ATT_CLAUSE_USE_REGEX, Boolean.toString(clause.isUsingRegex()));
			writer.addAttribute(CLAUSE_PFX + ATT_CLAUSE_TIMEOUT, Long.toString(clause.getTimeout()));
			writeRectangle(writer, clause.getSelection(), CLAUSE_SELECTION_PFX);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jmeter.save.converters.SampleResultConverter#saveResponseData(com.
	 * thoughtworks.xstream.io.HierarchicalStreamWriter,
	 * com.thoughtworks.xstream.converters.MarshallingContext,
	 * org.apache.jmeter.samplers.SampleResult,
	 * org.apache.jmeter.samplers.SampleSaveConfiguration)
	 */
	@Override
	protected void saveResponseData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
			SampleSaveConfiguration save) {
		super.saveResponseData(writer, context, res, save);
		CitrixSampleResult citrixSampleResult = (CitrixSampleResult) res;
		String base64Data = Base64.getEncoder().encodeToString(citrixSampleResult.getResponseData());
		writer.startNode(TAG_CITRIX_DATA);
		writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
		writer.setValue(base64Data);
		writer.endNode();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jmeter.save.converters.SampleResultConverter#retrieveItem(com.
	 * thoughtworks.xstream.io.HierarchicalStreamReader,
	 * com.thoughtworks.xstream.converters.UnmarshallingContext,
	 * org.apache.jmeter.samplers.SampleResult, java.lang.Object)
	 */
	@Override
	protected boolean retrieveItem(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res,
			Object subItem) {
		boolean result = super.retrieveItem(reader, context, res, subItem);
		String nodeName = reader.getNodeName();
		if (nodeName.equals(TAG_CITRIX_DATA)) {
			final String responseData = (String) subItem;
			if (responseData.length() > 0) {
				res.setResponseData(Base64.getDecoder().decode(responseData));
			}
		}
		return result;
	}
}
