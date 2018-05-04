package ar.edu.unq.arqsoft.utils

import java.security.MessageDigest

object Hash {
  def apply(s: String): String = {
    new String(MessageDigest.getInstance("MD5").digest(s.getBytes))
  }
}
