package eu.arrowhead;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.client.skeleton.common.eventhandler.ConfigEventProperties;
import eu.arrowhead.client.skeleton.common.eventhandler.EventHandler;
import eu.arrowhead.client.skeleton.common.serviceregistry.ServiceRegistry;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.misc.PublisherThread;
import eu.arrowhead.security.ProviderSecurityConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.SocketException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PublisherApplicationInitListener extends ApplicationInitListener {
	private PublisherThread p;

	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;

	@Autowired
	private ServiceRegistry serviceRegistry;

	@Autowired
	private EventHandler eventHandler;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	private final Logger logger = LogManager.getLogger(PublisherApplicationInitListener.class);

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		Map<String, String> metadata = new HashMap<>();

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);

		arrowheadService.updateCoreServiceURIs(CoreSystem.SERVICE_REGISTRY);
		arrowheadService.updateCoreServiceURIs(CoreSystem.EVENT_HANDLER);

		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		
			setTokenSecurityFilter();
		} else {
			logger.info("TokenSecurityFilter in not active");
		}

		// set general service metadata
		metadata.put("country", "Germany");
		metadata.put("location", "Dresden");
		metadata.put("organization", "HTW Dresden");
		metadata.put("department", "industrie40");

		// register services
		try {
			serviceRegistry.register("get-echo", "/api/echo", HttpMethod.GET, metadata);
			serviceRegistry.register("publish-data", "/api/data", HttpMethod.GET, metadata);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// send events
		p = new PublisherThread(this.eventHandler);
		p.run();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		// unregister services, destroy subscriptions on terminating
		p.interrupt();

		serviceRegistry.unregister("get-echo");
		serviceRegistry.unregister("publish-data");
	}

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();

		if (authorizationPublicKey == null)
			throw new ArrowheadException("Authorization public key is null");
		
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
			keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
			throw new ArrowheadException(ex.getMessage());
		}

		final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());
		
		providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
		providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);
	}
}
