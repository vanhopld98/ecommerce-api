package vn.com.ecommerceapi.interceptor;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import vn.com.ecommerceapi.logging.LoggingFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class HttpResponseWrapper extends HttpServletResponseWrapper {

    private static final Logger LOGGER = LoggingFactory.getLogger(HttpResponseWrapper.class);

    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ServletOutputStreamCopier copier;

    public HttpResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copier = new ServletOutputStreamCopier(outputStream);
        }

        return copier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copier.flush();
        }
    }

    public byte[] getCopy() {
        if (copier != null) {
            return copier.getCopy();
        } else {
            return new byte[0];
        }
    }

}

class ServletOutputStreamCopier extends ServletOutputStream {

    private final OutputStream outputStream;
    private final ByteArrayOutputStream copy;

    public ServletOutputStreamCopier(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.copy = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        copy.write(b);
    }

    public byte[] getCopy() {
        return copy.toByteArray();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        // no thing
    }

}
