package com.duyvv.camera.utils

import java.util.regex.Pattern

fun isValidUrl(url: String): Boolean {
    val urlPattern = ("^((https?|ftp|file)://)?"
            + "(([\\w.-]+)\\.([a-z]{2,3})|"
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}))"
            + "(:[0-9]{1,5})?"
            + "(/.*)?$")
    val pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(url)
    return matcher.matches()
}