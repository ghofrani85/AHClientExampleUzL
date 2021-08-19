package eu.arrowhead;

import java.io.IOException;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import eu.arrowhead.client.skeleton.common.serviceregistry.ServiceRegistry;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.security.ProviderSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;

	@Autowired
	private ServiceRegistry serviceRegistry;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		Map<String, String> metadata = new HashMap<>();

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);

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
			serviceRegistry.register("get-random-string", "/api/random/string", HttpMethod.GET, metadata);
			serviceRegistry.register("get-random-integer", "/api/random/integer", HttpMethod.GET, metadata);
			serviceRegistry.register("set-random-integer", "/api/random/integer", HttpMethod.POST, metadata);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		// unregister services on terminating
		serviceRegistry.unregister("get-random-string");
		serviceRegistry.unregister("get-random-integer");
		serviceRegistry.unregister("set-random-integer");
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
