from concurrent.futures import TimeoutError
from google.cloud import pubsub_v1
import json
import requests
from Tracer import init_tracer
from opentracing.ext import tags
from opentracing.propagation import Format


import os
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "key.json"

subscriber = pubsub_v1.SubscriberClient()
# pylint: disable=no-member
subscription_path = subscriber.subscription_path('gcp-poc1-282308', 'testSub1')

tracer = init_tracer('GCPSubscriberPY')


def callback(message):
    print("Received message: {}".format(message))
    print(str(message.data))
    format_message = message.data.decode('utf-8').rstrip()
    json_message = json.loads(format_message)
    print(json_message)

    message.ack()
    url = 'http://localhost:5002/createSalesOrder'
    with tracer.start_span('InvokeCreateSalesOrder', child_of=pollspan) as span:
        span.set_tag('Message', 'Received data google pub sub topic')
        span.set_tag(tags.HTTP_METHOD, 'POST')
        span.set_tag(tags.HTTP_URL, url)
        span.set_tag(tags.SPAN_KIND, tags.SPAN_KIND_RPC_CLIENT)
        headers = {'Content-Type': 'application/json', 'Accept': '*/*'}
        tracer.inject(span, Format.HTTP_HEADERS, headers)
        res = requests.post(url=url, json=json_message, headers=headers)
        print(res)


streaming_pull_future = subscriber.subscribe(
    subscription_path, callback=callback)
print("Listening for messages on {}..\n".format(subscription_path))

# Wrap subscriber in a 'with' block to automatically call close() when done.
with subscriber:
    try:
        with tracer.start_span('PollingTopic') as pollspan:
            pollspan.set_tag('Message', 'polling GCP Topic')

        streaming_pull_future.result(timeout=120.0)
    except TimeoutError:
        streaming_pull_future.cancel()
