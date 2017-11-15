import java.lang.annotation.Annotation
import java.lang.reflect.Constructor

import ar.edu.unq.arqsoft.database.{DBConnector, H2Connector}
import com.google.inject._
import com.google.inject.binder.{LinkedBindingBuilder, ScopedBindingBuilder}

import scala.reflect.ClassTag

class Module extends EmptyModule
  with DatabaseBindings

class EmptyModule extends AbstractModule {
  def configure(): Unit = {}

  protected def bind[T](implicit tag: ClassTag[T]): RichAnnotatedBindingBuilder[T] =
    new RichAnnotatedBindingBuilder[T](bind(tag.runtimeClass.asInstanceOf[Class[T]]))

  class RichAnnotatedBindingBuilder[T](builder: LinkedBindingBuilder[T]) extends LinkedBindingBuilder[T] {
    def to[U <: T](implicit tag: ClassTag[U]): ScopedBindingBuilder = builder.to(tag.runtimeClass.asInstanceOf[Class[U]])

    override def toInstance(instance: T): Unit = builder.toInstance(instance)

    override def to(implementation: Class[_ <: T]): ScopedBindingBuilder = builder.to(implementation)

    override def to(implementation: TypeLiteral[_ <: T]): ScopedBindingBuilder = builder.to(implementation)

    override def to(targetKey: Key[_ <: T]): ScopedBindingBuilder = builder.to(targetKey)

    override def toProvider(provider: Provider[_ <: T]): ScopedBindingBuilder = builder.toProvider(provider)

    override def toProvider(provider: javax.inject.Provider[_ <: T]): ScopedBindingBuilder = builder.toProvider(provider)

    override def toProvider(providerType: Class[_ <: javax.inject.Provider[_ <: T]]): ScopedBindingBuilder = builder.toProvider(providerType)

    override def toProvider(providerType: TypeLiteral[_ <: javax.inject.Provider[_ <: T]]): ScopedBindingBuilder = builder.toProvider(providerType)

    override def toProvider(providerKey: Key[_ <: javax.inject.Provider[_ <: T]]): ScopedBindingBuilder = builder.toProvider(providerKey)

    override def toConstructor[S <: T](constructor: Constructor[S]): ScopedBindingBuilder = builder.toConstructor(constructor)

    override def toConstructor[S <: T](constructor: Constructor[S], `type`: TypeLiteral[_ <: S]): ScopedBindingBuilder = builder.toConstructor(constructor,`type`)

    override def in(scopeAnnotation: Class[_ <: Annotation]): Unit = builder.in(scopeAnnotation)

    override def in(scope: Scope): Unit = builder.in(scope)

    override def asEagerSingleton(): Unit = builder.asEagerSingleton()
  }

}

trait DatabaseBindings extends EmptyModule {
  override def configure(): Unit = {
    super.configure()
    bind[DBConnector].to[H2Connector]
  }
}
