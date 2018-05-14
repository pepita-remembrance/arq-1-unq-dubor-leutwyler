package ar.edu.unq.arqsoft.utils

import java.security.MessageDigest

import ar.edu.unq.arqsoft.maybe.{CompareError, Maybe, Something}

object Hash {
  def apply(s: String): String = hash(s)

  def hash(s: String): String =
    new String(MessageDigest.getInstance("MD5").digest(s.getBytes))

  def compare(raw: String, hashed: String): Maybe[Unit] =
    if (hash(raw) == hashed) Something(())
    else CompareError
}
