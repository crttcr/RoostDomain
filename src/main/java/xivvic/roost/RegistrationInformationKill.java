package xivvic.roost;

import java.util.Map;
import java.util.logging.Logger;

import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.User;

import com.google.auto.value.AutoValue;

/**
 * Captures the information for a user registration
 * 
 * @author Reid
 *
 */
@AutoValue
public abstract class RegistrationInformationKill
{
	private final static Logger LOG = Logger.getLogger(RegistrationInformationKill.class.getName()); 

	@Nullable
	public abstract String id();
	public abstract String username();
	public abstract String passhash();
	public abstract String email();
	public abstract String groupName();
	public abstract String personId();
	
	public static RegistrationInformationKill create(Map<String, String> values)
	{
		String       id = values.get(User.PROP_ID);
		String    uname = values.get(User.PROP_UNAME);
		String    phash = values.get(User.PROP_PHASH);
		String    email = values.get(User.PROP_EMAIL);
		String    group = values.get(Group.PROP_NAME);
		String personId = values.get(Person.PROP_ID);
		
		if (uname == null)
		{
			String msg = "Cannot register a user without a username";
			LOG.warning(msg);
			return null;
		}
		
		if (phash == null)
		{
			String msg = "Cannot register a user without a password hash";
			LOG.warning(msg);
			return null;
		}
		
		if (email == null)
		{
			String msg = "Cannot register a user without a email name";
			LOG.warning(msg);
			return null;
		}
		
		if (group == null)
		{
			String msg = "Cannot register a user without a group";
			LOG.warning(msg);
			return null;
		}
		
		if (personId == null)
		{
			String msg = "Cannot register a user without an ID of a valid user.";
			LOG.warning(msg);
			return null;
		}
		
		return new AutoValue_RegistrationInformationKill(id, uname, phash, email, group, personId);
	}
}
