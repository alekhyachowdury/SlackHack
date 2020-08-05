from flask import Flask, request
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from json import dumps
from Tracer import init_tracer
import requests
from opentracing.ext import tags
from opentracing.propagation import Format


app = Flask(__name__)
api = Api(app)

tracer = init_tracer('CreateSalesOrderPY')


class CreateSalesOrder(Resource):
    def post(self):
        print(request.json)
        OrderId = request.json['order_id']
        url = 'http://localhost:5003/ordermanager'

        span_ctx = tracer.extract(Format.HTTP_HEADERS, request.headers)
        span_tags = {tags.SPAN_KIND: tags.SPAN_KIND_RPC_SERVER}
        with tracer.start_span('CreateSalesOrderGL', child_of=span_ctx, tags=span_tags) as span:

            span.set_tag('OrderID', OrderId)
            span.set_tag('Message', 'Sending data to order management service')

            span.set_tag(tags.HTTP_METHOD, 'POST')
            span.set_tag(tags.HTTP_URL, url)
            span.set_tag(tags.SPAN_KIND, tags.SPAN_KIND_RPC_CLIENT)
            headers = {'Content-Type': 'application/json', 'Accept': '*/*'}
            tracer.inject(span, Format.HTTP_HEADERS, headers)
            res = requests.post(
                url=url, json=request.json, headers=headers)
            print(headers)

        print(res)
        return 'From SalesOrder'


api.add_resource(CreateSalesOrder, '/createSalesOrder')
if __name__ == '__main__':
    app.run(port='5002')
