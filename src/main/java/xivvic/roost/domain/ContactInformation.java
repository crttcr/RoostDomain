package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.auto.value.AutoValue;

import xivvic.roost.Nullable;

/**
 * Immutable class representing contact information
 * 
 * @author reid.dev
 *
 */

@AutoValue
public abstract class ContactInformation 
{
	public static final List<ContactInformation> EMPTY_LIST = Collections.<ContactInformation>emptyList();
	/**
	 * Return the physical address (postal address).
	 * 
	 * @return Physical address or null if there is none.
	 * 
	 */

	@Nullable
	public abstract Address address();
	public abstract Map<PhoneType, String> phoneMap();
	public abstract String twitterHandle();
}
