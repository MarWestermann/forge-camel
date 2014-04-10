package de.intersales.forge.plugin.camel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class CamelPluginTest extends AbstractShellTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment()
            .addPackages(true, CamelPlugin.class.getPackage());
   }

   @Test
   public void testDefaultCommand() throws Exception
   {
      getShell().execute("came");
   }

   @Test
   public void testCommand() throws Exception
   {
      getShell().execute("came command");
   }

   @Test
   public void testPrompt() throws Exception
   {
      queueInputLines("y");
      getShell().execute("came prompt foo bar");
   }
}
