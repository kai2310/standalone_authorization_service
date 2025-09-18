%define _tmppath %{_topdir}/tmp
Name: access-service
Version: @VERSION@
Release: @RELEASE@
Summary: Rubicon Project Access Management Service
License: Rubicon Project
Distribution: Access Management Service
Vendor: the Rubicon Project
URL: http://www.rubiconproject.com/
Group: System/RP
Packager: <packager@rubiconproject.com>
BuildArchitectures: noarch
Prefix: /usr/local
BuildRoot: %{_builddir}/%{name}-root
Requires: apache-tomcat >= 7.0.42-0
Autoreq: 0
Autoprov: 0

%description
Rubicon Project Access Management Service

%files
%attr(755,prodicon,prodicon) "/usr/local/tomcat7/webapps/access.war"
%attr(755,prodicon,prodicon) "/etc/trp/*"
%attr(755,prodicon,prodicon) "/var/log/application"

%pre
echo "installing now"
echo "Stopping service..."
/sbin/service tomcat7 stop
rm -rf /usr/local/tomcat7/webapps/*
rm -rf /usr/local/tomcat7/work/Catalina/localhost/*

%post

%preun

%postun
