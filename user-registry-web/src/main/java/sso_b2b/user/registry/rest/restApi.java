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
import javax.persistence.TypedQuery;
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
            log.info(String.format("getIndividualList \n\trealm = %s", p_realm));
            List<Individual> result = new LinkedList();
            getEM();
            em.getTransaction().begin();
            TypedQuery<Individual> query = em.createNamedQuery("Individual.findAll", Individual.class);
            result = query.getResultList();
            em.getTransaction().commit();
            em.close();
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/realms/{realm}/individual/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndividual(@PathParam("realm") String p_realm, @PathParam("id") Long id) {
        try {
            log.info(String.format("getIndividual \n\trealm = %s\n\tid=%s", p_realm, id));
            Individual result = null;
            getEM();
            em.getTransaction().begin();
            result = em.find(Individual.class, id);
            em.getTransaction().commit();
            em.close();
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Добавить
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
            log.info(String.format("addIndividual \n\trealm = %s \n\tuser = %s", p_realm, user));
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            em.close();
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    /**
     * Обновить
     *
     * @param p_realm
     * @param userID
     * @return
     */
    @Path("/realms/{realm}/individual/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response updIndividual(@PathParam("realm") String p_realm, @PathParam("id") Long userID, Individual user) {
        try {
            log.info(String.format("updIndividual \n\trealm = %s \n\tuserID = %s", p_realm, userID));
            em.getTransaction().begin();
            Individual item = em.find(Individual.class, userID);
            if (item != null) {
                user.setId(userID);
                em.merge(user);
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity(String.format("Запись c id = %s не найдена.", userID)).build();
            }
            em.getTransaction().commit();
            em.close();
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    /**
     * Удалить
     *
     * @param p_realm
     * @param userID
     * @return
     */
    @Path("/realms/{realm}/individual/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public Response delIndividual(@PathParam("realm") String p_realm, @PathParam("id") Long userID) {
        try {
            log.info(String.format("delIndividual \n\trealm = %s \n\tuserID = %s", p_realm, userID));
            em.getTransaction().begin();
            Individual item = em.find(Individual.class, userID);
            if (item != null) {
                em.detach(item);
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity(String.format("Запись c id = %s не найдена.", userID)).build();
            }
            em.getTransaction().commit();
            em.close();
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }
}
