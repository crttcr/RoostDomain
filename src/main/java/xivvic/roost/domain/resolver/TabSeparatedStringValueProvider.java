package xivvic.roost.domain.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabSeparatedStringValueProvider
		implements ValueProvider
{
	public static final String RECORD_SEPARATOR = "\n";
	public static final String FIELD_SEPARATOR = "\t";

	
	private final String[] fields;
	private final String input;
	private final List<Map<String, String>> fieldMaps = new ArrayList<Map<String, String>>();
	private final String[] inputLines;
	

	public TabSeparatedStringValueProvider(String input)
	{
		assert input != null;
		
		this.input = input;

		inputLines      = input.split(RECORD_SEPARATOR);
		this.fields     = createFieldList(inputLines[0]);

		
		for (int i = 1; i < inputLines.length; i++)
		{
			String[]              values = inputLines[i].split(FIELD_SEPARATOR);
			Map<String, String> valueMap = createValueMap(fields, values);
			
			fieldMaps.add(valueMap);
		}
	}
	
	private Map<String, String> createValueMap(String[] fields, String[] values)
	{
		Map<String, String> map = new HashMap<>();
		
		for (int i = 0 ; i < fields.length; i++)
		{
	      String   field = fields[i];
	      
	      if (i >= values.length)
	      {
	      	break;
	      }

	      String   value = values[i];
	      
	      if (value == null || value.length() == 0)
	      	continue;
	      
	      map.put(field, value);
		}
		
		return Collections.unmodifiableMap(map);
	}
	

	private String[] createFieldList(String header)
	{
		if (header == null)
			throw new NullPointerException("Expecting non-null header");
		
		String[] fieldNames = header.split(FIELD_SEPARATOR);
		
		return fieldNames;
	}
	

	@Override
	public String[] getFieldNames()
	{
		return fields.clone();
	}

	@Override
	public String[] getFieldValues(int index)
	{
		if (index < 0)
			return null;
		
		if (index >= inputLines.length - 1)
			return null;
		
		int effective_index = index + 1;
		
		String line = inputLines[effective_index];
		
		return line.split(FIELD_SEPARATOR);
	}

	@Override
	public Map<String, String> getValueMap(int index)
	{
		return fieldMaps.get(index);
	}

	@Override
	public int getDataRowCount()
	{
		return fieldMaps.size();
	}

	public String getOriginalInput()
	{
		return input;
	}
}
