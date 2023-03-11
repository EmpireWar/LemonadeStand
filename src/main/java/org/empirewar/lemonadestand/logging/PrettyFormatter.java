package org.empirewar.lemonadestand.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class PrettyFormatter extends SimpleFormatter {

    private static final String FORMAT = "[%1$tc %4$s]: %5$s%6$s%n";

    @Override
    public String format(LogRecord record) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(
                record.getInstant(), ZoneId.systemDefault());

        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
               source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }

        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return String.format(FORMAT,
                             zdt,
                             source,
                             record.getLoggerName(),
                             record.getLevel().getLocalizedName(),
                             message,
                             throwable);
    }
}
