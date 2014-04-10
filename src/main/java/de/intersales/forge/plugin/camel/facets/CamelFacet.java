package de.intersales.forge.plugin.camel.facets;

import javax.inject.Inject;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;

public class CamelFacet extends BaseFacet {

	private static final Dependency CAMEL_CORE_DEPENDENCY = cD("org.apache.camel", "camel-core");
	private static final Dependency CAMEL_BLUEPRINT_DEPENDENCY = cD("org.apache.camel", "camel-blueprint");
	private static final Dependency CAMEL_TEST_BLUEPRINT_DEPENDENCY = cD("org.apache.camel", "camel-test-blueprint");
	private static final Dependency SLF4J_API_DEPENDENCY = cD("org.slf4j", "slf4j-api");
	private static final Dependency SLF4J_LOG4J_DEPENDENCY = cD("org.slf4j", "slf4j-log4j12");
	private static final Dependency SLF4J_JCL_DEPENDENCY = cD("org.slf4j", "jcl-over-slf4j");
	private static final Dependency LOG4J_DEPENDENCY = cD("log4j", "log4j");
	
	@Inject
	private DependencyInstaller installer;
	
	
	@Override
	public boolean install() {
		createDependencies();
		
		setPackagingToBundle();
		
		addMavenBundlePlugin();
		
		return true;
	}

	private void addMavenBundlePlugin() {
		MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
		DependencyBuilder bundlePluginDependency = createBundlePluginDependency();
		MavenPluginBuilder bundlePlugin = MavenPluginBuilder.create().setDependency(bundlePluginDependency);
		bundlePlugin.addExecution(
                ExecutionBuilder.create()
                        .setId("bundle-manifest")
                        .setPhase("process-classes")
                        .addGoal("manifest"))
                .setExtensions(true);
		pluginFacet.addPlugin(bundlePlugin);
		
	}
	
	private DependencyBuilder createBundlePluginDependency() {
        return DependencyBuilder.create()
                .setGroupId("org.apache.felix")
                .setArtifactId("maven-bundle-plugin");
    }

	private void setPackagingToBundle() {
		getProject().getFacet(PackagingFacet.class).setPackagingType(PackagingType.BUNDLE);
	}

	private void createDependencies() {
		installer.install(project, CAMEL_CORE_DEPENDENCY);
		installer.install(project, CAMEL_BLUEPRINT_DEPENDENCY);
		installer.install(project, CAMEL_TEST_BLUEPRINT_DEPENDENCY);
		installer.install(project, SLF4J_API_DEPENDENCY);
		installer.install(project, SLF4J_JCL_DEPENDENCY);
		installer.install(project, SLF4J_LOG4J_DEPENDENCY);
		installer.install(project, LOG4J_DEPENDENCY);
	}

	@Override
	public boolean isInstalled() {
		return (installer.isInstalled(getProject(), CAMEL_CORE_DEPENDENCY));
	}
	
	private static Dependency cD(String groupId, String artifactId) {
		return DependencyBuilder
				.create().setGroupId(groupId)
				.setArtifactId(artifactId);
	}

}
