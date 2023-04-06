# ims-proxy
Spike into forwarding calls to the IMS proxy to the IMS stub

## Route all calls starting /identity-management-service to the stub.
- Read https://www.playframework.com/documentation/2.8.x/ScalaSirdRouter
- In routes we call ims.ImsRouter

## Add a controller
- See ims.ImsController
- This simply forwards all calls to the stub using WS and returns its reponses.
