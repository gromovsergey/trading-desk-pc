%if %{?__target:1}%{!?__target:0}
%define _target_cpu %{__target}
%endif

%define __os_install_post    \
  /usr/lib/rpm/redhat/brp-compress \
  %{!?__debug_package:/usr/lib/rpm/redhat/brp-strip %{__strip}} \
  /usr/lib/rpm/redhat/brp-strip-static-archive %{__strip} \
  /usr/lib/rpm/redhat/brp-strip-comment-note %{__strip} %{__objdump} \
  /usr/lib/rpm/brp-python-bytecompile \
%{nil}

# UI
%define __ui_dir             /opt/foros/ui
%define __ui_bin_dir         /opt/foros/ui/bin
%define __ui_lib_dir         /opt/foros/ui/lib
%define __domain_dir         /opt/foros/ui/lib/domains
%define __domain1_lib_dir    /opt/foros/ui/lib/domains/domain1/lib
%define __autodeploy_dir     /opt/foros/ui/lib/autodeploy
%define __share_dir          /opt/foros/ui/share
%define __reports_dir        /opt/foros/ui/share/reports
%define __customizations_dir /opt/foros/ui/share/customizations

%define __cms_plugin_name    ui-plugin-%{version}
%define __cms_plugin_dst_dir /opt/cms/var/spool

%define __maven_cache %_tmppath/%name-%version-m2
%define __osbe_build_dir     .

Name: foros-ui
Version: 2.7.0.0
Release: ssv1%{?dist}
License: Commercial
Group: System Environment/Daemons
Summary: FOROS UI
Conflicts: ad-mpt
Source0: foros-ui-%version.tar.gz

Buildroot: %{_tmppath}/%{name}-%{version}-buildroot

# Cron Jobs
Requires: cronie
Requires: logrotate

# File Sender to AdServer
Requires: rsync

# Birt Report Installation and Migrations
Requires: python
Requires: postgresql94
Requires: postgresql94-libs
Requires: python-psycopg2 >= 2.4.2

# Monitoring
%{lua:
if rpm.expand("%{?rhel}") == "7" then
  print("Requires: net-snmp-subagent-shell >= 2.1.0.1\n")
else
  print("Requires: net-snmp-subagent >= 2.1.0.1\n")
end
}
Requires: curl
Requires: procps

# FOROS UI
%{lua:
if rpm.expand("%{?rhel}") == "7" then
print("Requires: httpd >= 1:2.4.10.1-1.ssv1.el7\n")
print("Requires: mod_ssl >= 1:2.4.10.1-1.ssv1.el7\n")
else
print("Requires: httpd\n")
print("Requires: mod_ssl\n")
end
}

Requires: mod_tomcat-connector
Requires: glassfish = 3.1.2.2
Requires: java-1.7.0-oracle = 1:1.7.0.79 java-1.7.0-oracle-devel = 1:1.7.0.79
Requires: java-oracle-tzdata >= 2015a
Requires: foros-polyglot-dict
Requires: which >= 2.16-7
%{lua:
if rpm.expand("%{?rhel}") == "7" then
print("Requires: memcached = 1.4.21\n")
else
print("Requires: memcached = 1.4.20\n")
end
}
Requires: net-tools >= 1.60-82.el5
Requires: GeoIP-City-CSV = 1:20150901-ssv2

%{lua:
if rpm.expand("%{?rhel}") == "7" then
  print("BuildRequires: maven >= 3.0.5-16.el7\n")
else
  print("BuildRequires: maven2 >= 2.2.1-ssv4.el5\n")
end
}
BuildRequires: java-1.7.0-oracle-devel = 1:1.7.0.79
BuildRequires: glassfish = 3.1.2.2 ant >= 1.6.5
BuildRequires: ace-tao-devel >= 6.1.0.1
BuildRequires: net-snmp-devel >= 5.5
BuildRequires: openssl-devel >= 1.0.0
BuildRequires: OpenSBE >= 1.0.50 OpenSBE-defs >= 1.0.17.0
BuildRequires: autoconf GeoIP-devel >= 1.4.8
BuildRequires: gcc-c++ pcre-devel libevent-devel bzip2-devel apr-devel httpd-devel redhat-rpm-config
BuildRequires: zip saxon xsd xerces-c-devel wget

Provides: foros-ui = %{version}

%description
FOROS UI is a web portal for managment advertising entities, reporting and runtime configuration of the FOROS platform.
It includes interfaces for five roles:
- Advertiser and Agency: Manage campaigns, creatives, channels, payments. Access reporting.
- Publisher: Manage sites, moderate creatives. Access reporting
- ISP: Access reporting.
- Internal: Monitor and moderate all transactions. Full access and advanced reporting.

