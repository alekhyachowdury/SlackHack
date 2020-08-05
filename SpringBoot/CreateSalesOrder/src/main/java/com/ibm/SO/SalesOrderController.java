package com.ibm.SO;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;

import com.google.gson.Gson;
//import com.uber.jaeger.*;

import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;

@RestController
public class SalesOrderController {
	@Value("${OrderManagementURL:notset}")
	String baseUrl;
	
	@Autowired
	private Tracer tracer;
	

    @Autowired
    private RestTemplate restTemplate;
	
	@RequestMapping("/testService")
	public String getMessage() {
	
		return "Service is up and running at" + baseUrl;
	}
	
	
	
	@PostMapping("/createSalesOrderGL" ) 
	public String getCreateSalesOrderMessage(@RequestBody SalesOrderDTO SalesOrder , @RequestHeader HttpHeaders headers) {
		
		SpanContext parentContext = tracer.extract(Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(headers));
		
		Span span = tracer.buildSpan("CreateSalesOrderGL").asChildOf(parentContext).withTag("orderID", SalesOrder.getOrder_id()).withTag("message", "received in global").start();
		
		
		System.out.println("Processing in GL");
	

		String status = OrderManagementAPI(SalesOrder , span);
		System.out.println(status);
		span.setTag("response", status);
		span.finish();
		return "received from SO GL";
	}
	
	@PostMapping("/createSalesOrderCN" ) 
	public String getCreateSalesOrderMessageCN(@RequestBody SalesOrderDTO SalesOrder) {
		
		Span span = tracer.buildSpan("CreateSalesOrderGL").withTag("orderID", SalesOrder.getOrder_id()).withTag("message", "received in global").start();
		
		
		
		System.out.println("Processing in CN");
		
		String status = OrderManagementAPI(SalesOrder ,span);
		 span.setTag("response", status);
		 //span.close();
		System.out.println(status);
	
		return "received from SO CN";
	} 

	
	
	public String OrderManagementAPI(SalesOrderDTO SalesOrder , Span rootspan)

	{
		
		
		RestTemplate restTemplate = new RestTemplate();

		String endpoint = baseUrl + "/createWorkOrder";

		SalesOrderDTO SO = SalesOrder;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		
		
		
		 
	
		
        
		
		Span span = tracer.buildSpan("CreateWO").asChildOf(rootspan.context()).withTag("orderID", SalesOrder.getOrder_id()).withTag("message", "invoking OrderManagement")
				.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
				.withTag(Tags.HTTP_URL,endpoint)
				.withTag(Tags.HTTP_METHOD, "POST").start();
	

		
		
		tracer.inject(span.context(),  Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(headers));
		HttpEntity<Object> requestEntity = new HttpEntity<>(SO, headers);
		ResponseEntity<String> response = null;
		System.out.println("from so");
		System.out.println(requestEntity.getHeaders());


		
		response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
			span.finish();
		
		System.out.println(response);
		return "Success";

	}



}
