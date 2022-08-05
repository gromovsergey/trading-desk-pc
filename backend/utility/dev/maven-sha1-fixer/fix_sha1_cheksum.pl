#!/usr/bin/perl

my $usage =
  "Usage: $0 [params]\n".
  "  params:\n".
  "    -h (--help, h, help):  this message\n".
  "Description:\n".
  "  $0 scans current dir and all subdirs for\n".
  "  *.jar, *.pod and *.xml files; checks up accordant\n".
  "  sha1 checksums and replaces accordant *.sha1 files\n".
  "  if these files contain incorrect sha1 checksums (\n".
  "  or creates these files if they don't exist).\n\n";

if (@ARGV > 0)
{
  if($ARGV[0] ne 'h' &&
     $ARGV[0] ne '-h' &&
     $ARGV[0] ne '--help' &&
     $ARGV[0] ne 'help')
  {
    die "Error: incorrect argument '$ARGV[0]'\n\n$usage";
  }
  die $usage;
}

fix_files("jar");
fix_files("pom");
fix_files("xml");

exit 0;


sub fix_files
{
  my $file_ext = @_[0];

  #filtering files by type and extension
  my @all_needed_files = `find * -type f | grep -v "\.sha1" | grep "\.$file_ext"`;

  foreach $filename (@all_needed_files)
  {
    #got rid of '\n' at the end of filename
    if ($filename =~ /([\w|\W]*?)\n/)
    {
      $filename = $1;
    }

    #generating new checksum
    my $new_sha1_sequence = `openssl sha1 $filename`;
     if ($new_sha1_sequence =~ /$filename[() \t=;.,]*([\d\w]*)\n?/)
    {
      `echo -n $1 1>$filename.sha1._buf_`;
    }

    #comparing cheksums
    my $current_checksum = `cat $filename.sha1`;
    my $new_checksum = `cat $filename.sha1._buf_`;
    if ($new_checksum eq '')
    {
      die "Error: can't calculate checksum of '$filename'";
    }
    elsif ($current_checksum eq '' || $current_checksum ne $new_checksum)
    {
      #replacing incorrect checksum
      `cp $filename.sha1._buf_ $filename.sha1`;

      if ($?)
      {
        die "Error: can't replace incorrect checksum for '$filename'";
      }

      print "Checksum for $filename is changed\n";
    }

    `rm $filename.sha1._buf_`;
  }
}
