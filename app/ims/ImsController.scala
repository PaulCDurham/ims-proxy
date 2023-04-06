package ims

import akka.util.{ByteString, CompactByteString}
import play.api.Logging
import play.api.http.HttpEntity
import play.api.libs.ws.WSClient
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ImsController @Inject()(
  override val controllerComponents: ControllerComponents,
  ws: WSClient
)(implicit ec: ExecutionContext) extends BaseController with Logging {

  def forward: Action[ByteString] = Action(parse.byteString).async {
    request =>
      val path = request.path.replaceFirst("identity-management-service", "identity-management-service-stubs")
      val url = s"http://localhost:15020$path"

      logger.info(s"Forwarding: ${request.method} $url")

      ws.url(url)
        .withMethod(request.method)
        .withHttpHeaders(request.headers.headers: _*)
        .withBody(request.body)
        .execute()
        .map {
          response =>
            Result(
              ResponseHeader(
                status = response.status,
                headers = buildHeaders(response.headers)
              ),
              body = buildBody(response.body, response.headers)
            )
        }
  }

  // For HTTP Verbs we want to use Seq not scala.collection.Seq
  private def buildBody(body: String, headers: Map[String, scala.collection.Seq[String]]): HttpEntity = {
    if (body.isEmpty) {
      HttpEntity.NoEntity
    }
    else {
      HttpEntity.Strict(CompactByteString(body), buildContentType(headers))
    }
  }

  // For HTTP Verbs we want to use Seq not scala.collection.Seq
  private def buildContentType(headers: Map[String, scala.collection.Seq[String]]): Option[String] = {
    headers
      .find(_._1.equalsIgnoreCase("content-type"))
      .map(_._2.head)
  }

  // For HTTP Verbs we want to use Seq not scala.collection.Seq
  private def buildHeaders(headers: Map[String, scala.collection.Seq[String]]): Map[String, String] = {
    headers
      .map {
        case (header, values) => (header, values.head)
      }
      .filter {
        case (header, _) if header.equalsIgnoreCase("content-type") => false
        case (header, _) if header.equalsIgnoreCase("content-length") => false
        case _ => true
      }
  }

}
