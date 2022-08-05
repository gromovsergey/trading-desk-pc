echo ; echo "For all reports find their template file name"
for afile in `find /opt/foros/ui/share/reports/ -name *.rptdesign` ; do
  found="no"
  for bfile in `find /opt/foros/ui/var/www/fmroot/reports/ -name *.rptdesign` ; do
    cmp -s $afile $bfile
    if [ "$?" = "0" ] ; then
      echo $afile" : "$bfile
      found="yes"
    fi
  done
  if [ "$found" = "no" ] ; then
    echo $afile" : not found"
  fi
done

echo ; echo "Find templates which doesn't related to reports"
for bfile in `find /opt/foros/ui/var/www/fmroot/reports/ -name *.rptdesign` ; do
  found="no"
  for afile in `find /opt/foros/ui/share/reports/ -name *.rptdesign` ; do
    cmp -s $afile $bfile
    if [ "$?" = "0" ] ; then
      found="yes"
    fi
  done
  if [ "$found" = "no" ] ; then
    echo $bfile" : doesn't related to any report"
  fi
done

