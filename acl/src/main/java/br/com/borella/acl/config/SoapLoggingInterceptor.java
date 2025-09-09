package br.com.borella.acl.config;

import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.WebServiceMessage;

import java.io.ByteArrayOutputStream;

public class SoapLoggingInterceptor implements ClientInterceptor {

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        logMessage("Request", messageContext.getRequest());
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        logMessage("Response", messageContext.getResponse());
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        logMessage("Fault", messageContext.getResponse());
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) {
        // nada aqui
    }

    private void logMessage(String label, WebServiceMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            System.out.println(label + ":\n" + out.toString("UTF-8") + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

