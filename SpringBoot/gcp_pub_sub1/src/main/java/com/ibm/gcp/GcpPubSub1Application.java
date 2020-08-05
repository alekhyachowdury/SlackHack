package com.ibm.gcp;

import java.net.URI;

import org.apache.tomcat.jni.Thread;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.*;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ibm.gcp.*;
import com.uber.jaeger.Configuration;
import com.uber.jaeger.samplers.ProbabilisticSampler;

import io.opentracing.Span;
import io.opentracing.Tracer;

@SpringBootApplication
public class GcpPubSub1Application {
	@Value("${CreateSalesOrderURL:notset}")
	String baseUrl;
	
	@Bean
	public io.opentracing.Tracer jaegerTracer() {
		return new Configuration("GCPSubscriber", new Configuration.SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
				new Configuration.ReporterConfiguration()).getTracer();
	}

	public static void main(String[] args) {

		SpringApplication.run(GcpPubSub1Application.class, args);
	}

	@Bean
	public MessageChannel myInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier("myInputChannel") MessageChannel inputChannel,
			PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "testSub2");
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "myInputChannel")
	public MessageHandler messageReceiver() throws UnHandledMethod {
		
		
		return message -> {
			//System.out.println(Thread.current());
			System.out.println("Payload: " + new String((byte[]) message.getPayload()));
			
			System.out.println("Custom Origin Header: " + message.getHeaders().get("origin"));
			System.out.println("Custom Target Header: " + message.getHeaders().get("target"));
			System.out.println("Custom Compartment Header: " + message.getHeaders().get("compartment"));

			String TargetSystem = (String) message.getHeaders().get("target");
			String Compartment = (String) message.getHeaders().get("compartment");

			BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
					.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
			// originalMessage.ack();

			String json = new String((byte[]) message.getPayload());

			try {

				String resp = CallRestAPI(json, TargetSystem, Compartment);
				System.out.println("sending ack");
				originalMessage.ack();
				
			} catch (UnHandledMethod e) {
				System.out.println("sending nack");

				originalMessage.nack();
				System.out.println(e.getMessage());

			} catch (Exception e) {
				System.out.println("sending nack");
				originalMessage.nack();
				e.printStackTrace();
			}

		};

	}

	public String CallRestAPI(String json, String target, String Compartment) throws UnHandledMethod, Exception

	{

		String endpoint = null;

		if (target.equals("REST")) {
			System.out.println("Received proper routing info");
		}

		else {
			throw new UnHandledMethod("Target System unavaiable");
		}

		if (Compartment.equals("GL")) {
			endpoint = baseUrl + "/createSalesOrderGL";
		} else if (Compartment.equals("CN")) {
			endpoint = baseUrl + "/createSalesOrderCN";
		} else {
			throw new UnHandledMethod("Compartment unavaiable");
		}

		RestTemplate restTemplate = new RestTemplate();

		Gson gson = new Gson();

		SalesOrderDTO SO = gson.fromJson(json, SalesOrderDTO.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> requestEntity = new HttpEntity<>(SO, headers);
		ResponseEntity<String> response = null;

		try {
			Span span = jaegerTracer().buildSpan("InvokeCreateSalesOrder").withTag("orderID", SO.getOrder_id()).start();
			response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
			span.close();
		} catch (Exception e) {
			throw new UnHandledMethod("Remote Fault");
		}

		System.out.println(response);
		return "Success";

	}

}
