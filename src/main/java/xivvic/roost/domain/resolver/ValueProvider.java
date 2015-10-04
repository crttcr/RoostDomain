package xivvic.roost.domain.resolver;

import java.util.Map;

public interface ValueProvider
{
	public String[] getFieldNames();
	
	public Map<String, String> getValueMap(int index);

	public String[] getFieldValues(int index);

	public int getDataRowCount();

}
