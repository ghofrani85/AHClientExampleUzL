package eu.arrowhead;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.client.skeleton.common.orchestrator.Orchestrator;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.security.ProviderSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

//import eu.arrowhead.MyCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class ConsumerApplicationInitListener extends ApplicationInitListener {
	
	public static MqttClient client;
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;

	@Autowired
	private Orchestrator orchestrator;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	private final Logger logger = LogManager.getLogger(ConsumerApplicationInitListener.class);

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		Map<String, String> metadata = new HashMap<>();

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		checkCoreSystemReachability(CoreSystem.ORCHESTRATOR);

		arrowheadService.updateCoreServiceURIs(CoreSystem.ORCHESTRATOR);

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

		try {
			// find services
			// statt random string nach sensor daten suchen
			List<OrchestrationResultDTO> results = orchestrator.orchestrate("get-random-string", "HTTP-INSECURE-JSON",
					HttpMethod.GET,	metadata);
			results.addAll(orchestrator.orchestrate("get-random-integer", "HTTP-INSECURE-JSON", HttpMethod.GET,
					metadata));

			// consume services and output
			for (OrchestrationResultDTO result : results) {
				String output = arrowheadService.consumeServiceHTTP(String.class,
						HttpMethod.valueOf(result.getMetadata().get("http-method")),
						result.getProvider().getAddress(), result.getProvider().getPort(),
						result.getServiceUri(), result.getInterfaces().get(0).getInterfaceName(), null, null);
				
				// Read MQTT Server and topic from output
				String mqttServer = "";
				String port = "";
				String topic = "";
				
				System.out.println("MQTT Server: " + mqttServer+port);
				System.out.println("Topic: " + topic);

				// Generate a random ID
				Random rnd = new Random();
				int ID = rnd.nextInt(999999);

				// New MQTT Client
				try {
					client = new MqttClient(mqttServer, "" + ID);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Connect MQTT Client to Callback
				MyCallback callback = new MyCallback();
				client.setCallback(callback);

				// Connect MQTT Client to Broker
				MqttConnectOptions mqOptions = new MqttConnectOptions();
				mqOptions.setCleanSession(true);
				try {
					client.connect(mqOptions);
				} catch (MqttSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Subscribe MQTT Client to the topic
				try {
					client.subscribe(topic);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(output);
			}
		} catch (ArrowheadException | InterruptedException e) {
			System.out.println("test");
			e.printStackTrace();
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
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
