from flask import Flask, request
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from json import dumps
from Tracer import init_tracer
from opentracing.ext import tags
from opentracing.propagation import Format

app = Flask(__name__)
api = Api(app)

tracer = init_tracer('OrderManagementPY')


class OrderManager(Resource):
    def post(self):
        print(request)
        OrderId = request.json['order_id']
        span_ctx = tracer.extract(Format.HTTP_HEADERS, request.headers)
        span_tags = {tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER}
        with tracer.start_span('CreateWorkOrder', child_of=span_ctx, tags=span_tags) as span:
            span.set_tag('OrderID', OrderId)
            span.set_tag('Message', 'Received data from sales order service')

        return 'SalesOrder Processed'


api.add_resource(OrderManager, '/ordermanager')
if __name__ == '__main__':
    app.run(port='5003')
