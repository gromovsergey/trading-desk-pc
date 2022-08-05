import {AuthService} from './auth.service';
import {AuthGuard} from './auth.guard';
import {CommonService} from './common.service';
import {GeoService} from './geo.service';

export const COMMON_SERVICES = [
  AuthService,
  AuthGuard,
  CommonService,
  GeoService
];
