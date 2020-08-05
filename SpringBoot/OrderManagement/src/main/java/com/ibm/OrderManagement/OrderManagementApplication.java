package com.ibm.OrderManagement;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//import com.uber.jaeger.Configuration;
//import com.uber.jaeger.samplers.ProbabilisticSampler;

@SpringBootApplication
public class OrderManagementApplication {
	
//	@Bean
//	public io.opentracing.Tracer jaegerTracer() {
//		return new Configuration("OrderManagement", new Configuration.SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
//				new Configuration.ReporterConfiguration())
//				.getTracer();
//	}
	
	  @Bean
	    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
	        return restTemplateBuilder
	                .setConnectTimeout(Duration.ofSeconds(5))
	                .setReadTimeout(Duration.ofSeconds(5))
	                .build();
	    }


	public static void main(String[] args) {
		SpringApplication.run(OrderManagementApplication.class, args);
	}

}
