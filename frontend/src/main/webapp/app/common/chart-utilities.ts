import * as jqplot from './jqplot-utilities';
import * as google from './google-utilities';
import { mergeObjects } from './utilities';

export default mergeObjects(jqplot.DefaultConfig, google.DefaultConfig);
