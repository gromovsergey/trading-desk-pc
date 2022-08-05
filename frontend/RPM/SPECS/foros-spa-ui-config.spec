%define __spa_ui_colo ##SPA_UI_COLO##
%define __product_group foros
%define __product spa-ui-configuration-%__spa_ui_colo
%define __prefix /opt/%__product_group/spa-ui

Name:		%__product_group-%__product
Version:	1.0
Release:	ssv1%{?dist}
Summary:	Foros SPA UI Client and configuration

Group:		Applications/Internet
License:	Custom non-free
URL:		https://gitlab.ocslab.com/ui/frontend.git
Source0:	%name-%version.tar.gz	
BuildRequires:	apache-maven >= 3.3.9 java-1.8.0-devel git >= 1.7.1
Requires:	foros-spa-ui = %version nginx >= 1.8.1
BuildRoot: %{_tmppath}/%{name}-buildroot

%description
Foros SPA UI Client and configuration files

%prep

%setup -q

%build
export _USE_PROD_MODE_=`utility/build/get-app-prop-value-by-name.sh "%__spa_ui_colo" web.useProdMode`
[ -z "$_USE_PROD_MODE_" ] && echo "_USE_PROD_MODE_ is not set" >&2 && exit 1
export _JAVA_HOST_=`utility/build/get-app-prop-value-by-name.sh "%__spa_ui_colo" web.restBaseUrl`
[ -z "$_JAVA_HOST_" ] && echo "_JAVA_HOST_ is not set" >&2 && exit 1
export _EXTERNAL_CHANNEL_SOURCES_=`utility/build/get-app-prop-value-by-name.sh "%__spa_ui_colo" backend.externalChannelSources`
[ -z "$_EXTERNAL_CHANNEL_SOURCES_" ] && echo "_EXTERNAL_CHANNEL_SOURCES_ is not set" >&2 && exit 1
export _OWN_CHANNEL_SOURCE_=`utility/build/get-app-prop-value-by-name.sh "%__spa_ui_colo" backend.ownChannelSource`
[ -z "$_OWN_CHANNEL_SOURCE_" ] && echo "_OWN_CHANNEL_SOURCE_ is not set" >&2 && exit 1

# ToDo: remove this workaround for Issue #494
cp -rf /tmp/node src/main/webapp/
cp -rf /tmp/node_modules src/main/webapp/

mvn -U -Dmaven.test.skip.exec=true package -PrebuildAngular

%install
%__rm -rf %buildroot
%__mkdir_p %buildroot%__prefix/{etc,var}

# etc
%__mkdir_p %buildroot%__prefix/etc/spa-ui
%__cp src/main/resources/application.properties %buildroot/%__prefix/etc/spa-ui
%__cp conf/application-%__spa_ui_colo.properties %buildroot/%__prefix/etc/spa-ui

# var
%__mkdir_p %buildroot%__prefix/var/www
%__cp -r utility/promo-site %buildroot%__prefix/var/www

%__mkdir_p %buildroot%__prefix/var/www/spa-ui-client
%__cp -r target/classes/static/`cat target/classes/static/.base`/* %buildroot%__prefix/var/www/spa-ui-client

# NGINX
%__cp -r utility/nginx/etc/nginx %buildroot%__prefix/etc

%__mkdir_p %buildroot%__prefix/var/nginx/tmp/client_body
%__mkdir_p %buildroot%__prefix/var/nginx/tmp/fastcgi
%__mkdir_p %buildroot%__prefix/var/nginx/tmp/proxy
%__mkdir_p %buildroot%__prefix/var/nginx/tmp/scgi
%__mkdir_p %buildroot%__prefix/var/nginx/tmp/uwsgi

%post
setfacl -m user:uiuser:rw /var/log/nginx/error.log
setfacl -m user:uiuser:rw /var/log/nginx/access.log

%files
%defattr(-, root, root)
%dir %__prefix/etc/
%__prefix/etc/*
%attr(-, uiuser, uiuser) %__prefix/var/www
%attr(-, uiuser, uiuser) %__prefix/var/nginx

%changelog
