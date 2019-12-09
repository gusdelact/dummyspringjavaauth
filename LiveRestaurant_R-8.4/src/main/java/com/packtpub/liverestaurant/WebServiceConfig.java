package com.packtpub.liverestaurant;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;



@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
//	@Bean
//	public KeyStoreCallbackHandler securityCallbackHandler() {
//		KeyStoreCallbackHandler callbackHandler = new KeyStoreCallbackHandler();
//		callbackHandler.setPrivateKeyPassword("changeit");
//		return callbackHandler;
//	}

	@Bean
	public Wss4jSecurityInterceptor securityInterceptor() throws Exception {
		Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();

		// validate incoming request
		securityInterceptor.setValidationActions("Signature");
		securityInterceptor.setValidationSignatureCrypto(getCryptoFactoryBean().getObject());
//		securityInterceptor.setValidationDecryptionCrypto(getCryptoFactoryBean().getObject());
//		securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());


		return securityInterceptor;
	}

	@Bean
	public CryptoFactoryBean getCryptoFactoryBean() throws IOException {
		CryptoFactoryBean cryptoFactoryBean = new CryptoFactoryBean();
		cryptoFactoryBean.setKeyStorePassword("serverPassword");
		cryptoFactoryBean.setKeyStoreLocation(new ClassPathResource("serverStore.jks"));
		return cryptoFactoryBean;
	}

	@Override
	public void addInterceptors(List<EndpointInterceptor> interceptors) {
		try {
			interceptors.add(securityInterceptor());
		} catch (Exception e) {
			throw new RuntimeException("could not initialize security interceptor");
		}
	}

	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = "orders")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("OrderService");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("orderService.xsd"));
	}
}