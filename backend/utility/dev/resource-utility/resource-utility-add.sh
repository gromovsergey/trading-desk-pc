# Adding the translated resources to the trunk
java -jar ./target/resource-utility.jar add \
  dir=../../../ui-ejb/src/main/resources/L10n \
  add-dir=/home/vitaliy_knyazev/downloads/patches/pt_resources \
  out=../../../ui-ejb/src/main/resources/L10n \
  lang=pt

# Adding the translated resources to a branch
java -jar ./target/resource-utility.jar add \
  dir=../../../../branches/2.5.0/ui-ejb/src/main/resources/L10n \
  add-dir=/home/vitaliy_knyazev/downloads/patches/pt_resources \
  out=../../../../branches/2.5.0/ui-ejb/src/main/resources/L10n \
  lang=pt
