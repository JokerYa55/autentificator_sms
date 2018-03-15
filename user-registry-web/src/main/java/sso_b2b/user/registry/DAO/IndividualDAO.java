/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sso_b2b.user.registry.DAO;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.jboss.logging.Logger;
import org.json.simple.parser.JSONParser;
import rtk.httpUtil.utlhttp;
import sso_b2b.user.registry.beans.Individual;

/**
 *
 * @author vasil
 */
public class IndividualDAO implements daoInterface<Individual, Long> {

    private final Logger log = Logger.getLogger(getClass().getName());
    @Override
    public EntityManager getEM() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Logger getLog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual addItem(Individual Item) {
        return daoInterface.super.addItem(Item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteItem(Individual Item) {
        return daoInterface.super.deleteItem(Item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateItem(Individual Item) {
        return daoInterface.super.updateItem(Item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual getItem(long id, String jpqName, Class<Individual> cl) {
        Individual res = null;
        try {
            utlhttp http = new utlhttp();
            String httpRes = http.doGet(String.format("http://192.168.0.20:8080/user-registry-web/api/realms/videomanager/individual/%s", id), null, null);
            JSONParser parser = new JSONParser();
             try {
                Object obj = parser.parse(httpRes);
                res = (Individual) obj;
            } catch (Exception ex1) {
                 System.out.println(ex1.getMessage());
            }
        } catch (Exception e) {
        }
        return res;
    }

    @Override
    public List<Individual> getList(String jpqName, Class<Individual> cl) {
        return daoInterface.super.getList(jpqName, cl); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Individual> getList(String jpqName, Class<Individual> cl, Map<String, Object> params) {
        return daoInterface.super.getList(jpqName, cl, params); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Individual> getList(int startIdx, int countRec, String jpqName, Class<Individual> cl) {
        return daoInterface.super.getList(startIdx, countRec, jpqName, cl); //To change body of generated methods, choose Tools | Templates.
    }

}
