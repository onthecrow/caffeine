package com.onthecrow.caffeine.core.logger

import android.content.Context
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern

object FileLogger: Logger {

    private lateinit var logFile: File
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    private const val MAX_TAG_LENGTH = 23

    private val ignoreClassNames = listOf(
        FileLogger::class.java.name,
        Logger::class.java.name,
    )

    fun initialize(context: Context) {
        val appDir = context.getExternalFilesDir(null)
        logFile = File(appDir?.path + "/log.md")
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

    override fun log(message: String) {
        checkIfInitialized()
        val logMessageString = buildString {
            append(dateFormatter.format(Date()))
            append(' ')
            val tag = Throwable().stackTrace
                .first { it.className !in ignoreClassNames }
                .let(::createStackElementTag)
            append(tag)
            append(' ')
            append(message)
        }
        FileOutputStream(logFile, true).use { stream ->
            stream.bufferedWriter().use { writer ->
                writer.appendLine(logMessageString)
            }
        }
    }

    private fun checkIfInitialized() {
        if (!::logFile.isInitialized) {
            throw IllegalStateException("You should call FileLogger.initialize() in Application.onCreate()!")
        }
    }

    private fun createStackElementTag(element: StackTraceElement): String? {
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        // Tag length limit was removed in API 26.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
            tag
        } else {
            tag.substring(0, MAX_TAG_LENGTH)
        }
    }
}