%ifarch noarch

%package -n foros-ui-cms
Summary:  Foros UI cms plugin
Group:    System Environment/Daemons

# CMS Config files generation
Requires: python-jinja2 >= 2.2.1
Requires: libxslt
Requires: openssl
Requires: rsync
BuildRequires: zip saxon xsd xerces-c-devel

%description cms
the package provides cms plugin for Foros UI

%endif

%prep

%setup -n foros-ui-%version

%ifnarch noarch

  %__mkdir_p -p unixcommons/%{__osbe_build_dir}
  cpp -DOS_%{_os_release} -DARCH_%{_target_cpu} -DARCH_FLAGS='%{__arch_flags}' unixcommons/default.config.t > unixcommons/%{__osbe_build_dir}/default.config

%endif

%build

%ifnarch noarch

  pushd unixcommons
    osbe
    product_root=`pwd`
    cd %{__osbe_build_dir}
    ${product_root}/configure --enable-no-questions --enable-guess-location=no --prefix=%{__ui_dir}
    %__make %_smp_mflags
  popd

  pushd ui
    export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF8"
    test %{rsClientTests} && export rsClientTests=true
    test %{precompileJsp} && export precompileJsp=true
    mvn install -B -Dmaven.test.skip.exec=true -Dmaven.repo.local=%__maven_cache -P Migrations
  popd

  pushd ui/utility/migration/automatic-migration
    ./build-migrations.sh %__maven_cache
  popd

  wget --timeout=60 -O ui/jmx-client.jar http://maven.ocslab.com/repository/com/foros/jmx-client/2.2/jmx-client-2.2.jar

  %__rm -rf %__maven_cache

%else
  ui/cms-plugin/create_plugin.sh ui/cms-plugin/%{__cms_plugin_name}.zip
%endif

%install
%__rm -rf %buildroot/

