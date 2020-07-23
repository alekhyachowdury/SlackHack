import jenkins
import requests


def Trigger(request):
    JENKINS_URL = "http://35.228.157.92:8080"
    JENKINS_USERNAME = "mindbenders"
    JENKINS_PASSWORD = "password@1"

    parameter = request.args.get('parameter')
    print(parameter)
    jenkins_server = jenkins.Jenkins(
        JENKINS_URL, username=JENKINS_USERNAME, password=JENKINS_PASSWORD)
    if parameter == 'report':
        print('invoke jenkins')
        jenkins_server.build_job(
            'IncidentReport', parameters=None, token='mindbenders')
    elif parameter == 'create':
        print('invoke create file service')
        requests.get(
            'https://europe-west3-gcp-poc1-282308.cloudfunctions.net/py-android-voice-trigger?parameter=create')
        print('file cretaed in GCS bucket')
    else:
        jenkins_server.build_job(
            'MindBenders_PipeLine', parameters=None, token='mindbenders')
    return 'Success'
