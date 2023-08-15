import java.util.logging.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.io.IOException;

public class LoggerPro {
    private static final Logger logger = Logger.getLogger(LoggerPro.class.getName());

    private static class SimpleFormatter extends Formatter {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(dateFormat.format(new Date(record.getMillis())));
            builder.append(" - ");
            builder.append(record.getMessage());
            builder.append(System.lineSeparator());
            return builder.toString();
        }
    }
    public void escribir(String mensaje) {
        try {
            logger.setLevel(Level.INFO);
            FileHandler fileHandler = new FileHandler("log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.info(mensaje);
            fileHandler.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
