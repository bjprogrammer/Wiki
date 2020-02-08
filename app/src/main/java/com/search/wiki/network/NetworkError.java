package com.search.wiki.network;

import java.io.IOException;
import retrofit2.HttpException;

public class NetworkError extends Throwable {
    public static final String DEFAULT_ERROR_MESSAGE = "Something went wrong!";
    public static final String NETWORK_ERROR_MESSAGE = "Network Issue! Try after sometime";
    private final Throwable error;

    public NetworkError(Throwable e) {
        super(e);
        this.error = e;
    }

    public String getMessage() {
        return error.getMessage();
    }

    public String getAppErrorMessage() {
        if (this.error instanceof IOException) return NETWORK_ERROR_MESSAGE;
        if (!(this.error instanceof HttpException)) return DEFAULT_ERROR_MESSAGE;

        retrofit2.Response<?> response = ((HttpException) this.error).response();
            if (response != null) {
                if(response.code()==500){
                    return  "Server issue! Contact admin";
                }
                else if(response.code()==401){
                    return "Auth token expired. Login again";
                }
                else if(response.code()==550){
                    return "Auth token does not match. Login again";
                }
                else if(response.code()==400){
                    return "Invalid login credentials";
                }
                else if(response.code()==404){
                    return "Record not found";
                }
            }

        return DEFAULT_ERROR_MESSAGE;
    }


    public Throwable getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkError that = (NetworkError) o;
        return error != null ? error.equals(that.error) : that.error == null;
    }

    @Override
    public int hashCode() {
        return error != null ? error.hashCode() : 0;
    }
}