/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sso_b2b.user.registry.rest;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import sso_b2b.user.registry.beans.Individual;

/**
 *
 * @author vasil
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class restApi {

    private final Logger log = Logger.getLogger(getClass().getName());
    @Context
    private Request request;
    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders requestHeaders;
    @Context
    private Response response;

    private static EntityManagerFactory emf;
    private EntityManager em;

    /**
     *
     */
    public restApi() {
        getEM();
    }

    private EntityManager getEM() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("user-registry-web_JPA");
        }
        this.em = this.emf.createEntityManager();
        return this.em;
    }

    /**
     *
     * @return
     */
    @Path("/test")
    @GET
    public Response test() {
        Response res;
        return Response.status(Response.Status.NOT_MODIFIED).entity("Database error").build();
    }

    /**
     * Получить список всех физ. лиц.
     *
     * @param p_realm
     * @return
     */
    @Path("/realms/{realm}/individual")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndividualList(@PathParam("realm") String p_realm) {
        try {
            throw new Exception(p_realm);
            /*log.info(String.format("getIndividualList = %s", em));
            Individual item = new Individual();
            item.setFirdtName(p_realm);
            List<Individual> result = new LinkedList();
            result.add(item);
            return Response.status(Response.Status.OK).entity(result).build();*/
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     *
     * @param p_realm
     * @param user
     * @return
     */
    @Path("/realms/{realm}/individual")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response addIndividual(@PathParam("realm") String p_realm, Individual user) {
        try {
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    @Path("/realms/{realm}/individual/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response updIndividual(@PathParam("realm") String p_realm, @PathParam("id") Long userID) {
        try {
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    @Path("/realms/{realm}/individual/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public Response delIndividual(@PathParam("realm") String p_realm, @PathParam("id") Long userID) {
        try {
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }
}
