package models

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

class Color extends Enumeration {
  val Blue = Value
  val Red = Value
  val Green = Value
}
object Color extends Color

case class Sample(name:String, v:Int, c:Color)

// Schemas
@Singleton
class ColorDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  class SampleTable(tag: Tag) extends Table[Sample](tag, "STATS") {
    def name  = column[String]("name")
    def v     = column[Int]("v")
    def color = column[Color]("COLOR")
    def * = (name, v, color) <> (Sample.tupled, Sample.unapply)
  }

  def enumIdMapper(enum: Enumeration) = MappedColumnType.base[enum.Value, Int](
    e => e.id,
    i => enum.apply(i)
  )
  implicit val colorMapper = enumIdMapper(Color)
}
