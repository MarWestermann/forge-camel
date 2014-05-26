package de.intersales.forge.plugin.camel;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Properties;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

import de.intersales.forge.plugin.camel.facets.CamelFacet;

/**
 *
 */

@RequiresFacet(CamelFacet.class)
@Alias("camel")
public class CamelPlugin implements Plugin {


	@Inject
	private ShellPrompt prompt;

	@Inject
	private Event<InstallFacets> event;

	@Inject
	private Project project;

	static {
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        Velocity.init(properties);
    }
	
	@SetupCommand
	public void setup(PipeOut out) {
		
		out.println("setting up camel project");
		
		if (!project.hasFacet(CamelFacet.class)) {
			event.fire(new InstallFacets(CamelFacet.class));
		} else {
			out.println("project is already setup as camel project");
			return;
		}
		out.println("creating blueprint xml");
		createBlueprintXml();
	}
	
	private void createBlueprintXml() {
		VelocityContext context = new VelocityContext();
		context.put("package", getRoutePackage());
		StringWriter writer = new StringWriter();
		Velocity.mergeTemplate("templates/blueprint.xml.vtl", "UTF-8", context, writer);
		FileResource<?> blueprintFile = project.getFacet(ResourceFacet.class).getResource("OSGI-INF/blueprint/blueprint.xml");
		if (!blueprintFile.exists()) {
			if (!blueprintFile.createNewFile()) {
				throw new RuntimeException("error creating blueprint xml");
			}
			blueprintFile.setContents(writer.toString());
			
		}
	}

	private String getRoutePackage() {
		String defaultPackage = project.getFacet(MetadataFacet.class).getTopLevelPackage();
		return prompt.prompt("package for route builders", defaultPackage);
	}

	@Command("new-routebuilder")
	public void newRouteBuilder(@Option(name="name") String className, PipeOut out) {
		
		if (className == null || className.equals("")) {
			className = prompt.prompt("name of the route builder class");
		}
		
		JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
		
		String routeBuilerPackage = getRouteBuilderPackage();
		VelocityContext context = new VelocityContext();
		
		out.println("package: " + routeBuilerPackage);
		
		context.put("package", routeBuilerPackage);
		context.put("className", className);
		StringWriter writer = new StringWriter();
		Velocity.mergeTemplate("templates/RouteBuilder.java.vtl", "UTF-8", context, writer);
		
		JavaClass routeBuilderClass = JavaParser.parse(JavaClass.class, writer.toString());
		try {
			javaSourceFacet.saveJavaSource(routeBuilderClass);
		} catch (FileNotFoundException e) {
			out.println("error creating the route builder class");
		}
	}

	private String getRouteBuilderPackage() {
		FileResource<?> blueprintFile = project.getFacet(ResourceFacet.class).getResource("OSGI-INF/blueprint/blueprint.xml");
		Node xml = XMLParser.parse(blueprintFile.getResourceInputStream());
		String pack = xml.getTextValueForPatternName("/blueprint/camelContext/package");
		
		if (pack == null || pack.equals("")) {
			pack = project.getFacet(MetadataFacet.class).getTopLevelPackage();
		}
		return pack;
	}

}
