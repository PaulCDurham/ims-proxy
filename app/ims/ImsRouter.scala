package ims

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter

import javax.inject.Inject

class ImsRouter @Inject()(imsController: ImsController) extends SimpleRouter {

  override def routes: Routes = {
    case _ => imsController.forward
  }

}
