from google.cloud import pubsub_v1
import schedule
import time
import random
import json

import os
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "key.json"


publisher = pubsub_v1.PublisherClient()
# pylint: disable=no-member
topic_path = publisher.topic_path('gcp-poc1-282308', 'input_queue_1')


def post_msg():

    SO_dict = {}
    SO_dict['order_id'] = random.randint(0, 100)
    SO_dict['item'] = 'teItem'
    SO_dict['quantity'] = random.randint(0, 100)
    data = json.dumps(SO_dict)
    data = data.encode("utf-8")
    # Add two attributes, origin and username, to the message
    future = publisher.publish(
        topic_path, data)
    print(future.result())


schedule.every(5).seconds.do(post_msg)

while True:
    schedule.run_pending()
