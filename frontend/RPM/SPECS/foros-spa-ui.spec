%define __product_group foros
%define __product spa-ui
%define __prefix /opt/%__product_group/%__product

Name:		%__product_group-%__product
Version:	1.0
Release:	ssv1%{?dist}
Summary:	Foros SPA UI REST Services

Group:		Applications/Internet
License:	Custom non-free
URL:		https://gitlab.ocslab.com/ui/frontend.git
Source0:  %name-%version.tar.gz	
BuildRequires:	apache-maven >= 3.3.9 java-1.8.0-devel git >= 1.7.1
Requires:	java-1.8.0-devel
BuildRoot: %{_tmppath}/%{name}-buildroot

%description
Foros SPA UI REST Services

%prep

%setup -q

%build
# ToDo: remove this workaround for Issue #494
cp -rf /tmp/node src/main/webapp/
cp -rf /tmp/node_modules src/main/webapp/

mvn -U -Dmaven.test.skip.exec=true package

%install
%__rm -rf %buildroot
%__mkdir_p %buildroot%__prefix/{bin,lib}

# lib
%__cp target/frontend-1.0-SNAPSHOT.jar %buildroot/%__prefix/lib
%__mkdir_p %buildroot%__prefix/lib/resources
%__cp -r src/main/resources/app/programmatic/ui/agentreport/pdf/fonts %buildroot/%__prefix/lib/resources

# bin
for s in start.sh stop.sh
do
  %__install -m 0755 utility/bin/$s %buildroot%__prefix/bin/$s
  sed -e 's#^\(project_path\).*#\1=%{__prefix}#' -i %buildroot%__prefix/bin/$s
done

# var
%__mkdir_p %buildroot%__prefix/var/log
%__mkdir_p %buildroot%__prefix/var/tmp

%pre
/usr/bin/getent group uiuser || /usr/sbin/groupadd uiuser
/usr/bin/getent passwd uiuser || /usr/sbin/useradd -m -g uiuser uiuser

%files
%defattr(-, root, root)
%dir %__prefix/
%__prefix/*
%dir %attr(-, uiuser, uiuser) %__prefix/var
%dir %attr(-, uiuser, uiuser) %__prefix/var/log
%dir %attr(-, uiuser, uiuser) %__prefix/var/tmp

%changelog
