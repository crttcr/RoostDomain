package xivvic.roost.neo;

import java.util.function.Function;

import com.google.auto.value.AutoValue;

import xivvic.roost.Nullable;

@AutoValue
public abstract class PropMetaBase
	implements PropMeta
{
	public abstract String name();
	public abstract String key();
	public abstract Class<?> type();
	public abstract boolean unique();
	public abstract boolean required();

	@Nullable
	public abstract Function<Object, Object> object2NeoConverter();

	@Nullable
	public abstract Function<Object, Object> neo2ObjectConverter();

	
	public static Builder builder()
	{
		return new AutoValue_PropMetaBase.Builder();
	}
	
	@AutoValue.Builder
	public abstract static class Builder
	{
		public abstract PropMetaBase build();
		public abstract Builder key(String key);
		public abstract Builder name(String name);
		public abstract Builder type(Class<?> cls);
		public abstract Builder unique(boolean unique);
		public abstract Builder required(boolean required);
		public abstract Builder object2NeoConverter(Function<Object, Object> converter);
		public abstract Builder neo2ObjectConverter(Function<Object, Object> converter);
	}
}
