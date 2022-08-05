import {AuthService} from './auth.service';
import {FileService} from './file.service';
import {QuickSearchService} from './quick-search.service';

export const SHARED_SERVICES = [
  AuthService,
  FileService,
  QuickSearchService
];
