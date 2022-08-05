#!/bin/bash
run_from=$1
useCustomDb=$2

bash -c 'echo -e \\n[foros-oracle11]\\nname=foros/oracle11g\\nbaseurl=http://repo/repos/RedHat6-extra/RPMS.oracle11g\\nenabled=1 >> /etc/yum.repos.d/foros.repo'
bash -c "echo 'search ocslab.com' >> /etc/resolv.conf"
yum -y install --nogpgcheck maven2 subversion

cp /vagrant/maint.key .ssh/
chown vagrant:vagrant .ssh/maint.key
chmod 600 .ssh/maint.key

SVN_SSH="ssh -o StrictHostKeyChecking=no -i .ssh/maint.key"
export SVN_SSH

version=`cat /project/cms-plugin/UIConfigDescriptor.xml | grep version | grep -v xml | cut -d '"' -f 2`
if [ version != "trunk" ]
then
  version=`echo $version | sed s'/\.[0-9]*$//'`
fi

emergency_version=`svn list svn+ssh://maint@svn/home/svnroot/oix/ui/branches/ | grep -v mpt | grep -v dev | sed s'/.$//'| tail -2 | head -1 | sort --version-sort`
test_version=`svn list svn+ssh://maint@svn/home/svnroot/oix/ui/branches/ | grep -v mpt | grep -v dev | sed s'/.$//'| tail -1 | sort --version-sort`

if [ "$version" == "$emergency_version" ]
then
  colo_suffix="emergency"
elif [ "$version" == "$test_version" ]
then
  colo_suffix="test"
elif [ "$version" == "trunk" ]
then
  colo_suffix="trunk"
else
  echo "Not supported version $version, should be trunk or $test_version or $emergency_version"
  exit 1
fi

echo "Identified version $version"

if [ "$version" == "trunk" ] 
then
  forosuipackage=`ssh -o StrictHostKeyChecking=no -i .ssh/maint.key maint@buildbox "cd /tmp/OUI-COMMON/repo/nb/ui/ && ls foros-ui-20* | sort | tail -1"`
  scp -i .ssh/maint.key maint@buildbox:/tmp/OUI-COMMON/repo/nb/ui/$forosuipackage .
else
  forosuipackage=`yum --showduplicates list foros-ui | grep $version | sed -r 's/.+('$version'.[0-9]+).+/foros-ui-\1/' | tail -1`
fi

yum -y install --nogpgcheck perl-CGI # workaround ENVDEV-9532
yum -y install --nogpgcheck $forosuipackage foros-creatives-vagrant-$colo_suffix foros-config-ui-vagrant-$colo_suffix foros-config-ui-vagrant-$colo_suffix-mgr
rm -f $forosuipackage

yum -y install --nogpgcheck libNLPIR libNLPIR-devel libNLPIR-debuginfo

cp /opt/foros/ui/bin/run-migrations.py /opt/foros/ui/bin/run-migrations.py.bak
sh -c 'echo -e "#!/bin/bash \\necho \"automigration is skipped\" >&2" > /opt/foros/ui/bin/run-migrations.py'

/etc/rc.d/init.d/syslog-ng start

chmod +x /vagrant/customize_and_restart.sh
/vagrant/customize_and_restart.sh $run_from $useCustomDb
