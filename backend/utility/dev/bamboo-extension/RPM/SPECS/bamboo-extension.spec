%define __os_install_post    \
  /usr/lib/rpm/redhat/brp-compress \
  %{!?__debug_package:/usr/lib/rpm/redhat/brp-strip %{__strip}} \
  /usr/lib/rpm/redhat/brp-strip-static-archive %{__strip} \
  /usr/lib/rpm/redhat/brp-strip-comment-note %{__strip} %{__objdump} \
  /usr/lib/rpm/brp-python-bytecompile \
%{nil}

Name: bamboo-extension
Version: 0.1.0.1
Release: ssv1.%{_os_release}
License: Commercial
Group: System Environment/Daemons
Summary: Bamboo Extension
Source0: bamboo-extension-%version.tar.gz
BuildArch: noarch

Buildroot: %{_tmppath}/%{name}-%{version}-buildroot

Requires: memcached

Requires: python-ldap
Requires: python-memcached
Requires: python-devel
Requires: python-lxml
Requires: python-crypto >= 2.0.1
Requires: python-virtualenv = 1.10.1

BuildRequires: python-devel

Provides: bamboo-extension = %{version}

%description
Bamboo Extension

%prep

%setup -n bamboo-extension-%version

%build

pushd bamboo-extension
    python setup.py sdist
popd

%install
%__rm -rf %buildroot/

%__mkdir_p %{buildroot}/opt/bamboo-extension
%__mkdir_p %{buildroot}/opt/bamboo-extension/bin
%__mkdir_p %{buildroot}/opt/bamboo-extension/etc
%__mkdir_p %{buildroot}/opt/bamboo-extension/lib
%__mkdir_p %{buildroot}/opt/bamboo-extension/lib/packages
%__mkdir_p %{buildroot}/u01/bamboo-extension/var
%__mkdir_p %{buildroot}/u01/bamboo-extension/var/log
%__mkdir_p %{buildroot}/u01/bamboo-extension/var/run

# config
%__cp bamboo-extension/data/config/settings.json %{buildroot}/opt/bamboo-extension/etc/config.json
%__cp bamboo-extension/data/certs/default.crt %{buildroot}/opt/bamboo-extension/etc/
%__cp bamboo-extension/data/certs/default.key %{buildroot}/opt/bamboo-extension/etc/

# bin
ln -s /opt/bamboo-extension/lib/env/bin/bx %{buildroot}/opt/bamboo-extension/bin/bx

# lib
%__cp -r bamboo-extension/data/packages/* %{buildroot}/opt/bamboo-extension/lib/packages
%__cp -r bamboo-extension/dist/bamboo_extension-%{version}.tar.gz %{buildroot}/opt/bamboo-extension/lib/packages


%post
[ -d /opt/bamboo-extension/lib/env ] && rm -rf /opt/bamboo-extension/lib/env || :
virtualenv --system-site-packages --never-download /opt/bamboo-extension/lib/env
/opt/bamboo-extension/lib/env/bin/pip install /opt/bamboo-extension/lib/packages/cssselect-0.9.1.tar.gz
/opt/bamboo-extension/lib/env/bin/pip install /opt/bamboo-extension/lib/packages/pyquery-1.2.6.zip
/opt/bamboo-extension/lib/env/bin/pip install /opt/bamboo-extension/lib/packages/tornado-3.1.1.tar.gz
/opt/bamboo-extension/lib/env/bin/pip install /opt/bamboo-extension/lib/packages/bamboo_extension-%{version}.tar.gz

%preun
[ "$1" = "0" ] && rm -rf /opt/bamboo-extension/lib/env || :

%files

%defattr(-, root, root)
/opt/bamboo-extension/bin/*
/opt/bamboo-extension/lib/*

%defattr(-, maint, maint)
%dir /u01/bamboo-extension/var
/u01/bamboo-extension/var/*
%config /opt/bamboo-extension/etc/config.json
%config /opt/bamboo-extension/etc/default.key
%config /opt/bamboo-extension/etc/default.crt

%clean
%__rm -rf %{buildroot}

%changelog
* Wed Oct 28 2013 Serge V. Sergeev <serge@ocslab.com> 2.7.0.0-ssv1.el5
Initial release
