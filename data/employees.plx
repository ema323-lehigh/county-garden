#!/usr/bin/env perl

use strict;
use warnings;

sub rand_id {
    my $numstring = "";
    for (my $i = 0; $i < 6; $i++) {
        $numstring .= int(rand(10));
    }
    return $numstring;
}

open(NAMES, '<', "names.txt") or die $!;
$/ = undef; # slurp to the end
my @lines = split('\n', <NAMES>);
my @fnames = split(',', $lines[0]);
my @lnames = split(',', $lines[1]);
my %records = ();
for (my $i = 0; $i < 12; $i++) {
    $records{&rand_id} = join(' ', $fnames[rand(@fnames)], $lnames[rand(@lnames)]);
}

open(EMPLOYEES, '>', "employees.txt") or die $!;
foreach my $key (keys %records) {
    print EMPLOYEES "INSERT INTO employees VALUES ($key, '$records{$key}');\n";
}
