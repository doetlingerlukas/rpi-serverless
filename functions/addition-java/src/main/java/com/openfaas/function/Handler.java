package com.openfaas.function;

import java.util.stream.Stream;

import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {

        Response res = new Response();
        res.setBody(Integer.toString(Stream.of(req.getBody().split(","))
                .map(Integer::parseInt)
                .mapToInt(Integer::intValue)
                .sum()));

	    return res;
    }
}
