
package xivvic.roost.console;


import javax.inject.Singleton;

import dagger.Component;
import xivvic.neotest.program.Neo4jDatabaseModule;


/** 
 * Creates the resources and the services available from the domain.
 * 
 * All of these are available via a ServiceLocator singleton.
 * 
 */

public class InstallProgram
{
	public static void main(String[] args)
	{
		Installer installer = DaggerInstallProgram_Installer.create();
		
		InstallRunner runner = installer.runner();
		
		runner.run();
	}

	@Singleton
	@Component(modules = Neo4jDatabaseModule.class)
	public interface Installer
	{
		InstallRunner runner();
	}
}

