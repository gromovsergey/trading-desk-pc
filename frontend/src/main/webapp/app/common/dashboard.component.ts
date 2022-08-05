import { Component }    from '@angular/core';
import { RouterModule } from '@angular/router';

import { FileService }         from '../shared/file.service';
import { PageComponent }       from '../shared/page.component';
import { FileUploadComponent } from '../shared/file_upload.component';
import { UserSessionModel }    from '../user/user_session.model';

@Component({
    selector: 'ui-dashboard',
    templateUrl: 'dashboard.html'
})

export class DashboardComponent extends PageComponent{

    public title:string = 'My Dashboard';

    public user: UserSessionModel = new UserSessionModel();
}
