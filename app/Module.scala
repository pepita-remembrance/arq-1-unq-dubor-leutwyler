import ar.edu.unq.arqsoft.database.{DBConnector, H2Connector}
import com.google.inject.AbstractModule
import com.google.inject.binder.{LinkedBindingBuilder, ScopedBindingBuilder}

import scala.reflect.ClassTag

class Module extends EmptyModule
  with DatabaseBindings

class EmptyModule extends AbstractModule {
  def configure(): Unit = {}

  def bind[T](implicit tag: ClassTag[T]): RichAnnotatedBindingBuilder[T] =
    new RichAnnotatedBindingBuilder[T](bind(tag.runtimeClass.asInstanceOf[Class[T]]))

  class RichAnnotatedBindingBuilder[T](builder: LinkedBindingBuilder[T]) {
    def to[U <: T](implicit tag: ClassTag[U]): ScopedBindingBuilder = builder.to(tag.runtimeClass.asInstanceOf[Class[U]])
  }

}

trait DatabaseBindings extends EmptyModule {
  override def configure(): Unit = {
    super.configure()
    bind[DBConnector].to[H2Connector]
  }
}
