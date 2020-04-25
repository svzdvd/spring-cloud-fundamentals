package net.davidesavazzi.client;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ClientApplication {

	private static final String SERVER_NAME = "service-app";

	@Autowired
	EurekaClient eurekaClient;

	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@PostConstruct
	private void postConstruct() {
		InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(SERVER_NAME, false);
		System.out.println("Found service: " + instanceInfo.getHomePageUrl());

		ServiceInstance serviceInstance = discoveryClient.getInstances(SERVER_NAME).get(0);
		System.out.println("Found service: " + serviceInstance.getHost() + ":" + serviceInstance.getPort());
	}

	@RequestMapping("/")
	public String message() {
		RestTemplate restTemplate = restTemplateBuilder.build();
		InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(SERVER_NAME, false);
		ResponseEntity<String> response = restTemplate.exchange(instanceInfo.getHomePageUrl(),
				HttpMethod.GET, null, String.class);
		return response.getBody();
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}
}
