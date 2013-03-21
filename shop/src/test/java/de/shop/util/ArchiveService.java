package de.shop.util;

import java.io.File;
import java.nio.file.Paths;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public enum ArchiveService {
	INSTANCE;

	private static final String WEB_PROJEKT = "shop";
	private static final String TEST_WAR = WEB_PROJEKT + ".war";
	private static final String BASE_PACKAGE_PATH = "/de/shop/";

	private static final String CLASSES_DIR = "target/classes";

	private static final String WEBINF_DIR = "src/main/webapp/WEB-INF/";
	private static final String BEANS_XML = WEBINF_DIR + "beans.xml";
	private static final String WEB_XML = WEBINF_DIR + "web.xml";
	private static final String FACES_CONFIG_XML = WEBINF_DIR
			+ "faces-config.xml";
	private static final String EJBJAR_XML = WEBINF_DIR + "ejb-jar.xml";
	private static final String JBOSSWEB_XML = WEBINF_DIR + "jboss-web.xml";
	private static final String JBOSSEJB3_XML = WEBINF_DIR + "jboss-ejb3.xml";

	private static final String JBOSS_DEPLOYMENT_STRUCTURE_XML = "src/test/resources/jboss-deployment-structure.xml";

	private static final String SOLDER_VERSION = "3.1.1.Final";

	private static final String SEAM_VERSION = "3.1.0.Final";
	private static final String PICKETLINK_VERSION = "1.5.0.Alpha02";
	private static final String DROOLS_VERSION = "5.1.1";

	private static final String RICHFACES_VERSION = "4.2.2.Final";
	private static final String CSSPARSER_VERSION = "0.9.5";
	private static final String SAC_VERSION = "1.3";

	private final WebArchive archive = ShrinkWrap.create(WebArchive.class,
			TEST_WAR);

	private ArchiveService() {
		addKlassen();

		addSolder();
		addSeam();
		addRichFaces();

		addWebInf();

		addTestklassen();

		
	}

	private void addKlassen() {
		final JavaArchive tmp = ShrinkWrap.create(JavaArchive.class);
		tmp.as(ExplodedImporter.class).importDirectory(CLASSES_DIR);
		archive.merge(tmp, "WEB-INF/classes");
	}

	private void addWebInf() {
		final File beansXml = Paths.get(BEANS_XML).toFile();
		if (beansXml.exists()) {
			archive.addAsWebInfResource(beansXml);
		} 
		else {
			archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		}

		final File webXml = Paths.get(WEB_XML).toFile();
		if (webXml.exists()) {
			archive.setWebXML(webXml);
		}

		final File facesConfigXml = Paths.get(FACES_CONFIG_XML).toFile();
		if (facesConfigXml.exists()) {
			archive.addAsWebInfResource(facesConfigXml);
		}

		final File ejbJarXml = Paths.get(EJBJAR_XML).toFile();
		if (ejbJarXml.exists()) {
			archive.addAsWebInfResource(ejbJarXml);
		}

		final File jbossWebXml = Paths.get(JBOSSWEB_XML).toFile();
		if (jbossWebXml.exists()) {
			archive.addAsWebInfResource(jbossWebXml);
		}

		final File jbossEjb3Xml = Paths.get(JBOSSEJB3_XML).toFile();
		if (jbossEjb3Xml.exists()) {
			archive.addAsWebInfResource(jbossEjb3Xml);
		}

		final File jbossDeploymentStructureXml = Paths.get(
				JBOSS_DEPLOYMENT_STRUCTURE_XML).toFile();
		if (jbossDeploymentStructureXml.exists()) {
			archive.addAsWebInfResource(jbossDeploymentStructureXml);
		} 
		else {
			
		}
	}

	private void addSolder() {
		archive.addAsLibraries(
				DependencyResolvers
						.use(MavenDependencyResolver.class)
						.goOffline()
						.artifact(
								"org.jboss.solder:solder-api:" + SOLDER_VERSION)
						.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibraries(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.solder:solder-impl:"
												+ SOLDER_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibraries(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.solder:solder-logging:"
												+ SOLDER_VERSION)
								.resolveAs(JavaArchive.class).iterator().next());
	}

	private void addSeam() {
		final MavenDependencyResolver pomResolver = DependencyResolvers
				.use(MavenDependencyResolver.class).goOffline()
				.loadMetadataFromPom("pom.xml");

		archive.addAsLibrary(
				DependencyResolvers
						.use(MavenDependencyResolver.class)
						.goOffline()
						.artifact(
								"org.jboss.seam.persistence:seam-persistence-api:"
										+ SEAM_VERSION)
						.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.persistence:seam-persistence:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.transaction:seam-transaction-api:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.transaction:seam-transaction:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.faces:seam-faces-api:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.faces:seam-faces:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.international:seam-international-api:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.international:seam-international:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.security:seam-security-api:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.jboss.seam.security:seam-security:"
												+ SEAM_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						pomResolver.artifact("com.ocpsoft:prettyfaces-jsf2")
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.picketlink.idm:picketlink-idm-core:"
												+ PICKETLINK_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.picketlink.idm:picketlink-idm-common:"
												+ PICKETLINK_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.picketlink.idm:picketlink-idm-spi:"
												+ PICKETLINK_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.picketlink.idm:picketlink-idm-api:"
												+ PICKETLINK_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.drools:drools-api:"
												+ DROOLS_VERSION)
								.resolveAs(JavaArchive.class).iterator().next());
	}

	private void addRichFaces() {
		archive.addAsLibrary(
				DependencyResolvers
						.use(MavenDependencyResolver.class)
						.goOffline()
						.artifact(
								"org.richfaces.ui:richfaces-components-ui:"
										+ RICHFACES_VERSION)
						.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.richfaces.ui:richfaces-components-api:"
												+ RICHFACES_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.richfaces.core:richfaces-core-impl:"
												+ RICHFACES_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"org.richfaces.core:richfaces-core-api:"
												+ RICHFACES_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers
								.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact(
										"net.sourceforge.cssparser:cssparser:"
												+ CSSPARSER_VERSION)
								.resolveAs(JavaArchive.class).iterator().next())
				.addAsLibrary(
						DependencyResolvers.use(MavenDependencyResolver.class)
								.goOffline()
								.artifact("org.w3c.css:sac:" + SAC_VERSION)
								.resolveAs(JavaArchive.class).iterator().next());
	}

	private void addTestklassen() {
		archive.addClass(AbstractTest.class);
		archive.addClass(AbstractDomainTest.class);

		final Filter<ArchivePath> filter = Filters.include(BASE_PACKAGE_PATH
				+ "[\\w-/]*Test\\.class");
		for (Class<?> clazz : Testklassen.INSTANCE.getTestklassen()) {
			archive.addPackages(false, filter, clazz.getPackage());
		}
	}

	public static ArchiveService getInstance() {
		return INSTANCE;
	}

	public Archive<?> getArchive() {
		return archive;
	}
}
