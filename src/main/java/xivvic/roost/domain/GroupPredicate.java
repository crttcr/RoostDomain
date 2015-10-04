package xivvic.roost.domain;

import java.util.function.Predicate;

public interface GroupPredicate
	extends Predicate<Group>
{
	public static GroupPredicate TRUE = (g) -> true;

}