%ifnarch noarch

  %__mkdir_p %{buildroot}%{__ui_dir}
  %__mkdir_p %{buildroot}%{__share_dir}
  %__mkdir_p %{buildroot}%{__customizations_dir}
  %__mkdir_p %{buildroot}%{__reports_dir}

  make -C unixcommons/%{__osbe_build_dir} install destdir=%{buildroot}

  rm -rf %{buildroot}%{__ui_dir}/{bin,include,share}
  rm -rf `find %{buildroot}%{__ui_dir}/lib -type f -name '*.a'`

  # FOROS UI & Birt
  %__mkdir_p %{buildroot}%{__domain1_lib_dir}
  %__mkdir_p %{buildroot}%{__autodeploy_dir}
  %__install ui/ui-ear/target/foros-ui.ear %{buildroot}%{__autodeploy_dir}/
  %__install ui/target/lib/* %{buildroot}%{__domain1_lib_dir}

  # FOROS UI
  %__mkdir_p %{buildroot}%{__ui_bin_dir}
  %__install ui/utility/cluster/*.sh %{buildroot}%{__ui_bin_dir}/
  %__install ui/utility/cluster/*.py %{buildroot}%{__ui_bin_dir}/

  %__install ui/utility/dev/glassfish-log-filter/*.py %{buildroot}%{__ui_bin_dir}/
  %__mkdir_p %{buildroot}%{__ui_lib_dir}/ui_log_parser
  %__install ui/utility/cluster/ui_log_parser_rules.json %{buildroot}%{__ui_lib_dir}/ui_log_parser/rules.json

  %__install ui/utility/migration/automatic-migration/run-migrations.py %{buildroot}%{__ui_bin_dir}/
  %__install ui/utility/migration/automatic-migration/run-java-migration.sh %{buildroot}%{__ui_bin_dir}/
  %__cp -pr ui/utility/migration/automatic-migration/target/migrations %{buildroot}%{__ui_lib_dir}/migration

  %__mkdir_p %{buildroot}%{__reports_dir}
  %__cp -rf ui/birt-reports/* %{buildroot}%{__reports_dir}/ || :

  %__mkdir_p %{buildroot}%{__customizations_dir}
  %__cp -rf ui/customizations/* %{buildroot}%{__customizations_dir}/ || :

  %__mkdir_p %{buildroot}%{__ui_lib_dir}/jmx-client
  %__install ui/jmx-client.jar %{buildroot}%{__ui_lib_dir}/jmx-client/jmx-client.jar

  %__mkdir_p %{buildroot}%{__domain1_lib_dir}

%post
alternatives --set java                     /usr/lib/jvm/jre-1.7.0-oracle.x86_64/bin/java
alternatives --set javac                    /usr/lib/jvm/java-1.7.0-oracle.x86_64/bin/javac
alternatives --set pgsql-ld-conf            /usr/pgsql-9.4/share/postgresql-9.4-libs.conf
alternatives --set pgsql-psql               /usr/pgsql-9.4/bin/psql
alternatives --set pgsql-clusterdb          /usr/pgsql-9.4/bin/clusterdb
alternatives --set pgsql-createdb           /usr/pgsql-9.4/bin/createdb
alternatives --set pgsql-createlang         /usr/pgsql-9.4/bin/createlang
alternatives --set pgsql-createuser         /usr/pgsql-9.4/bin/createuser
alternatives --set pgsql-dropdb             /usr/pgsql-9.4/bin/dropdb
alternatives --set pgsql-droplang           /usr/pgsql-9.4/bin/droplang
alternatives --set pgsql-dropuser           /usr/pgsql-9.4/bin/dropuser
alternatives --set pgsql-pg_basebackup      /usr/pgsql-9.4/bin/pg_basebackup
alternatives --set pgsql-pg_dump            /usr/pgsql-9.4/bin/pg_dump
alternatives --set pgsql-pg_dumpall         /usr/pgsql-9.4/bin/pg_dumpall
alternatives --set pgsql-pg_restore         /usr/pgsql-9.4/bin/pg_restore
alternatives --set pgsql-reindexdb          /usr/pgsql-9.4/bin/reindexdb
alternatives --set pgsql-vacuumdb           /usr/pgsql-9.4/bin/vacuumdb
alternatives --set pgsql-clusterdbman       /usr/pgsql-9.4/share/man/man1/clusterdb.1
alternatives --set pgsql-createdbman        /usr/pgsql-9.4/share/man/man1/createdb.1
alternatives --set pgsql-createlangman      /usr/pgsql-9.4/share/man/man1/createlang.1
alternatives --set pgsql-createuserman      /usr/pgsql-9.4/share/man/man1/createuser.1
alternatives --set pgsql-dropdbman          /usr/pgsql-9.4/share/man/man1/dropdb.1
alternatives --set pgsql-droplangman        /usr/pgsql-9.4/share/man/man1/droplang.1
alternatives --set pgsql-dropuserman        /usr/pgsql-9.4/share/man/man1/dropuser.1
alternatives --set pgsql-pg_basebackupman   /usr/pgsql-9.4/share/man/man1/pg_basebackup.1
alternatives --set pgsql-pg_dumpman         /usr/pgsql-9.4/share/man/man1/pg_dump.1
alternatives --set pgsql-pg_dumpallman      /usr/pgsql-9.4/share/man/man1/pg_dumpall.1
alternatives --set pgsql-pg_restoreman      /usr/pgsql-9.4/share/man/man1/pg_restore.1
alternatives --set pgsql-psqlman            /usr/pgsql-9.4/share/man/man1/psql.1
alternatives --set pgsql-reindexdbman       /usr/pgsql-9.4/share/man/man1/reindexdb.1
alternatives --set pgsql-vacuumdbman        /usr/pgsql-9.4/share/man/man1/vacuumdb.1

%else

  %__mkdir_p %{buildroot}%{__cms_plugin_dst_dir}
  %__install -m 0644 -T ui/cms-plugin/%{__cms_plugin_name}.zip %{buildroot}%{__cms_plugin_dst_dir}/%{__cms_plugin_name}.zip.rpm

%post -n foros-ui-cms
  ln -s %{__cms_plugin_name}.zip.rpm %{__cms_plugin_dst_dir}/%{__cms_plugin_name}.zip

%preun -n foros-ui-cms
  %__rm -f %{__cms_plugin_dst_dir}/%{__cms_plugin_name}.zip ||:

%endif

%ifnarch noarch

%files
%defattr(-, root, root)
%dir %{__ui_dir}
%{__ui_bin_dir}/*
%{__ui_lib_dir}/*
%{__autodeploy_dir}
%{__domain_dir}
%{__share_dir}

%else

%files -n foros-ui-cms
%defattr(-, root, root)
%{__cms_plugin_dst_dir}/%{__cms_plugin_name}.zip.rpm

%endif

%clean
%__rm -rf %{buildroot}

%changelog
* Wed Jan 17 2007 Serge V. Sergeev <serge@121media.com> 1.0.0-ssv1.el4
- initial release
