package app.programmatic.ui.common.restriction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import app.programmatic.ui.common.restriction.service.LocalRestrictionServiceImpl;

@RestController
public class LocalRestrictionController {

    @Autowired
    private LocalRestrictionServiceImpl restrictionService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/restriction/local", produces = "application/json")
    public boolean isAllowed(@RequestParam(value = "name") String name,
                             @RequestParam(value = "entityId", required = false) Long entityId)
            throws HttpMessageNotReadableException {

        if (entityId == null) {
            return restrictionService.isAllowed(name);
        }
        return restrictionService.isAllowed(name, entityId);
    }
}
