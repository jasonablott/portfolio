package com.a6server.util

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlin.text.toCharArray


fun hashPassword(password: String) : String =
    BCrypt.withDefaults().hashToString(14, password.toCharArray())