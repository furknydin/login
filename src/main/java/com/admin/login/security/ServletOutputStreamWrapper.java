package com.admin.login.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ServletOutputStreamWrapper extends ServletOutputStream {

    private OutputStream outputStream;
    private ByteArrayOutputStream copyByteArrayOutputStream;

    public ServletOutputStreamWrapper(OutputStream outputStream) {
        this.outputStream = outputStream;
        copyByteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        copyByteArrayOutputStream.write(b);
    }

    public byte[] getCopy(){
        return copyByteArrayOutputStream.toByteArray();
    }
}