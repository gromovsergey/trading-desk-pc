--
-- See OUI-21429 Clear old stats on ADSERVER_TC and ADSERVER_EC after refresh
--
--
create table channelinventory_ as select * from channelinventory where 1=0;
insert /*+ APPEND */ into channelinventory_ select * from channelinventory where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table channelinventory;
insert /*+ APPEND */ into channelinventory select * from channelinventory_;
drop table channelinventory_ purge;

create table channelusagestats_ as select * from channelusagestats where 1=0;
insert /*+ APPEND */ into channelusagestats_ select * from channelusagestats where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table channelusagestats;
insert /*+ APPEND */ into channelusagestats select * from channelusagestats_;
drop table channelusagestats_ purge;

create table requeststatshourly_ as select * from requeststatshourly where 1=0;
insert /*+ APPEND */ into requeststatshourly_ select * from requeststatshourly where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table requeststatshourly;
insert /*+ APPEND */ into requeststatshourly select * from requeststatshourly_;
drop table requeststatshourly_ purge;

create table requeststatsdailygb_ as select * from requeststatsdailygb where 1=0;
insert /*+ APPEND */ into requeststatsdailygb_ select * from requeststatsdailygb where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table requeststatsdailygb;
insert /*+ APPEND */ into requeststatsdailygb select * from requeststatsdailygb_;
drop table requeststatsdailygb_ purge;

create table requeststatsdailyisp_ as select * from requeststatsdailyisp where 1=0;
insert /*+ APPEND */ into requeststatsdailyisp_ select * from requeststatsdailyisp where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table requeststatsdailyisp;
insert /*+ APPEND */ into requeststatsdailyisp select * from requeststatsdailyisp_;
drop table requeststatsdailyisp_ purge;

create table requeststatsdailygmt_ as select * from requeststatsdailygmt where 1=0;
insert /*+ APPEND */ into requeststatsdailygmt_ select * from requeststatsdailygmt where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table requeststatsdailygmt;
insert /*+ APPEND */ into requeststatsdailygmt select * from requeststatsdailygmt_;
drop table requeststatsdailygmt_ purge;

create table requeststatsdailytr_ as select * from requeststatsdailytr where 1=0;
insert /*+ APPEND */ into requeststatsdailytr_ select * from requeststatsdailytr where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table requeststatsdailytr;
insert /*+ APPEND */ into requeststatsdailytr select * from requeststatsdailytr_;
drop table requeststatsdailytr_ purge;

create table requeststatsdailybr_ as select * from requeststatsdailybr where 1=0;
insert /*+ APPEND */ into requeststatsdailybr_ select * from requeststatsdailybr where sdate > trunc(trunc(sysdate, 'Q') - 1, 'Q');
truncate table requeststatsdailybr;
insert /*+ APPEND */ into requeststatsdailybr select * from requeststatsdailybr_;
drop table requeststatsdailybr_ purge;

create table auditlog_ as select * from auditlog where 1=0;
insert /*+ APPEND */ into auditlog_ select * from auditlog where log_date > sysdate - 31;
truncate table auditlog;
insert /*+ APPEND */ into auditlog select * from auditlog_;
drop table auditlog_ purge;

truncate table webwisediscoveritem;
truncate table channeltriggerstats;
truncate table interfacedatasent;
truncate table userproperties;
truncate table replication_heartbeat;
truncate table requesttriggerstats;
truncate table channelrequeststatshourly;
truncate table requesttriggerchannelstats;
truncate table webwisediscoveritemstats;
truncate table webwisediscovertagstats;

exit
