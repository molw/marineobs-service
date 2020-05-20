package org.molw;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.molw.model.MarineAnalysis;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/marineobs")
public class EntryPoint {

    @Inject
    MarineAnalysis ma;

    @GET
    @Path("/hibernate")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(name = "HibernateAPITime", description = "Hibernate API", unit = MetricUnits.MILLISECONDS)
    public ArrayList hibernate()
    {

        return ma.daysNoAvgHibernate();
    }

    @GET
    @Path("/hibernateavg")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(name = "HibernateAVGAPITime", description = "Hibernate AVG API", unit = MetricUnits.MILLISECONDS)
    public ArrayList hibernateAvg()
    {

        return ma.daysAvgHibernate();
    }

    @GET
    @Path("/native")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(name = "NativeAPITime", description = "Native API", unit = MetricUnits.MILLISECONDS)
    public ArrayList nativeQuery()
    {

        return ma.daysNoAvgNative();
    }

    @GET
    @Path("/nativeavg")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(name = "NativeAvgAPITime", description = "Native API", unit = MetricUnits.MILLISECONDS)
    public ArrayList nativeAvgQuerry()
    {

        return ma.daysAvgNative();
    }

}