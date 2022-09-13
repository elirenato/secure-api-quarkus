package com.mycompany.resources;

import com.mycompany.entities.StateProvince;
import com.mycompany.services.StateProvinceService;
import io.quarkus.security.Authenticated;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/api/states-provinces")
public class StateProvinceResource {

    private StateProvinceService stateProvinceService;

    public StateProvinceResource(StateProvinceService stateProvinceService) {
        this.stateProvinceService = stateProvinceService;
    }

    @Path("/{id}")
    @GET
    @Authenticated
    public StateProvince getStateProvince(@PathParam("id") Integer id) {
        return stateProvinceService.getStateProvince(id);
    }

    @GET
    @Authenticated
    public List<StateProvince> listAllStatesProvinces(@NotNull @QueryParam("country") Integer countryId) {
        return stateProvinceService.listAllStatesProvinces(countryId);
    }
}