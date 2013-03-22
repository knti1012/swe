package de.shop.util;

import static javax.transaction.Status.STATUS_ACTIVE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

//import java.util.ServiceLoader;

import javax.annotation.Resource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
//import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class AbstractTest {
	protected static final Locale LOCALE = Locale.GERMAN;

	@Resource(lookup = "java:jboss/UserTransaction")
	private UserTransaction trans;

	@Rule
	public ExpectedException thrown = ExpectedException.none(); 
@Deployment
	protected static Archive<?> deployment() {
		return ArchiveService.getInstance().getArchive();
	}

	/*
	@BeforeClass
	public static void dbreload() {
		ServiceLoader.load(DbReload.class).iterator().next().run(); 
	}
	*/

	@Before
	public void before() throws SystemException { 
		assertThat(trans.getStatus(), is(STATUS_ACTIVE));

		
	}

	@After
	public void after() {
		
	}

	protected UserTransaction getUserTransaction() {
		return trans;
	}
}