package org.empirewar.lemonadestand.logging

import java.io.PrintWriter
import java.io.StringWriter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.logging.LogRecord
import java.util.logging.SimpleFormatter

class PrettyFormatter : SimpleFormatter() {

    override fun format(record: LogRecord): String {
        val zdt = ZonedDateTime.ofInstant(
            record.instant, ZoneId.systemDefault()
        )

        var source: String?
        if (record.sourceClassName != null) {
            source = record.sourceClassName
            if (record.sourceMethodName != null) {
                source += " " + record.sourceMethodName
            }
        } else {
            source = record.loggerName
        }

        val message = formatMessage(record)

        var throwable = ""
        if (record.thrown != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            pw.println()
            record.thrown.printStackTrace(pw)
            pw.close()
            throwable = sw.toString()
        }

        return String.format(
            FORMAT,
            zdt,
            source,
            record.loggerName,
            record.level.localizedName,
            message,
            throwable
        )
    }

    companion object {
        private const val FORMAT = "[%1\$tc %4\$s]: %5\$s%6\$s%n"
    }
}