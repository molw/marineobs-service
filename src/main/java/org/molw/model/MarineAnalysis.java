package org.molw.model;


import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

@ApplicationScoped
public class MarineAnalysis {

    @Inject
    EntityManager em;


    /*
    For the hibernate ones we will do one where we get all the values for the days and then average each hour in the Java Code
    And then another where we try to do the avg for the hours in the HQL
    I don't think I can use the HQL to get the day directly and I don't think I can use my cool time functions either,
    so to get the day we will have to do a where month == this month and day == this day.
    This will give slightly different results from DOY but both have problems

     */

    @Transactional
    @Timed(name = "HibernateQuery", description = "The best I can do in hibernate", unit = MetricUnits.MILLISECONDS)
    public ArrayList daysNoAvgHibernate(){
        ArrayList results = new ArrayList();

        String beginningJPQL = "select m.identification, extract(DOY FROM m.timeOfObservation), m.airTemperature, m.seaSurfaceTemperature from Marineobs m ";
        String endingJPQL = "WHERE  extract(DOY FROM m.timeOfObservation) = extract(DOY FROM CURRENT_TIMESTAMP)";
        Query query = em.createQuery(beginningJPQL + endingJPQL);
        /*
        final Session session = (Session) em.getDelegate();
        final SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
        final Dialect dialect = sessionFactory.getJdbcServices().getDialect();
        Map functions = dialect.getFunctions();
        System.out.println(functions);
        */

        results = (ArrayList) query.getResultList();
        
        return results;
    }
    @Transactional
    @Timed(name = "HibernateAvgQuery", description = "The best avg I can do in hibernate", unit = MetricUnits.MILLISECONDS)
    public ArrayList daysAvgHibernate(){
        ArrayList results = new ArrayList();

        String beginningJPQL = "select m.identification, max(extract(HOUR FROM m.timeOfObservation)), avg(m.airTemperature), avg(m.seaSurfaceTemperature) from Marineobs m ";
        String endingJPQL = "WHERE  extract(DOY FROM m.timeOfObservation) = extract(DOY FROM CURRENT_TIMESTAMP) " +
                "group by m.identification, extract(HOUR from m.timeOfObservation) " +
                "order by m.identification, extract(HOUR from m.timeOfObservation)";

        Query query = em.createQuery(beginningJPQL + endingJPQL);

        results = (ArrayList) query.getResultList();

        return results;
    }

    @Transactional
    @Timed(name = "NativeQuery", description = "Able to use the index", unit = MetricUnits.MILLISECONDS)
    public ArrayList daysNoAvgNative(){
        ArrayList<Object> results = new ArrayList<Object>();
        String beginJPQL = "select identification, extract(DOY from timezone('PST', time_of_observation)), air_temperature, sea_surface_temperature from marineobs ";
        String endJPQL = "where extract(DOY from timezone('PST', time_of_observation)) = extract(DOY from timezone('PST', now())) ";

        Query q = em.createNativeQuery(beginJPQL + endJPQL);
        results = (ArrayList<Object>) q.getResultList();

        return results;
    }

    @Transactional
    @Timed(name = "NativeAverageQuery", description = "Able to use the index now with Averaging", unit = MetricUnits.MILLISECONDS)
    public ArrayList daysAvgNative(){
        ArrayList<Object> results = new ArrayList<Object>();
        String beginJPQL = "select identification, max(extract(HOUR from timezone('PST', time_of_observation))) as hour, " +
                "avg(air_temperature) as avg_air, avg(sea_surface_temperature) as avg_sea from marineobs ";
        String endJPQL = "where extract(DOY from timezone('PST', time_of_observation)) = extract(DOY from timezone('PST', now()))" +
                "group by identification, extract(HOUR from timezone('PST', time_of_observation)) " +
                "order by identification, extract(HOUR from timezone('PST', time_of_observation))";

        Query q = em.createNativeQuery(beginJPQL + endJPQL);
        results = (ArrayList<Object>) q.getResultList();

        return results;
    }

}
