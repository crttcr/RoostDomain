package xivvic.roost.neo;

import java.util.function.Function;


public interface PropMeta
{
	public String name();
	public String key();
	public Class<?> type();
	public boolean unique();
	public boolean required();
	
	public Function<Object, Object> object2NeoConverter();
	public Function<Object, Object> neo2ObjectConverter();
}
