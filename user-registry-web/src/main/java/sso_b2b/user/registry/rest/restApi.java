/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sso_b2b.user.registry.rest;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author vasil
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class restApi {

    @Path("/test")
    @GET
    public Response test() {
        Response res;
        return Response.status(Response.Status.NOT_MODIFIED).entity("Database error").build();
    }
}
