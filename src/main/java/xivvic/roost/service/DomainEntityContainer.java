package xivvic.roost.service;

import java.util.function.Consumer;

import xivvic.roost.domain.DomainEntity;


public interface DomainEntityContainer
{
	boolean apply(Consumer<DomainEntity> function);

//	boolean applyToRelated(Relationship rship, Consumer<DomainEntity> function);

}
