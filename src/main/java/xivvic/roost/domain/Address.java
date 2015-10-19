package xivvic.roost.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import xivvic.roost.Nullable;
import xivvic.util.hex.HexUtil;

/**
 *  
 * @author reid.dev
 *
 */

@AutoValue
@JsonDeserialize(builder=AutoValue_Address.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Address 
	implements DomainEntity
{
	public static final List<Address> EMPTY_LIST = Collections.<Address>emptyList();
	
	// TODO:  These properties should come from the domain model
	//
	public static final String PROP_ID          = "address_id";
	public static final String PROP_LINE_ONE    = "address_line_one";
	public static final String PROP_LINE_TWO    = "address_line_two";
	public static final String PROP_CITY        = "address_city";
	public static final String PROP_STATE       = "address_state";
	public static final String PROP_ZIP         = "address_zip";
	/**
	 * Unique id for this address
	 * 
	 * @return
	 */
	@JsonProperty("id")
	public abstract String id();
	
	public final String digest()
	{
		return Address.digest(this);
	}
	
	/**
	 * The first line of the address
	 * @return text of the first line
	 */
	@JsonProperty("lineOne")
	public abstract String lineOne();
	
	/**
	 * The second line of the address
	 * @return text of the second line
	 */
	@Nullable
	@JsonProperty("lineTwo")
	public abstract String lineTwo();
	
	/**
	 * The city component of the address
	 * 
	 * @return the city
	 */
	@JsonProperty("city")
	public abstract String city();
	
	/**
	 * The state component of the address
	 * 
	 * @return the state
	 */
	@JsonProperty("state")
	public abstract String state();
	
	@Nullable
	@JsonProperty("zip")
	public abstract String zip();

	@AutoValue.Builder
	public abstract static class Builder
	{
		@JsonProperty("id")
		public abstract Builder id(String id);

		@JsonProperty("lineOne")
		public abstract Builder lineOne(String lineOne);
		
		@JsonProperty("lineTwo")
		public abstract Builder lineTwo(String lineTwo);
		
		@JsonProperty("city")
		public abstract Builder city(String city);
		
		@JsonProperty("state")
		public abstract Builder state(String state);
		
		@JsonProperty("zip")
		public abstract Builder zip(String zip);
		
		public abstract Address build();
	}
	
	public static Builder builder()
	{
		return new AutoValue_Address.Builder();
	}
	
	public static String digest(Address address)
	{
		if (address == null)
			return null;
		
		String l1 = address.lineOne();
		String l2 = address.lineTwo();
		String ci = address.city();
		String st = address.state();
		String zi = address.zip();
		
		// These fields are required.
		//
		if (l1 == null || ci == null || st == null)
			return null;
		
		if (l2 == null) l2 = "Value Not Provided";
		if (zi == null) zi = "Value Not Provided";
		
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-224");
		}
		catch (NoSuchAlgorithmException e)
		{
			System.err.println(e.getLocalizedMessage());
			return null;
		}	

		md.update(l1.trim().toLowerCase().getBytes());
		md.update(l2.trim().toLowerCase().getBytes());
		md.update(ci.trim().toLowerCase().getBytes());
		md.update(st.trim().toLowerCase().getBytes());
		md.update(zi.trim().toLowerCase().getBytes());

		byte[] bytes  = md.digest();
		String digest = HexUtil.bytesToHex(bytes);
		
		return digest;
	}
}
