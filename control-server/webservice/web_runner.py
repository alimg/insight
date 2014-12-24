import WebService

#service = WebService.WebService()
#service.run()

service = WebService.WebService(lambda (x, y): 0)
service.run()

