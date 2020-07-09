import jenkins


def Trigger(request):
    JENKINS_URL = "http://35.228.157.92:8080"
    JENKINS_USERNAME = "mindbenders"
    JENKINS_PASSWORD = "password@1"

    parameter = request.args.get('parameter')
    print(parameter)
    jenkins_server = jenkins.Jenkins(
        JENKINS_URL, username=JENKINS_USERNAME, password=JENKINS_PASSWORD)
    if parameter == 'report':
        print('1 invoke jenkins')
        jenkins_server.build_job('Job1', parameters=None, token='job1')
        print('2 jenkins invoked')
    return 'Success'
