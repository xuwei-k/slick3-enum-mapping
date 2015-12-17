package models

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

class Color extends Enumeration {
  type Color = Value
  val Blue = Value("Blue")
  val Red = Value("Red")
  val Green = Value("Green")
}
object Color extends Color

case class Sample(name:String, id:Int, c:Color.Value)

// Schemas
@Singleton
class ColorDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import driver.api._

  class SampleTable(tag: Tag) extends Table[Sample](tag, "Sample") {
    def name  = column[String]("NAME")
    def id    = column[Int]("ID")
    def color = column[Color.Value]("COLOR")
    def * = (name, id, color) <> (Sample.tupled, Sample.unapply)
  }

  def enumStringMapper(enum: Enumeration) = MappedColumnType.base[enum.Value, String](
    e => e.toString,
    s => enum.withName(s)
  )
  implicit val colorMapper = enumStringMapper(Color)
}
