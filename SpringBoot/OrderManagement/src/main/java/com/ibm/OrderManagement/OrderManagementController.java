package com.ibm.OrderManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.HttpHeadersCarrier;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;

@RestController
public class OrderManagementController {
	
	
    @Autowired
	private Tracer tracer;
	

    @Autowired
    private RestTemplate restTemplate;
	
	@RequestMapping("/testService")
	public String getMessage() {
	
		return "OrderManagementService is up and running";
	}
	
	
	
	@PostMapping("/createWorkOrder" ) 
	public OrderStatusDTO getCreateSalesOrderMessage(@RequestBody SalesOrderDTO SalesOrder , @RequestHeader HttpHeaders headers) {
		
		 SpanContext parentContext = tracer.extract(Format.Builtin.HTTP_HEADERS, new HttpHeadersCarrier(headers));
		 System.out.println("from om");
		 System.out.println(headers);
		 //System.out.println(parentContext.toTraceId());
		
		 
		
		Span span = tracer.buildSpan("CreateWorkorder").asChildOf(parentContext).withTag("message", "Creating Workorder").withTag("orderID", SalesOrder.getOrder_id()).start();
		System.out.println("Creating  WorkOrder");
		OrderStatusDTO orderstatus = new OrderStatusDTO();
		
		orderstatus.setSo_order_id(SalesOrder.getOrder_id());
		orderstatus.setStatus("PROCESSING");
		
		span.finish();
		
	
		
		
		return orderstatus;
	}
	
	
}
