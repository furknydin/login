package com.admin.login.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private ServletOutputStream servletOutputStream;
    private PrintWriter printWriter;
    private ServletOutputStreamWrapper copier;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public Map<String,String> getAllHeaders(){
        Map<String,String> headers = new HashMap<>();
        getHeaderNames().forEach(header -> headers.put(header, getHeader(header)));
        return headers;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (printWriter != null){
            throw new IllegalStateException("getWriter() has already been called on this response");
        }
        if (servletOutputStream == null){
            servletOutputStream = getResponse().getOutputStream();
            copier = new ServletOutputStreamWrapper(servletOutputStream);
        }
        return copier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (printWriter != null){
            throw new IllegalStateException("getWriter() has already been called on this response");
        }

        if (servletOutputStream != null){
            copier = new ServletOutputStreamWrapper(getResponse().getOutputStream());
            printWriter = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()));
        }

        return printWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (printWriter != null){
            printWriter.flush();
        }else if(servletOutputStream != null){
            copier.flush();
        }
    }

    public byte[] getBody(){
        if (copier != null){
            return copier.getCopy();
        }
        return new byte[0];
    }
}
