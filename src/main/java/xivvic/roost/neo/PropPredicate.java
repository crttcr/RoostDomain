package xivvic.roost.neo;

import java.util.function.Predicate;

public interface PropPredicate
	extends Predicate<PropMeta>
{
	static PropPredicate predicateRequired()
	{
		PropPredicate p = new PropPredicate()
		{
			@Override
			public boolean test(PropMeta t)
			{
				if (t == null)
					return false;
				
				return t.required();
			}
			
		};
		
		return p;
		
	}

	static PropPredicate predicateUnique()
	{
		PropPredicate p = new PropPredicate()
		{
			@Override
			public boolean test(PropMeta t)
			{
				if (t == null)
					return false;
				
				return t.unique();
			}
			
		};
		
		return p;
		
	}

}
