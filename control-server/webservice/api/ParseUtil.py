import subprocess
from subprocess import check_output


def send_push(userid, message):
    # TODO: switch to native python way
    resp = check_output(
        'curl -s -X POST -H "X-Parse-Application-Id: HIWZgDpELVc7HanpltUv1EtSPGF5eBGJBj6QGrVS"  ' +
        ' -H "X-Parse-REST-API-Key: x6EfXn7x973Ke3DWiTW0KVNalwxWqY4WMB6CX0f9"  ' +
        ' -H "Content-Type: application/json"  ' +
        ' -d \'{"userid":"' + userid + '", "message":"' + message + '"}\'   https://api.parse.com/1/functions/update', shell=True)
    print "push response: ", resp

#	url = 'https://api.parse.com/1/functions/update'
#	headers = { 'X-Parse-Application-Id': 'HIWZgDpELVc7HanpltUv1EtSPGF5eBGJBj6QGrVS',
#		'X-Parse-REST-API-Key': 'x6EfXn7x973Ke3DWiTW0KVNalwxWqY4WMB6CX0f9',
#		'Content-Type': 'application/json'}
#	values = {'message': message}

#	data = urllib.urlencode(values)
#	req = urllib2.Request(url, data, headers=headers)
#	response = urllib2.urlopen(req)
#	result = response.read()
#	print result
