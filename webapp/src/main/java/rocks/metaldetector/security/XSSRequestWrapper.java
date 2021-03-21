package rocks.metaldetector.security;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

  private final HttpServletRequest request;
  private final ResettableServletInputStream servletStream;
  private byte[] rawData;

  public XSSRequestWrapper(HttpServletRequest request) {
    super(request);
    this.request = request;
    this.servletStream = new ResettableServletInputStream();
  }

  public void resetInputStream(byte[] newRawData) {
    rawData = newRawData;
    servletStream.stream = new ByteArrayInputStream(newRawData);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (rawData == null) {
      rawData = IOUtils.toByteArray(this.request.getReader(), Charset.defaultCharset());
      servletStream.stream = new ByteArrayInputStream(rawData);
    }
    return servletStream;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    if (rawData == null) {
      rawData = IOUtils.toByteArray(this.request.getReader(), Charset.defaultCharset());
      servletStream.stream = new ByteArrayInputStream(rawData);
    }
    return new BufferedReader(new InputStreamReader(servletStream));
  }

  @Override
  public String[] getParameterValues(String parameter) {
    String[] values = super.getParameterValues(parameter);
    if (values == null) {
      return null;
    }
    int count = values.length;
    String[] strippedValues = new String[count];
    for (int i = 0; i < count; i++) {
      strippedValues[i] = XSSUtils.stripXSS(values[i]);
    }
    return strippedValues;
  }

  @Override
  public String getParameter(String parameter) {
    String value = super.getParameter(parameter);
    return XSSUtils.stripXSS(value);
  }

  @Override
  public String getHeader(String name) {
    String value = super.getHeader(name);
    return XSSUtils.stripXSS(value);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    List<String> result = new ArrayList<>();
    Enumeration<String> headers = super.getHeaders(name);
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      String[] tokens = header.split(",");
      for (String token : tokens) {
        result.add(XSSUtils.stripXSS(token));
      }
    }
    return Collections.enumeration(result);
  }

  private static class ResettableServletInputStream extends ServletInputStream {

    private InputStream stream;

    @Override
    public int read() throws IOException {
      return stream.read();
    }

    @Override
    public boolean isFinished() {
      return false;
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }
  }
}
