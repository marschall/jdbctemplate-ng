package com.github.marschall.jdbctemplateng;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DerbyLogBridge {

  public static Writer errorMethod() {
    return new Slf4jLogWriter(LoggerFactory.getLogger("org.apache.derby"));
  }

  static final class Slf4jLogWriter extends Writer {

    private final Logger logger;

    private final StringBuilder buffer;


    Slf4jLogWriter(Logger logger) {
      this.logger = logger;
      this.buffer = new StringBuilder();
    }

    @Override
    public void write(int ch) {
      synchronized (this.lock) {
        if ((ch == '\n') && (this.buffer.length() > 0)) {
          this.logger.info(this.buffer.toString());
          this.buffer.setLength(0);
        } else {
          this.buffer.append((char) ch);
        }

      }
    }

    @Override
    public void write(char[] buffer, int offset, int length) {
      synchronized (this.lock) {
        for (int i = 0; i < length; i++) {
          char ch = buffer[offset + i];
          if ((ch == '\n') && (this.buffer.length() > 0)) {
            this.logger.info(this.buffer.toString());
            this.buffer.setLength(0);
          } else {
            this.buffer.append(ch);
          }
        }
      }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
      synchronized (this.lock) {
        int newlineIndex = str.indexOf('\n', off);
        if ((newlineIndex == -1) || (newlineIndex > (off + len))) {
          this.buffer.append(str, off, off + len);
        } else {
          for (int i = off; i < len; i++) {
            this.write(str.charAt(i));
          }
        }
      }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

  }

}
